package pl.kielce.tu.si.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.kielce.tu.si.backend.DataPoint;
import pl.kielce.tu.si.backend.RawDataPoint;
import pl.kielce.tu.si.backend.repositories.DataPointRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetRawDataUseCase {

    private final DataPointRepository dataPointRepository;

    public List<RawDataPoint> getRawData() {
        List<DataPoint> dataPoints = dataPointRepository.getAllDataPoints();

        return processDataPoints(dataPoints)
                .entrySet()
                .stream()
                .map(entry -> new RawDataPoint(entry.getKey(), entry.getValue()))
                .toList();
    }

    private Map<String, List<RawDataPoint.DataPointValue>> processDataPoints(List<DataPoint> dataPoints) {
        Map<String, List<RawDataPoint.DataPointValue>> result = new HashMap<>();

        dataPoints.forEach(dataPoint -> {
            if (!result.containsKey(dataPoint.timestamp())) {
                result.put(dataPoint.timestamp(), new ArrayList<>());
            }

            result.get(dataPoint.timestamp()).add(new RawDataPoint.DataPointValue(dataPoint.id(), dataPoint.value()));
        });

        return result;
    }
}
