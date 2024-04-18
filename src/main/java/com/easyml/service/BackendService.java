package com.easyml.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


@Service
public class BackendService {
    public int[] readCSV(MultipartFile csv) {
        int[] dims = {0, 0};
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csv.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int rowCount = 0;
            int columnCount = 0;
            for (CSVRecord record : csvParser) {
                rowCount++;
                if (rowCount == 1) {
                    columnCount = record.size();
                }
            }
            csvParser.close();
            if(rowCount >= 3 && columnCount >= 3) {
                dims[0] = rowCount;
                dims[1] = columnCount;
            }
            return dims;
        } catch (IOException e) {
            return dims;
        }
    }

}
