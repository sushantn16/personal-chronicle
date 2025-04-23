package com.sushant.chronicle.personal_chronicle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sushant.chronicle.personal_chronicle.model.Photo;
import com.sushant.chronicle.personal_chronicle.service.PhotoService;

record PhotoUploadResponse(String message, String filename, String storageIdentifier) {
}

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    
    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoUploadResponse> uploadPhoto(@RequestParam("File") MultipartFile file, @RequestParam("entryId") Long entryId) {
        try {
            Photo photo = photoService.storePhoto(file, entryId);
            PhotoUploadResponse response = new PhotoUploadResponse("Photo uploaded successfully", photo.getFilename(), photo.getStorageIdentifier());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/view/{identifier:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String identifier) {
        try{
            Resource resource = photoService.loadPhotoAsResource(identifier);

            return ResponseEntity.ok()
                    .body(resource);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Photo>> listPhotos( @RequestParam(required = false) Long entryId){
        List<Photo> photos;
        if(entryId != null) {
            photos = photoService.listPhotosByEntry(entryId);
        } else {
            photos = photoService.listAllPhotos();
        }
        return ResponseEntity.ok(photos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        try {
            photoService.deletePhoto(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
        

}
