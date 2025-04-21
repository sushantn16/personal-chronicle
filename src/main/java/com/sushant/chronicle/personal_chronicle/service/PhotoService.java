package com.sushant.chronicle.personal_chronicle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sushant.chronicle.personal_chronicle.model.JournalEntry;
import com.sushant.chronicle.personal_chronicle.model.Photo;
import com.sushant.chronicle.personal_chronicle.repository.JournalEntryRepository;
import com.sushant.chronicle.personal_chronicle.repository.PhotoRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}
