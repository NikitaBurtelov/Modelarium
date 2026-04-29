package app.repository;

import app.model.entity.MediaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaReactiveRepository
        extends ReactiveCrudRepository<MediaEntity, UUID>, MediaRepository {
    Flux<MediaEntity> findAllByExternalId(UUID externalId);

    Flux<MediaEntity> findAllByExternalIdIn(Collection<UUID> externalIds);

    Flux<MediaEntity> findAllByObjectNameIn(Collection<String> objectNames);
}

