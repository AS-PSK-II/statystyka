package pl.kielce.tu.si.backend.data;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.kielce.tu.si.backend.DataPoint;
import pl.kielce.tu.si.backend.config.CacheConfig;

import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class ExcelDataExtractor {

    @Cacheable(value = CacheConfig.DATA_POINTS_CACHE, key = "'" + CacheConfig.DATA_POINTS_CACHE_KEY + "'")
    public List<DataPoint> extractData(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Map<Integer, String> headers = getHeaders(rowIterator.next());

            return getDataPoints(rowIterator, headers);
        } catch (Exception e) {
            log.error("Cannot read excel file", e);
            throw new IllegalArgumentException(e);
        }
    }

    private Map<Integer, String> getHeaders(Row row) {
        Map<Integer, String> headers = new HashMap<>();

        Iterator<Cell> cellIterator = row.cellIterator();

        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            headers.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
        }

        return headers;
    }

    private List<DataPoint> getDataPoints(Iterator<Row> rowIterator, Map<Integer, String> headers) {
        List<DataPoint> dataPoints = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();

            String timestamp = "";
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int cellIndex = currentCell.getColumnIndex();
                if (cellIndex == 0) {
                    timestamp = currentCell.getStringCellValue();
                } else {
                    dataPoints.add(new DataPoint(
                            headers.get(cellIndex),
                            timestamp,
                            currentCell.getNumericCellValue()
                    ));
                }
            }
        }

        return dataPoints;
    }
}
