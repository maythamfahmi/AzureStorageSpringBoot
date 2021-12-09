package dk.nextbix.storage.controllers;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.models.BlobItem;
import dk.nextbix.storage.service.IBlobStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("api/")
public class AzureStorageBlobServiceController {

    private final IBlobStorageService service;
    private final String CONTAINER_NAME = "data";

    @Autowired
    public AzureStorageBlobServiceController(IBlobStorageService service) {
        this.service = service;
        this.service.GetOrCreateContainer(CONTAINER_NAME);
    }

    @GetMapping(path = "ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ping(){
        return "pong";
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> listBlobFiles() {

        PagedIterable<BlobItem> result;

        try {
            result = service.ListBlobs(CONTAINER_NAME);

            if (result.stream().findAny().isEmpty()) {
                return ResponseEntity.ok("Blob is empty");
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> uploadFileToBlob(@RequestParam("file") MultipartFile file) {

        if (file == null) {
            return new ResponseEntity<>("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.UploadBlob(CONTAINER_NAME, file);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/uploadFromPath")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> uploadFileToBlob(@RequestParam("file") String file) {

        if (file == null) {
            return new ResponseEntity<>("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.UploadBlob(CONTAINER_NAME, new File(file));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> downloadFile(
            @RequestParam("file") String file,
            @RequestParam("targetPath") String targetPath) {

        if (file == null || targetPath == null) {
            return new ResponseEntity<>("You must select the a file for downloading and target path",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.DownloadBlob(CONTAINER_NAME, file, targetPath + file);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteFile(@RequestParam("file") String file) {

        if (file == null) {
            return new ResponseEntity<>("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.DeleteBlob(CONTAINER_NAME, file);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/move")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> moveFile(@RequestParam("file") String file) {

        if (file == null) {
            return new ResponseEntity<>("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.MoveBlob(file, "container01", "container02");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
