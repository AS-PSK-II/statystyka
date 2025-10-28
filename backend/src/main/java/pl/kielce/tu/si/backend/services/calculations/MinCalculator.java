package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinCalculator extends BaseCalculator {

    public MinCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        double min = values.stream().min(Double::compareTo).orElse(Double.MAX_VALUE);
        result.put("min", min);

        return result;
    }
}
