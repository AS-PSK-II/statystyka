package pl.kielce.tu.si.backend;

public record DataPoint(
        String id,
        String timestamp,
        double value
) {
}
