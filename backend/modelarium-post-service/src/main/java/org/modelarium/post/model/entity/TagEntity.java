package org.modelarium.post.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("tags")
@Data
@Builder
public class TagEntity {
    @Id
    private UUID id;

    private String name;

    private UUID externalId;
}
