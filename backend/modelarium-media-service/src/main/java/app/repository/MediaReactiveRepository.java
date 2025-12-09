package app.repository;

import app.model.entity.MediaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MediaReactiveRepository
        extends ReactiveCrudRepository<MediaEntity, UUID>, MediaRepository {
}

