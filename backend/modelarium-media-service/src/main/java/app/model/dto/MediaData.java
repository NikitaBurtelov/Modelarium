package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class MediaData {
    private UUID id;
    private String objectName;
}
