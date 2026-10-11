package onon1101.lendingsystem.item.uploadIamge;

import java.util.List;
import java.util.UUID;

public record UploadImageResponse(UUID itemId, List<ImageOutcome> images) {}
