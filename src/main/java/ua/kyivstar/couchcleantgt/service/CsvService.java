package ua.kyivstar.couchcleantgt.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CsvService {

    public void generateCSV(int numberOfLines, int startId, String csvPath) {
        StringBuilder builder = new StringBuilder();

        IntStream.range(startId, startId + numberOfLines)
                .forEach(id -> builder.append(id).append(System.lineSeparator()));

        File csvFile = new File(csvPath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(csvFile);
            fileWriter.write(builder.toString());
            fileWriter.flush();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> readFromCSV(String csvPath) {
        List<String> result = new ArrayList<>();

        try {
            File file = new File(csvPath);
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            result = bufferedReader.lines()
                    .collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
