package dk.nextbix.storage.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IBlobStorageService {
    BlobContainerClient GetOrCreateContainer(String containerName);

    BlobClient UploadBlob(String containerName, MultipartFile file);

    BlobClient UploadBlob(String containerName, File file);

    void DownloadBlob(String containerName, String blobFileName, String localFilePath);

    void DeleteBlob(String containerName, String blobFileName);

    void MoveBlob(String blobFileName, String sourceContainer, String targetContainer);

    PagedIterable<BlobItem> ListBlobs(String containerName);
}