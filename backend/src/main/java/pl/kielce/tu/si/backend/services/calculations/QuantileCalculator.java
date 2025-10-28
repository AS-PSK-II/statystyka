package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantileCalculator extends BaseCalculator {

    public QuantileCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        values.sort(Double::compareTo);

        int numberOfElements = values.size();
        int secondQuantileIndex = getIndex(numberOfElements);
        double secondQuantile = getQuantile(values, secondQuantileIndex, numberOfElements);

        List<Double> firstHalf = values.subList(0, secondQuantileIndex);
        int firstQuantileIndex = getIndex(firstHalf.size());
        double firstQuantile = getQuantile(firstHalf, firstQuantileIndex, firstHalf.size());

        List<Double> secondHalf = values.subList(numberOfElements % 2 == 0 ? secondQuantileIndex : secondQuantileIndex + 1, numberOfElements);
        int thirdQuantileIndex = getIndex(secondHalf.size());
        double thirdQuantile = getQuantile(secondHalf, thirdQuantileIndex, secondHalf.size());

        result.put("firstQuantile", firstQuantile);
        result.put("secondQuantile", secondQuantile);
        result.put("thirdQuantile", thirdQuantile);

        return result;
    }

    private double getQuantile(List<Double> values, int index, int numberOfElements) {
        if (numberOfElements % 2 == 0) {
            double first = values.get(index - 1);
            double second = values.get(index);
            return (first + second) / 2;
        } else {
            return values.get(index);
        }
    }

    private int getIndex(int size) {
        return size / 2;
    }
}
