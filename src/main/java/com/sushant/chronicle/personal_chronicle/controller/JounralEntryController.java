package com.sushant.chronicle.personal_chronicle.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sushant.chronicle.personal_chronicle.model.JournalEntry;
import com.sushant.chronicle.personal_chronicle.repository.JournalEntryRepository;

@RestController
@RequestMapping("/api/entries")
public class JounralEntryController {
    private final JournalEntryRepository journalEntryRepository;

    public JounralEntryController(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalEntry createEntry(@RequestBody JournalEntry journalEntry){
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
}
