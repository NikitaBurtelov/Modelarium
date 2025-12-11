package org.modelarium.post.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.post.model.dto.response.MediaUploadResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final WebClient webClient;

    private static final String BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW";

    public Mono<ResponseEntity<MediaUploadResponse>> upload(
            UUID externalId,
            UUID authorId,
            Flux<FilePart> files
    ) {
        Flux<DataBuffer> jsonPart = createFormField("id", externalId.toString());
        Flux<DataBuffer> fileParts = files.flatMap(this::filePartToDataBuffers);
        DataBuffer lastBoundary = createEndBoundary();
        Flux<DataBuffer> body = Flux.concat(jsonPart, fileParts, Mono.just(lastBoundary));

        return webClient.post()
                .uri("/api/media/img")
                .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY)
                .header("author-id", authorId.toString())
                .body(BodyInserters.fromDataBuffers(body))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<MediaUploadResponse>() {});
    }

    private Flux<DataBuffer> createFormField(String name, String value) {
        String part =
                "--" + BOUNDARY + "\r\n" +
                        "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n\r\n" +
                        value + "\r\n";
        return Flux.just(stringToBuffer(part));
    }


    private Flux<DataBuffer> filePartToDataBuffers(FilePart filePart) {
        String header =
                "--" + BOUNDARY + "\r\n" +
                        "Content-Disposition: form-data; name=\"files\"; filename=\"" + filePart.filename() + "\"\r\n" +
                        "Content-Type: " + filePart.headers().getContentType() + "\r\n\r\n";

        DataBuffer headerBuffer = stringToBuffer(header);
        DataBuffer footerBuffer = stringToBuffer("\r\n");

        return Flux.concat(
                Mono.just(headerBuffer),
                filePart.content(),
                Mono.just(footerBuffer)
        );
    }

    private DataBuffer createEndBoundary() {
        String end = "--" + BOUNDARY + "--\r\n";
        return stringToBuffer(end);
    }

    private DataBuffer stringToBuffer(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return new DefaultDataBufferFactory().wrap(bytes);
    }

//    @Override
//    public Mono<ResponseEntity<MediaUploadResponse>> upload(
//            UUID externalId,
//            UUID authorId,
//            Flux<FilePart> files) {
//
//        return files
//                .flatMap(this::filePartToResource)
//                .collectList()
//                .flatMap(resources -> {
//                   MultipartBodyBuilder builder = new MultipartBodyBuilder();
//                   builder.part("id", externalId.toString());
//
//                   for (NamedByteArrayResource resource : resources) {
//                       builder.part("files", resource)
//                               .filename(resource.getFilename());
//                   }
//
//                    return webClient.post()
//                            .uri("/api/media/img")
//                            .contentType(MediaType.MULTIPART_FORM_DATA)
//                            .header("author-id", authorId.toString())
//                            .body(BodyInserters.fromMultipartData(builder.build()))
//                            .retrieve()
//                            .toEntity(new ParameterizedTypeReference<MediaUploadResponse>() {});
//                });
//    }


    //TODO подумать стоит ли делать проксирование
    @Override
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByExternalId(UUID externalId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/img/multiple/by-external-id")
                        .queryParam("externalId", externalId)
                        .build()
                )
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntityFlux(DataBuffer.class);
    }

    private Mono<NamedByteArrayResource> filePartToResource(FilePart part) {
        return DataBufferUtils.join(part.content())
                .map(dataBuffer -> {
                   try {
                       int readable = dataBuffer.readableByteCount();
                       byte[] bytes = new byte[readable];
                       dataBuffer.read(bytes);
                       return new NamedByteArrayResource(bytes, part.filename());
                   } finally {
                       DataBufferUtils.release(dataBuffer);
                   }
                });
    }

    public static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        public NamedByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename != null ? filename : "file";
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public String toString() {
            return "NamedByteArrayResource{" +
                    "filename='" + filename + '\'' +
                    ", size=" + (this.contentLengthSafe()) +
                    '}';
        }

        private long contentLengthSafe() {
            try {
                return super.contentLength();
            } catch (Exception e) {
                return -1;
            }
        }
    }

}
