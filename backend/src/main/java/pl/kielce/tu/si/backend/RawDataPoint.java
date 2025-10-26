package pl.kielce.tu.si.backend;

import java.util.List;

public record RawDataPoint(
        String timestamp,
        List<DataPointValue> dataPoints
) {
    public record DataPointValue(
            String id,
            double value
    ) {}
}
