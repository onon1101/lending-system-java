import {
  ApiContractError,
  authenticatedRequest,
} from "$lib/infrastructure/http";
import { API_ROUTES } from "$lib/shared/constants/api-routes";

export type UploadItemImageResponse = {
  itemId: string;
  imageUrl: string;
};

export async function uploadItemImage(
  itemId: string,
  file: File,
): Promise<UploadItemImageResponse> {
  const body = new FormData();

  body.append("file", file);

  const result = await authenticatedRequest<UploadItemImageResponse, FormData>(
    API_ROUTES.items.uploadImage(itemId),
    {
      method: "POST",
      body,
    },
  );

  if (
    !result ||
    result.itemId !== itemId ||
    typeof result.imageUrl !== "string" ||
    !result.imageUrl
  ) {
    throw new ApiContractError("Invalid image upload response");
  }

  return result;
}
