package pl.kielce.tu.si.backend.services.calculations;

public abstract class BaseCalculator implements Calculator {

    protected Calculator nextCalculator;

    protected BaseCalculator(Calculator nextCalculator) {
        this.nextCalculator = nextCalculator;
    }
}
