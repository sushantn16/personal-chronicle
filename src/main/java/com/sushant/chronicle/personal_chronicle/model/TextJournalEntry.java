package com.sushant.chronicle.personal_chronicle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "text_journal_entries")
@PrimaryKeyJoinColumn(name = "entry_id")
public class TextJournalEntry extends JournalEntry {

    @NotBlank(message = "Content is mandatory")
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public TextJournalEntry(String title, String content) {
        super();
        this.setTitle(title);
        this.setContent(content);
    }
    
}
