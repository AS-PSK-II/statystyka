package pl.kielce.tu.si.backend.services.calculations;

import java.util.List;
import java.util.Map;

public interface Calculator {

    Map<String, Double> calculate(List<Double> values);
}
