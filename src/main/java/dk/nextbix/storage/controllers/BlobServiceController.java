package dk.nextbix.storage.controllers;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.models.BlobItem;
import dk.nextbix.storage.service.BlobStorageService;
import dk.nextbix.storage.service.IBlobStorageService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("api/blobservice")
public class BlobServiceController {

    private IBlobStorageService service;

    private final ApplicationContext applicationContext;

    public BlobServiceController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.service = this.applicationContext.getBean(BlobStorageService.class);
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> listBlobFiles() throws IOException {

        PagedIterable<BlobItem> result;

        try {
            result = service.ListBlobs();

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
            service.UploadBlob(file);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/uploadFromPath")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity uploadFileToBlob(@RequestParam("file") String file) throws Exception {

        if (file == null) {
            return new ResponseEntity("You must select the a file for uploading",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.UploadBlob(new File(file));
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/download")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity downloadFile(
            @RequestParam("file") String file,
            @RequestParam("targetPath") String targetPath) {

        if (file == null || targetPath == null) {
            return new ResponseEntity("You must select the a file for downloading and target path",
                    HttpStatus.BAD_REQUEST);
        }

        try {
            service.DownloadBlob(file, targetPath + file);
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
            service.DeleteBlob(file);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
