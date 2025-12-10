package app.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("media_files")
@Data
@Builder
public class MediaEntity {
    @Id
    private UUID id;

    private UUID externalId;

    private String objectName;

    private String contentType;

    private Long size;
}
