package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketStatus;
import entities.tickets.TicketType;
import entities.users.Developer;
import entities.users.Manager;
import entities.users.UserRole;
import fileio.CommandInput;
import reports.CustomerImpactReport;
import reports.TicketReportVisitor;
import reports.performance.DevStatistics;
import reports.performance.PerformanceReportStrategy;
import reports.performance.PerformanceStrategyFactory;
import utils.CmdCommonOutput;
import utils.MathUtils;
import utils.PerformanceTicketOutput;
import utils.ReportTicketOutput;

import java.time.LocalDate;
import java.util.*;

public class GeneratePerformanceReportCmd implements Command{
    private CommandInput cmdInput;

    public GeneratePerformanceReportCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        Manager manager = (Manager) database.findUser(cmdInput.getUsername());
        List<DevStatistics> devMetrics = new ArrayList<>();

        for (String devName : manager.getSubordinates()) {
            Developer dev = (Developer) database.findUser(devName);
            DevStatistics devStats = new DevStatistics();
            devStats.saveStats(dev, LocalDate.parse(cmdInput.getTimestamp()));
            devMetrics.add(devStats);

            if (dev.isHasClosedTickets()) {
                PerformanceStrategyFactory factory = new PerformanceStrategyFactory(dev.getSeniority());
                PerformanceReportStrategy reportStrategy = factory.createStrategy();
                double performanceScore = reportStrategy.calculatePerformance(devStats);
                dev.setPerformanceScore(performanceScore);
            }
        }

        devMetrics.sort(new Comparator<DevStatistics>() {
            @Override
            public int compare(final DevStatistics o1, final DevStatistics o2) {
               return o1.getDev().getUsername().compareTo(o2.getDev().getUsername());
            }
        });

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        objNode.set("report", PerformanceTicketOutput.toJson(devMetrics));
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
