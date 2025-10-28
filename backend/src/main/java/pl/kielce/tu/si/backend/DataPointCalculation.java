package pl.kielce.tu.si.backend;

import java.util.List;

public record DataPointCalculation(
        String id,
        List<CalculationValue> values
) {
    public record CalculationValue(
            String name,
            Double value
    ){}
}
