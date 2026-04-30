package org.modelarium.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class MediaData {
    private UUID id;
    private String objectKey;
    private String mediaUrl;
}