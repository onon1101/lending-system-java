package onon1101.lendingsystem.image.uploadcover;

import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/items")
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class UploadCoverController {
    private final UploadCoverService service;
    private final ItemCoverInputReader inputReader;

    public UploadCoverController(
            UploadCoverService service, ItemCoverInputReader inputReader) {
        this.service = service;
        this.inputReader = inputReader;
    }

    @PostMapping(value = "/{itemId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<UploadCoverResponse>>> uploadCover(
            @PathVariable UUID itemId, @RequestPart("file") FilePart file) {
        var command = new UploadCoverCommand(itemId, inputReader.read(file));
        return service.uploadCover(command)
                .map(
                        outcome ->
                                outcome.match(
                                        result ->
                                                ResponseEntity.ok(
                                                        ApiResponse.success(
                                                                HttpStatus.OK,
                                                                UploadCoverResponse.from(
                                                                        result))),
                                        this::failure));
    }

    private ResponseEntity<ApiResponse<UploadCoverResponse>> failure(String errorCode) {
        HttpStatus status =
                switch (errorCode) {
                    case "Item.NotFound" -> HttpStatus.NOT_FOUND;
                    case "Item.CoverTooLarge" -> HttpStatus.PAYLOAD_TOO_LARGE;
                    case "Item.CoverUpdateConflict" -> HttpStatus.CONFLICT;
                    default -> HttpStatus.BAD_REQUEST;
                };
        return ResponseEntity.status(status).body(ApiResponse.failure(status, errorCode));
    }
}
