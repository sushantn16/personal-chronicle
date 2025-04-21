package com.sushant.chronicle.personal_chronicle.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sushant.chronicle.personal_chronicle.model.JournalEntry;
import com.sushant.chronicle.personal_chronicle.repository.JournalEntryRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entries")
public class JounralEntryController {
    private final JournalEntryRepository journalEntryRepository;

    public JounralEntryController(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalEntry createEntry(@Valid @RequestBody JournalEntry journalEntry){
        return journalEntryRepository.save(journalEntry);
    }

    @RequestMapping
    public List<JournalEntry> getAllEntries(){
        return journalEntryRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable Long id){
        Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);

        return optionalEntry.map(entry -> ResponseEntity.ok(entry)).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> updateEntry(@Valid @PathVariable Long id, @RequestBody JournalEntry journalEntry){
        return journalEntryRepository.findById(id)
            .map(existingEntry -> {
                existingEntry.setTitle(journalEntry.getTitle());
                existingEntry.setContent(journalEntry.getContent());
                JournalEntry updatedEntry = journalEntryRepository.save(existingEntry);
                return ResponseEntity.ok(updatedEntry);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id){
        return journalEntryRepository.findById(id)
            .map(existingEntry -> {
                journalEntryRepository.delete(existingEntry);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
