package com.myprojects.rentalassistant.rawmessage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RawMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(name = "message_timestamp", nullable = false)
    private LocalDateTime messageTimestamp;

    @Column(name = "message_text", nullable = false, columnDefinition = "text")
    private String messageText;

    @Column(name = "message_hash", nullable = false, unique = true, length = 64)
    private String messageHash;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    public RawMessage(String sender, LocalDateTime messageTimestamp, String messageText, String messageHash) {
        this.sender = sender;
        this.messageTimestamp = messageTimestamp;
        this.messageText = messageText;
        this.messageHash = messageHash;
        this.importedAt = LocalDateTime.now();
    }
}
