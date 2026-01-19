package utils;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DatesManagement {
    public static int getDaysPassed(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    public static boolean isDueDateTomorrow(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) == 1;
    }

    public static boolean isLastMonth(LocalDate dateToCheck, LocalDate currentDate) {
        LocalDate lastDate = currentDate.minusMonths(1);

        return dateToCheck.getMonth().equals(dateToCheck.getMonth());
    }
}
