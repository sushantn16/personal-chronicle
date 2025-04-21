package com.sushant.chronicle.personal_chronicle.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*; 


@Getter
@Setter
@Entity
@Table(name = "journal_entries")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setJournalEntry(this);
    }
    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setJournalEntry(null);
    }

}
