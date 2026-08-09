// src/lib/shared/constants/api-routes.ts

export const API_ROUTES = {
  auth: {
    login: "/api/v1/auth/login",
    logout: "/api/v1/auth/logout",
    forgotPassword: "/api/v1/auth/forgot-password",
    resetPassword: "/api/v1/auth",
    confirmEmail: "/api/v1/auth/email-verification/confirm",
  },
  users: {
    register: "/api/v1/user/register",
    currentUser: "/api/v1/user/whoami",
  },
  items: {
    create: "/api/v1/items/create",
    retrieve: (itemId: string) =>
      `/api/v1/item/retrieve/${encodeURIComponent(itemId)}`,
    update: "/api/v1/items/update",
    delete: "/api/v1/item/delete",
  },
} as const;
