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
import com.sushant.chronicle.personal_chronicle.model.TextJournalEntry;
import com.sushant.chronicle.personal_chronicle.repository.JournalEntryRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/text-entries")
public class TextEntryController {
    private final JournalEntryRepository journalEntryRepository;

    public TextEntryController(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TextJournalEntry createTextEntry(@Valid @RequestBody TextJournalEntry textEntry){
        return journalEntryRepository.save(textEntry);
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
    public ResponseEntity<TextJournalEntry> updateTextEntry(@Valid @PathVariable Long id, @RequestBody TextJournalEntry textEntry){
        Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
        if (optionalEntry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        JournalEntry existingEntry = optionalEntry.get();
        
        if(existingEntry instanceof TextJournalEntry) {
            TextJournalEntry textEntryToUpdate = (TextJournalEntry) existingEntry;
            textEntryToUpdate.setTitle(textEntry.getTitle());
            textEntryToUpdate.setContent(textEntry.getContent());
            return ResponseEntity.ok(journalEntryRepository.save(textEntryToUpdate));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
