package dk.nextbix.storage.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobCopyInfo;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class BlobStorageService implements IBlobStorageService {
    private final BlobServiceClient blobServiceClient;

    @Autowired
    public BlobStorageService(final BlobServiceClientBuilder blobServiceClientBuilder) {
        this.blobServiceClient = blobServiceClientBuilder.buildClient();
    }

    public BlobContainerClient GetOrCreateContainer(String containerName) {
        var container = blobServiceClient.getBlobContainerClient(containerName);

        if (!container.exists()) {
            return blobServiceClient.createBlobContainer(containerName);
        }

        return container;
    }

    public BlobClient UploadBlob(String containerName, MultipartFile file) {
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(file.getOriginalFilename());

        try {
            InputStream data = file.getInputStream();
            blobClient.upload(data, file.getSize());
        } catch (Exception e) {
        }

        return blobClient;
    }

    public BlobClient UploadBlob(String containerName, File file) {
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(file.getName());

        try {
            InputStream fileWriter = new FileInputStream(file.getAbsoluteFile());
            blobClient.upload(fileWriter, fileWriter.available());
        } catch (Exception e) {
        }

        return blobClient;
    }

    public void DownloadBlob(String containerName, String blobFileName, String localFilePath) {
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobFileName);
        blobClient.downloadToFile(localFilePath);
    }

    public void DeleteBlob(String containerName, String blobFileName) {
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobFileName);
        blobClient.delete();
    }

    public void MoveBlob(String blobFileName, String sourceContainer, String targetContainer) {
        BlobContainerClient source = GetOrCreateContainer(sourceContainer);
        BlobContainerClient target = GetOrCreateContainer(targetContainer);

        BlobClient blobClientRead = source.getBlobClient(blobFileName);
        BlobClient blobClientProcessed = target.getBlobClient(blobFileName);
        SyncPoller<BlobCopyInfo, Void> blobCopyInfoVoidSyncPoller = blobClientProcessed.beginCopy(blobClientRead.getBlobUrl(), null);
        PollResponse<BlobCopyInfo> blobCopyInfoPollResponse = blobCopyInfoVoidSyncPoller.waitForCompletion();
        if(blobCopyInfoPollResponse.getStatus().isComplete()){
            blobClientRead.delete();
        }
    }

    public PagedIterable<BlobItem> ListBlobs(String containerName) {
        PagedIterable<BlobItem> blobItems = blobServiceClient.getBlobContainerClient(containerName).listBlobs();
        return blobItems;
    }

}
