package dk.nextbix.storage.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class BlobStorageService implements IBlobStorageService {
    private final BlobContainerClient blobContainerClient;
    private final BlobServiceClient blobServiceClient;

    public BlobStorageService(String connStr, String containerName) {
        blobServiceClient = new BlobServiceClientBuilder().connectionString(connStr).buildClient();
        blobContainerClient = GetOrCreateContainer(containerName);
    }

    private BlobContainerClient GetOrCreateContainer(String containerName) {
        var container = blobServiceClient.getBlobContainerClient(containerName);

        if (!container.exists()) {
            return blobServiceClient.createBlobContainer(containerName);
        }

        return container;
    }

    public BlobClient UploadBlob(MultipartFile file) {
        BlobClient blobClient = blobContainerClient.getBlobClient(file.getOriginalFilename());

        try {
            InputStream data = file.getInputStream();
            blobClient.upload(data, file.getSize());
        } catch (Exception e) {
        }

        return blobClient;
    }

    public BlobClient UploadBlob(File file) {
        BlobClient blobClient = blobContainerClient.getBlobClient(file.getName());

        try {
            InputStream fileWriter = new FileInputStream(file.getAbsoluteFile());
            blobClient.upload(fileWriter, fileWriter.available());
        } catch (Exception e) {
        }

        return blobClient;
    }

    public void DownloadBlob(String blobFileName, String localFilePath) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobFileName);
        blobClient.downloadToFile(localFilePath);
    }

    public void DeleteBlob(String blobFileName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobFileName);
        blobClient.delete();
    }

    public PagedIterable<BlobItem> ListBlobs() {
        PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobs();
        return blobItems;
    }

}
