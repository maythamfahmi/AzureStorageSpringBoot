package dk.nextbix.storage.controllers;

import com.azure.storage.blob.BlobContainerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/blobclent")
public class BlobClientController {

    private final BlobContainerClient blobContainerClient;

    public BlobClientController(ApplicationContext applicationContext) {
        this.blobContainerClient = applicationContext.getBean(BlobContainerClient.class);
    }

    @GetMapping(path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void WhatEverAction() {
        blobContainerClient.getBlobClient("");
    }
}