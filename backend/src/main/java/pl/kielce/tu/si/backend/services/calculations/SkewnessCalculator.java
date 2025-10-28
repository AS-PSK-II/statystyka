package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkewnessCalculator extends BaseCalculator {

    public SkewnessCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        int numberOfElements = values.size();
        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);

        double sumOfSquares = values.stream().mapToDouble(value -> Math.pow(value - avg, 2)).sum();
        double std = Math.sqrt(sumOfSquares / (numberOfElements - 1));

        if (std == 0) {
            result.put("skewness", 0.0);
            return result;
        }

        double sumOfCubes = values.stream().mapToDouble(value -> Math.pow(value - avg, 3)).sum();
        double skewness = sumOfCubes / ((numberOfElements - 1) * Math.pow(std, 3));
        result.put("skewness", skewness);

        return result;
    }
}
