package com.sushant.chronicle.personal_chronicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sushant.chronicle.personal_chronicle.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByFilename, findByStorageIdentifier, etc.
    Optional<Photo> findByStorageIdentifier(String storageIdentifier);
    List<Photo> findByJournalEntryId(Long journalEntryId);
    
}
