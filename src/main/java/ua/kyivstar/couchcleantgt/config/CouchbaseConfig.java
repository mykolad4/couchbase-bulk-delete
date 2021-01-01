package ua.kyivstar.couchcleantgt.config;

import com.couchbase.client.java.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    @Value("${spring.couchbase.bootstrap-hosts}")
    private String[] nodes;
    @Value("${spring.couchbase.bucket.name}")
    private String tgtBucketName;
    @Value("${spring.couchbase.bucket.password}")
    private String tgtBucketPassword;
    @Value("${spring.couchbase.accmap-bucket.name}")
    private String commonBucketName;
    @Value("${spring.couchbase.accmap-bucket.password}")
    private String commonBucketPassword;

    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList(nodes);
    }

    @Override
    protected String getBucketName() {
        return tgtBucketName;
    }

    @Override
    protected String getBucketPassword() {
        return tgtBucketPassword;
    }

    @Bean
    public Bucket accountBucket() throws Exception {
        return couchbaseCluster().openBucket(commonBucketName, commonBucketPassword);
    }

    @Bean(name = "accountTemplate")
    public CouchbaseTemplate accountTemplate() throws Exception {
        CouchbaseTemplate template = new CouchbaseTemplate(couchbaseClusterInfo(), accountBucket(), mappingCouchbaseConverter(), translationService());
        template.setDefaultConsistency(getDefaultConsistency());
        return template;
    }

    @Bean(name = "tgtTemplate")
    public CouchbaseTemplate tgtTemplate() throws Exception {
        CouchbaseTemplate template = new CouchbaseTemplate(couchbaseClusterInfo(), couchbaseClient(), mappingCouchbaseConverter(), translationService());
        template.setDefaultConsistency(getDefaultConsistency());
        return template;
    }

    @Bean("fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(5);
    }
}