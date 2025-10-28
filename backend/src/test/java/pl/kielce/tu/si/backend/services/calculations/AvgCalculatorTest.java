package pl.kielce.tu.si.backend.services.calculations;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvgCalculatorTest {

    AvgCalculator calculator = new AvgCalculator(null);

    @Test
    void shouldCalculateAvg() {
        List<Double> values = new ArrayList<>(List.of(2d, 4d, 5d, 7d, 8d, 10d, 11d, 13d, 16d));

        Map<String, Double> result = calculator.calculate(values);

        assertEquals(844, (int) (result.get("avg") * 100));
    }
}
