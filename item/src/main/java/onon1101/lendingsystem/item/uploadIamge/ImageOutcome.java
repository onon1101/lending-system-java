package onon1101.lendingsystem.item.uploadIamge;

import java.util.UUID;

public record ImageOutcome(
        int index,
        UUID imageId,
        Integer sortOrder,
        String imageUrl,
        String errorCode
) {
}
