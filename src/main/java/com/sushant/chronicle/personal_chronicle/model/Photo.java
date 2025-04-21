package com.sushant.chronicle.personal_chronicle.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String storageIdentifier;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id")
    private JournalEntry journalEntry;

    @PrePersist
    protected void onUpload() {
        this.creationDate = LocalDateTime.now();
    }

    public Photo(String filename, String storageIdentifier, String url) {
        this.filename = filename;
        this.storageIdentifier = storageIdentifier;
        this.url = url;
    }

}
