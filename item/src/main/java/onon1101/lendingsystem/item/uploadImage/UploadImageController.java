package onon1101.lendingsystem.item.uploadImage;

import io.swagger.v3.oas.annotations.tags.Tag;
import onon1101.lendingsystem.item.uploadImage.image.ImageReader;

import onon1101.lendingsystem.web.response.ApiResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/items")
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled",
        havingValue = "true")
public class UploadImageController {

    private final UploadImageService service;
    private final ImageReader reader;

    public UploadImageController(
            UploadImageService service,
            ImageReader reader
    ) {
        this.service = service;
        this.reader = reader;
    }

    @PostMapping(value = "/{itemId}/image", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<UploadImageResponse>>> upload(
            @PathVariable UUID itemId,
            @RequestPart("file") FilePart file) {

        var command = new UploadImageCommand(itemId, reader.read(file));

        return service
                .upload(command)
                .map(outcome -> outcome.match(
                        result -> {
                            var response = UploadImageResponse.from(result);
                            var body = ApiResponse.success(HttpStatus.OK,
                                    response);
                            return ResponseEntity.ok(body);
                        },
                        this::failure));

    }

    private ResponseEntity<ApiResponse<UploadImageResponse>> failure(
            String errorCode) {

        HttpStatus status =
                switch (errorCode) {
                    case "Item.NotFound" -> HttpStatus.NOT_FOUND;
                    case "Item.ImageTooLarge" -> HttpStatus.PAYLOAD_TOO_LARGE;
                    case "Item.ImageUpdateConflict" -> HttpStatus.CONFLICT;
                    default -> HttpStatus.BAD_REQUEST;
                };

        return ResponseEntity
                .status(status)
                .body(ApiResponse.failure(status, errorCode));
    }
}
