package commands;

import fileio.CommandInput;

public class CommandFactory {
    public Command createCommand(final CommandInput commandInput) {
        switch (commandInput.getCommand()) {
            case "reportTicket":
                return new ReportTicketCmd(commandInput);
            case "lostInvestors":
                return new LostInvestorsCmd(commandInput);
            case "viewTickets":
                return new ViewTicketsCmd(commandInput);
            case "createMilestone":
                return new CreateMilestoneCmd(commandInput);
            case "viewMilestones":
                return new ViewMilestonesCmd(commandInput);
            case "assignTicket":
                return new AssignTicketCmd(commandInput);
            case "viewAssignedTickets":
                return new ViewAssignedTicketsCmd(commandInput);
            case "undoAssignTicket":
                return new UndoAssignTicketCmd(commandInput);
            case "addComment":
                return new AddCommentCmd(commandInput);
            case "undoAddComment":
                return new UndoAddCommentCmd(commandInput);
            case "changeStatus":
                return new ChangeStatusCmd(commandInput);
            case "undoChangeStatus":
                return new UndoChangeStatusCmd(commandInput);
            case "viewTicketHistory":
                return new ViewTicketHistoryCmd(commandInput);
            case "viewNotifications":
                return new ViewNotificationsCmd(commandInput);
            case "search":
                return new SearchCmd(commandInput);
            default:
                throw new IllegalArgumentException();
        }
    }
}
