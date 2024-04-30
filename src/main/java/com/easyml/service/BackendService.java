package com.easyml.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackendService {

    private int[] dims;
    private List<String> columns;
    private List<List<String>> rows;

    public int[] getDims() {
        return dims;
    }

    public void setDims(int[] dims) {
        this.dims = dims;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    public void readCSV(MultipartFile csv, String csvUrl) throws IOException {
        BufferedReader reader = null;
        if (csv != null) {
            reader = new BufferedReader(new InputStreamReader(csv.getInputStream()));
        }
        if (csvUrl != null) {
            URL url = new URL(csvUrl);
                reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        }
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        List<List<String>> rows = new ArrayList<>();
        int rowCount = 0;
        int columnCount = 0;
        for (CSVRecord record : csvParser) {
            rows.add(record.toList());
            rowCount++;
            if (rowCount == 1) {
                columnCount = record.size();
                rows.remove(record.toList());
                setColumns(record.toList());
            }
        }
        csvParser.close();
        setRows(rows);
        setDims(new int[]{rowCount, columnCount});

    }
}
