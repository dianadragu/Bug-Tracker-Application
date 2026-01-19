package utils;

import java.util.List;

public class MathUtils {
    public static Double roundResult(Double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    public static double calculateImpactFinal(double baseScore, double maxValue) {
        return Math.min(100.0, (baseScore * 100.0) / maxValue);
    }

    public static double calculateAverageImpact(List<Double> scores) {
        return scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
