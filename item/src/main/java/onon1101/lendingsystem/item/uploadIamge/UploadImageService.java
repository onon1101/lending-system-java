package onon1101.lendingsystem.item.uploadIamge;

import java.util.List;
import java.util.UUID;
import onon1101.lendingsystem.application.currentuser.CurrentUserProvider;
import onon1101.lendingsystem.item.uploadIamge.error.UploadImageDomainError;
import onon1101.lendingsystem.item.uploadIamge.repository.UploadImageReader;
import onon1101.lendingsystem.shared.domain.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "lending.rustfs", name = "enabled", havingValue = "true")
public class UploadImageService {

    private static final int MAX_FILES = 10;

    private final CurrentUserProvider currentUserProvider;
    private final UploadImageReader itemReader;
    private final SingleImageUploader singleImageUploader;

    public UploadImageService(
            CurrentUserProvider currentUserProvider,
            UploadImageReader itemReader,
            SingleImageUploader singleImageUploader) {
        this.currentUserProvider = currentUserProvider;
        this.itemReader = itemReader;
        this.singleImageUploader = singleImageUploader;
    }

    public Mono<Result<UploadImageResponse>> uploadImage(
            UUID itemId, Mono<MultiValueMap<String, Part>> parts) {

        return currentUserProvider
                .getCurrentUser()
                .flatMap(
                        user -> {
                            long ownerId = user.privateUserId();

                            return uploadForOwner(itemId, ownerId, parts);
                        });
    }

    private Mono<Result<UploadImageResponse>> uploadForOwner(
            UUID itemId, long ownerId, Mono<MultiValueMap<String, Part>> parts) {

        return itemReader
                .isOwnedItem(itemId, ownerId)
                .flatMap(
                        owned -> {
                            if (!owned) {
                                return Mono.just(failure("Item.NotFound"));
                            }

                            // 通過授權後，才訂閱並處理 Multipart。
                            return parts.flatMap(
                                            form -> {
                                                List<Part> files = form.get("files");

                                                return uploadBatch(itemId, ownerId, files);
                                            })
                                    .switchIfEmpty(
                                            Mono.fromSupplier(
                                                    () -> {
                                                        return failure("Item.ImagesRequired");
                                                    }));
                        });
    }

    private Mono<Result<UploadImageResponse>> uploadBatch(
            UUID itemId, long ownerId, List<Part> files) {

        if (files == null || files.isEmpty()) {
            return Mono.just(failure("Item.ImagesRequired"));
        }

        if (files.size() > MAX_FILES) {
            return Mono.just(failure("Item.TooManyImages"));
        }

        // 逐張讀取與上傳，避免同時將所有圖片解碼到記憶體。
        return Flux.fromIterable(files)
                .index()
                .concatMap(
                        entry -> {
                            int index = entry.getT1().intValue();
                            Part part = entry.getT2();

                            return singleImageUploader.upload(itemId, ownerId, index, part);
                        },
                        1)
                .collectList()
                .map(
                        outcomes -> {
                            var response = new UploadImageResponse(itemId, outcomes);

                            return Result.success(response);
                        });
    }

    private static Result<UploadImageResponse> failure(String errorCode) {
        return Result.failure(new UploadImageDomainError(errorCode));
    }
}
