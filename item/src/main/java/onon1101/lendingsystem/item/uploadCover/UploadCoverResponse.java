package onon1101.lendingsystem.item.uploadCover;

import java.util.UUID;

public record UploadCoverResponse(UUID itemId, String imageUrl) {

    public static UploadCoverResponse from(UploadCoverResult result) {
        return new UploadCoverResponse(result.itemId(), result.imageUrl());
    }
}
