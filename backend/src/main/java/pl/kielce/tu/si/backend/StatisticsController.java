package pl.kielce.tu.si.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kielce.tu.si.backend.data.ExcelDataExtractor;
import pl.kielce.tu.si.backend.services.CalculateDataUseCase;
import pl.kielce.tu.si.backend.services.GetRawDataUseCase;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {

    private final ExcelDataExtractor excelDataExtractor;
    private final GetRawDataUseCase getRawDataUseCase;
    private final CalculateDataUseCase calculateDataUseCase;

    @PostMapping
    public void uploadFile(@RequestPart MultipartFile file) throws IOException {
        List<DataPoint> extractedData = excelDataExtractor.extractData(file.getInputStream());
        log.info("File uploaded: {}", extractedData);
    }

    @GetMapping
    public ResponseEntity<List<DataPointCalculation>> getStatistics() {
        return ResponseEntity.ok(calculateDataUseCase.calculateData());
    }

    @GetMapping("/raw")
    public ResponseEntity<List<RawDataPoint>> getRawStatistics() {
        return ResponseEntity.ok(getRawDataUseCase.getRawData());
    }
}
