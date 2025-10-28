package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DominantCalculator extends BaseCalculator {

    public DominantCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        Map<Double, Integer> frequencyMap = new HashMap<>();
        values.forEach(value -> frequencyMap.merge(value, 1, Integer::sum));

        double dominant = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Double.NaN);
        result.put("dominant", dominant);

        return result;
    }
}
