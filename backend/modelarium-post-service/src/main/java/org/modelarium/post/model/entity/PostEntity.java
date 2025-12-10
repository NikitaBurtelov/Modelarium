package org.modelarium.post.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table("posts")
@Data
@Builder
public class PostEntity {
    @Id
    private UUID id;

    private UUID authorId;

    private String description;

    private List<String> tags;

    private List<String> mentions;

    private List<UUID> mediaIds;

    private Instant createdAt;

    private Instant updatedAt;
}