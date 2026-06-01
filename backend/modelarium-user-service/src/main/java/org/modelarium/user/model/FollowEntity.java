package org.modelarium.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "follows")
@Getter
@Setter
@Builder(builderMethodName = "builder")
@NoArgsConstructor
@AllArgsConstructor
public class FollowEntity {
    @Id
    private UUID id;

    private UUID userId;   // who
    private UUID subscriberId;  //

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if  (id == null) {
            id = UUID.randomUUID();
            createdAt = LocalDateTime.now();
        }
    }
}