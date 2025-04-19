package com.sushant.chronicle.personal_chronicle.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sushant.chronicle.personal_chronicle.model.JournalEntry;

@Repository
public interface JournalEntryRepository extends JpaRepository< JournalEntry, Long> {
    // Custom query methods can be defined here if needed
    // For example, findByTitle, findByCreationDateBetween, etc.

    
}
