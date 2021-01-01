package ua.kyivstar.couchcleantgt.service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.SerializableDocument;
import com.couchbase.client.java.document.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.stereotype.Service;
import ua.kyivstar.cas.ticket.registry.AccountTgtBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouchbaseService {

    @Qualifier(BeanNames.COUCHBASE_BUCKET)
    private final Bucket tgtBucket;

    @Qualifier("accountBucket")
    private final Bucket accountBucket;

    @Qualifier("fixedThreadPool")
    private final ExecutorService executorService;

    private final CsvService csvService;

    // Fill couchbase with test data
    public void fillBuckets(String csvPath) {
        List<String> accountIds = csvService.readFromCSV(csvPath);
        createBulkTgt(accountIds, tgtBucket);
        createBulkAccount(accountIds, accountBucket);
    }

    public void cleanTgt(String csvPath) {
        List<String> accountIds = csvService.readFromCSV(csvPath);
        log.info("All ids from csv file have been read");

        List<AccountTgtBox> accountTgtBoxes = readAccountTgtBoxes(accountIds);
        log.info("{}} AccountTgtBoxes have been read", accountTgtBoxes.size());

        List<String> tgtIds = accountTgtBoxes.stream()
                .flatMap(accountTgtBox -> accountTgtBox.getTgtList().stream())
                .collect(Collectors.toList());
        log.info("{} TGT tickets are going to be deleted", tgtIds.size());

        accountIds = accountTgtBoxes.stream()
                .map(AccountTgtBox::getAccountId)
                .collect(Collectors.toList());

        deleteDocuments(tgtBucket, tgtIds);
        log.info("Removing tickets from tgtdefault bucket finished");
        deleteDocuments(accountBucket, accountIds);
        log.info("Removing tickets from accmap bucket finished");
    }

    private List<AccountTgtBox> readAccountTgtBoxes(Iterable<String> ids) {
        List<AccountTgtBox> accountTgtBoxes = new ArrayList<>();
        ids.forEach(id -> {
            try {
                SerializableDocument document = accountBucket.get(id, SerializableDocument.class);
                accountTgtBoxes.add((AccountTgtBox) document.content());
            } catch (Exception e) {
                log.error("There is no such account id into accmap bucket");
            }
        });
        return accountTgtBoxes;
    }

    private void deleteDocuments(Bucket bucket, Iterable<String> ids) {
        ids.forEach(id -> {
            try {
                bucket.remove(id);
            } catch (Exception e) {
                log.error("Could not delete id {} because of error {}", id, e.getMessage());
            }
        });
    }

    // Insert test data into tgtdefault bucket
    // data structure: key: "s" + id, value: any object
    private void createBulkTgt(Iterable<String> items, Bucket tgtBucket) {
        items.forEach(item -> executorService.submit(() -> {
            JsonObject obj = JsonObject.create().put("test", "test");
            try {
                tgtBucket.insert(JsonDocument.create("s" + item, obj));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }));
    }

    // Insert test data into accmap bucket
    // any document contains list of tgt ids that are mapped to account
    // data structure: key: id, value: accountId = id, thtList = List.of("s" + id...)
    private void createBulkAccount(Iterable<String> items, Bucket accountBucket) {
        items.forEach(item -> executorService.submit(() -> {
            try {
                accountBucket.insert(
                        SerializableDocument.create(item, AccountTgtBox.builder()
                                .accountId(item)
                                .tgtList(Collections.singletonList("s" + item))
                                .build()));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }));
    }
}
