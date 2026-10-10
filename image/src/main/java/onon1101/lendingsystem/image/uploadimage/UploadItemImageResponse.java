package onon1101.lendingsystem.image.uploadimage;

import java.util.UUID;

public record UploadItemImageResponse(UUID itemId, String imageUrl) {
    public static UploadItemImageResponse from(UploadItemImageResult result) {
        return new UploadItemImageResponse(result.itemId(), result.imageUrl());
    }
}
