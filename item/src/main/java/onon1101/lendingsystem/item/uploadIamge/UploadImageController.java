package onon1101.lendingsystem.item.uploadIamge;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import onon1101.lendingsystem.shared.domain.Result;
import onon1101.lendingsystem.web.response.ApiResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "item", description = "物品相關 API")
@RestController
@RequestMapping("/api/v1/items")
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class UploadImageController {

    private final UploadImageService service;

    public UploadImageController(UploadImageService service) {
        this.service = service;
    }

    @Operation(summary = "上傳多張物品圖片")
    @PostMapping(value = "/{itemId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<UploadImageResponse>>> uploadImage(
            @PathVariable UUID itemId, @RequestBody Mono<MultiValueMap<String, Part>> parts) {

        return service.uploadImage(itemId, parts).map(this::toResponse);
    }

    private ResponseEntity<ApiResponse<UploadImageResponse>> toResponse(
            Result<UploadImageResponse> outcome) {

        return outcome.match(
                response -> {
                    var body = ApiResponse.success(HttpStatus.OK, response);

                    return ResponseEntity.ok(body);
                },
                errorCode -> {
                    var status =
                            "Item.NotFound".equals(errorCode)
                                    ? HttpStatus.NOT_FOUND
                                    : HttpStatus.BAD_REQUEST;

                    return ResponseEntity.status(status)
                            .body(ApiResponse.failure(status, errorCode));
                });
    }
}
