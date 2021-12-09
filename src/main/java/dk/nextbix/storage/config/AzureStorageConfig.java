package dk.nextbix.storage.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.spring.autoconfigure.storage.StorageProperties;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(StorageProperties.class)
public class AzureStorageConfig {
    private final StorageProperties storageProperties;

    @Autowired
    public AzureStorageConfig(final StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

//    @Bean
//    @Primary
//    @Profile("!localdev")
//    public BlobServiceClientBuilder cloudBlobServiceClientBuilder() {
//        return new BlobServiceClientBuilder()
//                .endpoint(storageProperties.getBlobEndpoint())
//                .credential(new DefaultAzureCredentialBuilder().build());
//    }

    @Bean
    @Primary
    //@Profile("localdev")
    public BlobServiceClientBuilder localBlobServiceClientBuilder() {
        return new BlobServiceClientBuilder()
                .endpoint(storageProperties.getBlobEndpoint())
                .credential(new StorageSharedKeyCredential(storageProperties.getAccountName(), storageProperties.getAccountKey()));
    }

}
