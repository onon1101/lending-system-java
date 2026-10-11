import {
  ApiContractError,
  authenticatedRequest,
} from "$lib/infrastructure/http";
import { API_ROUTES } from "$lib/shared/constants/api-routes";

export type UploadCoverResponse = {
  itemId: string;
  imageUrl: string;
};

export async function uploadCover(
  itemId: string,
  file: File,
): Promise<UploadCoverResponse> {
  const body = new FormData();

  body.append("file", file);

  const result = await authenticatedRequest<UploadCoverResponse, FormData>(
    API_ROUTES.items.uploadCover(itemId),
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
    throw new ApiContractError("Invalid cover upload response");
  }

  return result;
}
