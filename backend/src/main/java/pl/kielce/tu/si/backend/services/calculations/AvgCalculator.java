package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvgCalculator extends BaseCalculator {

    public AvgCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        result.put("avg", avg);

        return result;
    }
}
