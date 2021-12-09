package dk.nextbix.storage;


import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.assertj.core.api.WithAssertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

@Testcontainers
public class AzuriteTest implements WithAssertions {

    private static final int AZURITE_PORT = 10000;
    private static final String AZURITE_IMAGE = "mcr.microsoft.com/azure-storage/azurite";
    private static final String AccountName = "devstoreaccount1";
    private static final String AccountKey = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";
    private static final String BlobEndpoint = "http://127.0.0.1:${azuriteContainer.getMappedPort(10000)}/devstoreaccount1";
    private static final String ConnectionString = "DefaultEndpointsProtocol=$defaultEndpointsProtocol;AccountName=$accountName;AccountKey=$accountKey;BlobEndpoint=$blobEndpoint;";

    /**
     * Azurite
     */
    @ClassRule
    public static GenericContainer<?> azurite = new GenericContainer(AZURITE_IMAGE)
            .withExposedPorts(AZURITE_PORT);

    @Test
    public void isContainerRunning() {
        System.out.println(azurite.getContainerInfo().getNetworkSettings().getPorts());
        assertThat(azurite.isRunning()).isTrue();
    }

    @Test
    public void ShouldUploadAndDownloadBlob() throws Exception {
        var containerName = "container-" + UUID.randomUUID();
        var ip = azurite.getMappedPort(AZURITE_PORT);
        var endpoint = String.format("http://127.0.0.1:%d/%s", ip, AccountName);

        var credential = new StorageSharedKeyCredential(AccountName, AccountKey);
        var containerClient = new BlobContainerClientBuilder()
                .endpoint(endpoint)
                .containerName(containerName)
                .credential(credential)
                .buildClient();

        containerClient.create();

        var blobName = "blob-" + UUID.randomUUID();
        var blobClient = containerClient.getBlobClient(blobName);
        var content = "hello world".getBytes(StandardCharsets.UTF_8);
        InputStream stream = new ByteArrayInputStream(content);
        blobClient.upload(stream, content.length);

        // read text of blob
        var stream1 = blobClient.openInputStream();
        var text = new String(stream1.readAllBytes());
        assertThat(text).isEqualTo("hello world");
    }

}