package pl.kielce.tu.si.backend.services.calculations;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinCalculatorTest {

    MinCalculator calculator = new MinCalculator(null);

    @Test
    void shouldCalculateMin() {
        List<Double> values = new ArrayList<>(List.of(2d, 4d, 5d, 7d, 8d, 10d, 11d, 13d, 16d, -10d));

        Map<String, Double> result = calculator.calculate(values);

        assertEquals(-10, result.get("min"));
    }
}
