package dk.nextbix.storage;

import dk.nextbix.storage.service.BlobStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

	@Bean
	public BlobStorageService blobStorageService(){
		return new BlobStorageService(connStr, containerName);
	}

}
