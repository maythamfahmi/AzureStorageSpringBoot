package dk.nextbix.storage.actuator;

import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BlobStorageHealthIndicator implements HealthIndicator {
    private static final String URL_FIELD = "URL";
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(10);
    private static final Status NOT_CONFIGURED_STATUS = new Status("Not configured");
    private final BlobServiceAsyncClient internalClient;

    @Autowired
    public BlobStorageHealthIndicator(final BlobServiceClientBuilder blobServiceClientBuilder) {
        this.internalClient = blobServiceClientBuilder.buildAsyncClient();
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = new Health.Builder();

        try {
            if (internalClient == null) {
                healthBuilder.status(NOT_CONFIGURED_STATUS);
            } else {
                healthBuilder.withDetail(URL_FIELD, internalClient.getAccountUrl());
                healthBuilder.withDetail("AccountName", internalClient.getAccountName());
                BlobContainerItem item = internalClient.listBlobContainers().blockFirst(POLL_TIMEOUT);
                if (null != item) {
                    healthBuilder.withDetail("Container", item.getName());
                }
                healthBuilder.up();
            }
        } catch (Exception e) {
            healthBuilder.status("Could not complete health check.").down(e);
        }
        return healthBuilder.build();
    }
}



