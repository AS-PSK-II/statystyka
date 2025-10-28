package pl.kielce.tu.si.backend.services.calculations;

import org.springframework.stereotype.Service;

@Service
public class CalculatorFactory {

    public Calculator getCalculator() {
        return new MaxCalculator(new MinCalculator(new AvgCalculator(new MedianCalculator(new StandardDeviationCalculator(new DominantCalculator(new MinMaxDiffCalculator(new SkewnessCalculator(new QuantileCalculator(new KurtosisCalculator(null))))))))));
    }
}
