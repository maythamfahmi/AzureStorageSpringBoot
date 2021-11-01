package dk.nextbix.storage.controllers;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/BlobClient")
public class BlobClientController {

    private final BlobContainerClient blobContainerClient;

    public BlobClientController(ApplicationContext applicationContext) {
        this.blobContainerClient = applicationContext.getBean(BlobContainerClient.class);
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> listBlobFiles() throws IOException {

        PagedIterable<BlobItem> result;

        try {
            result = blobContainerClient.listBlobs();

            if (result.stream().count() == 0 || result == null) {
                return ResponseEntity.ok("Blob is empty");
            }
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity uploadFileToBlob(@RequestParam("file") MultipartFile file) throws Exception {

        if (file == null) {
            return new ResponseEntity("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(file.getOriginalFilename());
            InputStream data = file.getInputStream();
            blobClient.upload(data, file.getSize());
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/uploadFromPath")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity uploadFileToBlob(@RequestParam("filename") String filename) throws Exception {

        if (filename == null) {
            return new ResponseEntity("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            var file = new File(filename);
            BlobClient blobClient = blobContainerClient.getBlobClient(file.getName());
            InputStream fileWriter = new FileInputStream(file.getAbsoluteFile());
            blobClient.upload(fileWriter, fileWriter.available());
        } catch (Exception e) {
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity downloadFile(
            @RequestParam("file") String filename,
            @RequestParam("targetPath") String targetPath) {

        if (filename == null || targetPath == null) {
            return new ResponseEntity("You must select the a file for downloading and target path",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(filename);
            blobClient.downloadToFile(targetPath);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteFile(@RequestParam("file") String file) {

        if (file == null) {
            return new ResponseEntity("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(file);
            blobClient.delete();
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
