package org.modelarium.post.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("image")
@Data
@Builder
public class ImageEntity {
    @Id
    private UUID id;

    private String objectKey;

    private UUID externalId;
}
