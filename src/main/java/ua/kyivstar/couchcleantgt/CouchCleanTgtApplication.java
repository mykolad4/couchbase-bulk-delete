package ua.kyivstar.couchcleantgt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.kyivstar.couchcleantgt.service.CouchbaseService;
import ua.kyivstar.couchcleantgt.service.CsvService;

@SpringBootApplication
public class CouchCleanTgtApplication implements CommandLineRunner {

    @Autowired
    private CsvService csvService;

    @Autowired
    private CouchbaseService couchService;

    public static void main(String[] args) {
        SpringApplication.run(CouchCleanTgtApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if ("generate-csv".equals(args[0])) {
            csvService.generateCSV(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
        } else if ("insert".equals(args[0])) {
            couchService.fillBuckets(args[1]);
        } else if ("delete".equals(args[0])) {
            couchService.cleanTgt(args[1]);
        } else {
            System.out.println("First argument must be generate or delete");
        }
    }
}
