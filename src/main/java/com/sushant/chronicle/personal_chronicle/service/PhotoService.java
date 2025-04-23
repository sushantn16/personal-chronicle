package com.sushant.chronicle.personal_chronicle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sushant.chronicle.personal_chronicle.model.JournalEntry;
import com.sushant.chronicle.personal_chronicle.model.Photo;
import com.sushant.chronicle.personal_chronicle.repository.JournalEntryRepository;
import com.sushant.chronicle.personal_chronicle.repository.PhotoRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PhotoService {
    
    private final Path fileStorageLocation;
    private final PhotoRepository photoRepository;
    private final JournalEntryRepository journalEntryRepository;

    @Autowired
    public PhotoService(@Value("${myapp.upload-dir}") String uploadDir, PhotoRepository photoRepository, JournalEntryRepository journalEntryRepository) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.photoRepository = photoRepository;
        this.journalEntryRepository = journalEntryRepository;

        try{
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    public Photo storePhoto(MultipartFile file, Long entryId) {
        JournalEntry journalEntry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + entryId));

        String OrigionalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String UniqueFileName = UUID.randomUUID().toString() + "_" + OrigionalFileName;
        try {
            if (OrigionalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + OrigionalFileName);
            }

            // Copy the file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(UniqueFileName);
            Files.copy(file.getInputStream(), targetLocation);
            String fileDownloadUrl = "/api/photos/view/" + UniqueFileName;

            Photo photo = new Photo(OrigionalFileName, UniqueFileName, fileDownloadUrl);
            journalEntry.addPhoto(photo);
            journalEntryRepository.save(journalEntry);
            photoRepository.save(photo);
            return photo;
        } catch (Exception e) {
            throw new RuntimeException("Could not store file " + OrigionalFileName + ". Please try again!", e);
        }
    }
    
    public Resource loadPhotoAsResource(String identifier) throws Exception {
        Photo photo = photoRepository.findByStorageIdentifier(identifier)
                .orElseThrow(() -> new FileNotFoundException("Photo Not Found" + identifier));
        
        try{
            Path filePath = this.fileStorageLocation.resolve(photo.getStorageIdentifier()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + identifier);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + identifier);
        }
    }

    public List<Photo> listPhotosByEntry(Long entryId) {
        return photoRepository.findByJournalEntryId(entryId);
    }

    public List<Photo> listAllPhotos() {
        return photoRepository.findAll();
    }

    public void deletePhoto(Long photoId) throws IOException {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new FileNotFoundException("Photo not found with id: " + photoId));
        
        try{
            Path filepath = this.fileStorageLocation.resolve(photo.getStorageIdentifier()).normalize();
            Files.deleteIfExists(filepath);
        }
        catch (IOException e) {
            throw new IOException("Could not delete file: " + photo.getStorageIdentifier(), e);
        }
        JournalEntry journalEntry = photo.getJournalEntry();
        if (journalEntry != null) {
            journalEntry.removePhoto(photo);
            journalEntryRepository.save(journalEntry);
        }
        photoRepository.deleteById(photoId);
    }
}
