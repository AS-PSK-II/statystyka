package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedianCalculator extends BaseCalculator {

    public MedianCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        double median = 0;
        values.sort(Double::compareTo);
        if (values.size() % 2 == 0) {
            double first = values.get(getIndex(values.size()) - 1);
            double second = values.get(getIndex(values.size()));
            median = (first + second) / 2;
        } else {
            median = values.get(getIndex(values.size()));
        }
        result.put("median", median);

        return result;
    }

    private int getIndex(int size) {
        return size / 2;
    }
}
