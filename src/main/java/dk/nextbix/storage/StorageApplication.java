package dk.nextbix.storage;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import dk.nextbix.storage.service.BlobStorageService;
import dk.nextbix.storage.service.IBlobStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StorageApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorageApplication.class, args);
	}

	@Value("${azure.storage.connection.string}")
	private String connStr;
	@Value("${azure.storage.container}")
	private String containerName;

	//Register as customized service
	@Bean
	public BlobStorageService blobStorageService(){
		return new BlobStorageService(connStr, containerName);
	}

	//Register as BlobServiceClient
	@Bean
	public BlobContainerClient blobContainerClient() {
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.connectionString(connStr)
				.buildClient();

		BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
		if (!container.exists()) {
			return blobServiceClient.createBlobContainer(containerName);
		}
		return container;
	}

}
