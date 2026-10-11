package onon1101.lendingsystem.item.uploadCover;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import onon1101.lendingsystem.item.uploadCover.image.ImageReader;

import onon1101.lendingsystem.shared.domain.Result;
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
public class UploadCoverController {

    private final UploadCoverService service;
    private final ImageReader reader;

    public UploadCoverController(
            UploadCoverService service,
            ImageReader reader
    ) {
        this.service = service;
        this.reader = reader;
    }

    @Operation(summary = "上傳物品封面")
    @PostMapping(value = "/{itemId}/cover", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<UploadCoverResponse>>> uploadCover(
            @PathVariable UUID itemId,
            @RequestPart("file") FilePart file) {

        var command = new UploadCoverCommand(itemId, reader.read(file));

        return service
                .uploadCover(command)
                .map(this::toResponse);
    }

    private ResponseEntity<ApiResponse<UploadCoverResponse>> toResponse(
            Result<UploadCoverResult> outcome) {
        return outcome.match(result -> {
            var response = UploadCoverResponse.from(result);
            var body = ApiResponse.success(HttpStatus.OK, response);
            return ResponseEntity.ok(body);
        }, this::failure);
    }

    private ResponseEntity<ApiResponse<UploadCoverResponse>> failure(
            String errorCode) {

        HttpStatus status =
                switch (errorCode) {
                    case "Item.NotFound" -> HttpStatus.NOT_FOUND;
                    case "Item.CoverTooLarge" -> HttpStatus.PAYLOAD_TOO_LARGE;
                    case "Item.CoverUpdateConflict" -> HttpStatus.CONFLICT;
                    default -> HttpStatus.BAD_REQUEST;
                };

        return ResponseEntity
                .status(status)
                .body(ApiResponse.failure(status, errorCode));
    }
}
