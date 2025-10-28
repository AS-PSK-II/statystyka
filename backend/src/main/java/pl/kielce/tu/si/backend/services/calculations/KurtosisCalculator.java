package pl.kielce.tu.si.backend.services.calculations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KurtosisCalculator extends BaseCalculator {

    public KurtosisCalculator(Calculator nextCalculator) {
        super(nextCalculator);
    }

    @Override
    public Map<String, Double> calculate(List<Double> values) {
        Map<String, Double> result = nextCalculator != null ? nextCalculator.calculate(values) : new HashMap<>();

        int numberOfElements = values.size();
        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);

        double sumOfSquares = values.stream().mapToDouble(value -> Math.pow(value - avg, 2)).sum();
        double std = Math.sqrt(sumOfSquares / (numberOfElements - 1));

        double sumOfForPower = values.stream().mapToDouble(value -> Math.pow(value - avg, 4)).sum();
        double numerator = numberOfElements* (numberOfElements+ 1) * sumOfForPower;
        double denominator = (numberOfElements- 1) * (numberOfElements- 2) * (numberOfElements- 3) * Math.pow(std, 4);
        double correction= 3 * Math.pow(numberOfElements - 1d, 2) / ((numberOfElements- 2) * (numberOfElements- 3));

        double kurtosis = numerator / denominator - correction;

        result.put("kurtosis", kurtosis);

        return result;
    }
}
