package org.modelarium.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
@Getter
@Builder
public class MediaData {
    private UUID id;
    private String objectKey;
    private String mediaUrl;

    @JsonCreator
    public MediaData(
            @JsonProperty("id") UUID id,
            @JsonProperty("objectKey") String objectKey,
            @JsonProperty("mediaUrl") String mediaUrl
    ) {
        this.id = id;
        this.objectKey = objectKey;
        this.mediaUrl = mediaUrl;
    }
}

