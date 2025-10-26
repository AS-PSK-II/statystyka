package pl.kielce.tu.si.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/statistics")
@Slf4j
public class StatisticsController {

    @PostMapping
    public void uploadFile(@RequestPart MultipartFile file) {
        log.info("File uploaded: {}", file.getOriginalFilename());
    }

    @GetMapping
    public String getStatistics() {
        return "Statistics";
    }

    @GetMapping("/raw")
    public String getRawStatistics() {
        return "Raw statistics";
    }
}
