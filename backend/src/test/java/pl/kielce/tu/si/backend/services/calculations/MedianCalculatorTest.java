package pl.kielce.tu.si.backend.services.calculations;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedianCalculatorTest {
    MedianCalculator calculator = new MedianCalculator(null);

    @Test
    void shouldCalculateQuantileForOddNumberOfElements() {
        List<Double> values = new ArrayList<>(List.of(2d, 4d, 5d, 7d, 8d, 10d, 11d, 13d, 16d));

        Map<String, Double> result = calculator.calculate(values);

        assertEquals(8, result.get("median"));
    }

    @Test
    void shouldCalculateQuantileForEvenNumberOfElements() {
        List<Double> values = new ArrayList<>(List.of(1d, 3d, 5d, 8d, 9d, 10d, 11d, 14d, 15d, 17d));

        Map<String, Double> result = calculator.calculate(values);

        assertEquals(9.5, result.get("median"));
    }
}
