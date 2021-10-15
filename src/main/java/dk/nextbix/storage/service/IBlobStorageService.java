package dk.nextbix.storage.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface IBlobStorageService {
    BlobClient UploadBlob(MultipartFile file);
    BlobClient UploadBlob(File file) throws IOException;
    void DownloadBlob(String blobFileName, String localFilePath);
    void DeleteBlob(String blobFileName);
    PagedIterable<BlobItem> ListBlobs();
}