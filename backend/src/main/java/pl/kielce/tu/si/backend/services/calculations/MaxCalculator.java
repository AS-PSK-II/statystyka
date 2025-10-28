package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxCalculator extends BaseCalculator {

    public MaxCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        double max = values.stream().max(Double::compareTo).orElse(Double.MIN_VALUE);
        result.put("max", max);

        return result;
    }
}
