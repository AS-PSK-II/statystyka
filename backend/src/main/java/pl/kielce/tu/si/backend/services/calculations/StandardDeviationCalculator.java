package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardDeviationCalculator extends BaseCalculator {

    public StandardDeviationCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        double sumOfSquares = values.stream().mapToDouble(value -> Math.pow(value - avg, 2)).sum();
        double std = Math.sqrt(sumOfSquares / (values.size() - 1));
        result.put("std", std);

        return result;
    }
}
