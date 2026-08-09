import { request } from "$lib/infrastructure/http";
import { API_ROUTES } from "$lib/shared/constants/api-routes";
import type {
  MessageResponse,
  ResendEmailVerificationRequest,
} from "../types/auth.types";

export function resendEmailVerification(
  body: ResendEmailVerificationRequest,
): Promise<MessageResponse> {
  return request<MessageResponse, ResendEmailVerificationRequest>(
    API_ROUTES.auth.resendEmailVerification,
    {
      method: "POST",
      body,
    },
  );
}
