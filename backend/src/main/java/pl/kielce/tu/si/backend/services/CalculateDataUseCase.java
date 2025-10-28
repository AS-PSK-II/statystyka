package pl.kielce.tu.si.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kielce.tu.si.backend.DataPoint;
import pl.kielce.tu.si.backend.DataPointCalculation;
import pl.kielce.tu.si.backend.repositories.DataPointRepository;
import pl.kielce.tu.si.backend.services.calculations.Calculator;
import pl.kielce.tu.si.backend.services.calculations.CalculatorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculateDataUseCase {

    private final DataPointRepository dataPointRepository;
    private final CalculatorFactory calculatorFactory;

    public List<DataPointCalculation> calculateData() {
        List<DataPointCalculation> result = new ArrayList<>();
        List<DataPoint> dataPoints = dataPointRepository.getAllDataPoints();
        Map<String, List<Double>> groupedByAttribute = groupByAttribute(dataPoints);

        Calculator calculator = calculatorFactory.getCalculator();

        Map<String, Double> calculations = calculator.calculate(groupedByAttribute.get(dataPoints.get(0).id()));

        result.add(new DataPointCalculation(dataPoints.get(0).id(), calculations.entrySet().stream().map(element -> new DataPointCalculation.CalculationValue(element.getKey(), element.getValue())).toList()));

        return result;
    }

    private Map<String, List<Double>> groupByAttribute(List<DataPoint> dataPoints) {
        Map<String, List<Double>> result = new HashMap<>();

        dataPoints.forEach(dataPoint -> {
            if (!result.containsKey(dataPoint.id())) {
                result.put(dataPoint.id(), new ArrayList<>());
            }

            result.get(dataPoint.id()).add(dataPoint.value());
        });

        return result;
    }
}
