package app.repository;

import app.model.entity.MediaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MediaRepository
        extends ReactiveCrudRepository<MediaEntity, UUID> {
}

