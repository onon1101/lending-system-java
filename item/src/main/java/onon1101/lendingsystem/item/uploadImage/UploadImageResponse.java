package onon1101.lendingsystem.item.uploadImage;

import java.util.UUID;

public record UploadImageResponse(UUID itemId, String imageUrl) {

  public static UploadImageResponse from(UploadImageResult result) {
    return new UploadImageResponse(result.itemId(), result.imageUrl());
  }
}
