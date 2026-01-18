package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import entities.users.Developer;
import entities.users.Manager;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import search.FilterFactory;
import search.SearchContext;
import search.filters.SearchStrategy;
import utils.CmdCommonOutput;
import utils.DevFilterOutput;
import utils.FilterTicketOutput;
import utils.StandardTicketOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchCmd implements Command {
    private CommandInput cmdInput;

    public SearchCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        User user = database.findUser(cmdInput.getUsername());
        FilterFactory filterFactory = new FilterFactory(cmdInput.getFilters());
        String searchType = null;

        if (cmdInput.getFilters() != null) {
            searchType = cmdInput.getFilters().getSearchType();
        }
        if (searchType == null || searchType.equals("TICKET")) {
            List<Ticket> tickets = new ArrayList<>();
            switch (user.getRole()) {
                case MANAGER:
                    tickets.addAll(database.getCreatedTickets());
                    break;
                case DEVELOPER:
                    Developer developer = (Developer) user;
                    for (Milestone milestone : developer.getMilestones()) {
                        for (Ticket ticket : milestone.getAssignedTickets()) {
                            if (ticket.getStatus() == TicketStatus.OPEN) {
                                tickets.add(ticket);
                            }
                        }
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            if (searchType != null) {
                List<SearchStrategy<Ticket>> strategies = filterFactory.getStrategiesForTicket(user);
                SearchContext<Ticket> searchContext = new SearchContext<Ticket>(tickets, strategies);
                List<Ticket> filteredTickets = searchContext.applyFilters();
                List<String> matchedKeywords = null;
                if (cmdInput.getFilters().getKeywords() != null) {
                    matchedKeywords = matchedWords(filteredTickets, cmdInput.getFilters().getKeywords());
                }
                return generateTicketOutput(filteredTickets, true, matchedKeywords);
            } else {
                return generateTicketOutput(tickets, false, null);
            }

        } else {
            Manager manager = (Manager) user;
            List<Developer> devs  = new ArrayList<>();
            for (String name : manager.getSubordinates()) {
                Developer developer = (Developer) database.findUser(name);
                devs.add(developer);
            }
            List<SearchStrategy<Developer>> strategies = filterFactory.getStrategiesForDeveloper();
            SearchContext<Developer> searchContext = new SearchContext<Developer>(devs, strategies);
            List<Developer> filteredDevelopers = searchContext.applyFilters();

            return generateDevOutput(filteredDevelopers);
        }

    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }

    public ObjectNode generateTicketOutput(List<Ticket> tickets, Boolean isContentFiltered, List<String> keywords) {
        tickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(final Ticket o1, final Ticket o2) {
                int result = o1.getCreatedAt().compareTo(o2.getCreatedAt());
                if (result == 0) {
                    return o1.getId() - o2.getId();
                } else {
                    return result;
                }
            }
        });

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (Ticket ticket : tickets) {
            arrayNode.add(FilterTicketOutput.toJson(ticket, keywords));
        }

        if (isContentFiltered) {
            objNode.put("searchType", "TICKET");
            objNode.set("results", arrayNode);
        } else {
            objNode.set("tickets", arrayNode);
        }
        return objNode;
    }

    public ObjectNode generateDevOutput(List<Developer> devs) {
        devs.sort(new Comparator<Developer>() {
            @Override
            public int compare(final Developer o1, final Developer o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (Developer dev : devs) {
            arrayNode.add(DevFilterOutput.toJson(dev));
        }

        objNode.put("searchType", "DEVELOPER");
        objNode.set("results", arrayNode);
        return objNode;
    }

    public List<String> matchedWords(List<Ticket> tickets, List<String> keywords) {
        List<String> matchedWords = new ArrayList<>();
        for (Ticket ticket : tickets) {
            List<String> wordsToCheck = new ArrayList<>();
            wordsToCheck.addAll(List.of(ticket.getTitle().split("[ .?!]+")));

            if(ticket.getDescription() != null) {
                wordsToCheck.addAll(List.of(ticket.getDescription().split("[ .?!]+")));
            }

            for(String word : wordsToCheck) {
                if(keywords.contains(word.toLowerCase())) {
                    matchedWords.add(word);
                }
            }
        }

        Collections.sort(matchedWords);
        return matchedWords;
    }
}
