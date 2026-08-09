<script lang="ts">
  import { ApiError } from "$lib/infrastructure/http/api-error";
  import {
    Alert,
    FormInput,
    FormLabel,
    SubmitButton,
  } from "$lib/shared/components";
  import Toast from "$lib/shared/components/feedback/Toast.svelte";
  import { error } from "@sveltejs/kit";
  import { registerUser } from "../api/register-user";
  import { getRegisterErrorMessage } from "../utils/register-error-message";
  import { onDestroy } from "svelte";
  import { goto } from "$app/navigation";

  let username = $state("");
  let email = $state("");
  let password = $state("");
  let isSubmitting = $state(false);
  let message = $state("");
  let errorMessage = $state("");

  let redirectTimer: ReturnType<typeof setTimeout> | undefined;

  onDestroy(() => {
    if (redirectTimer) {
      clearTimeout(redirectTimer);
    }
  });

  function validateRegistration(): string | null {
    const normalizedUsername = username.trim();
    const normalizedEmail = email.trim();

    if (!normalizedUsername || !normalizedEmail || !password) {
      return "請完整填寫註冊資料。";
    }

    if (normalizedUsername.length > 50) {
      return "帳號不可超過 50 個字元。";
    }

    if (normalizedEmail.length > 255) {
      return "Email 不可超過 255 個字元。";
    }

    if (!normalizedEmail.includes("@")) {
      return "Email 格式不正確。";
    }

    if (password.length > 100) {
      return "密碼不可超過 100 個字元。";
    }

    return null;
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault();

    message = "";
    errorMessage = "";

    const validateionError = validateRegistration();

    if (validateionError) {
      errorMessage = validateionError;
      return;
    }

    isSubmitting = true;

    try {
      const result = await registerUser({
        username: username.trim(),
        email: email.trim(),
        password,
      });

      message = `帳號建立成功，即將前往登入頁面。`;
      password = "";

      redirectTimer = setTimeout(() => {
        void goto("/login");
      }, 2000);
    } catch (error) {
      errorMessage =
        error instanceof ApiError
          ? getRegisterErrorMessage(error.errorCode)
          : "目前無法連線至註冊服務。";
    } finally {
      isSubmitting = false;
    }
  }
</script>

{#if errorMessage}
  <Toast
    message={errorMessage}
    type="error"
    duration={5000}
    onDismiss={() => {
      errorMessage = "";
    }}
  />
{/if}

{#if message}
  <Toast
    {message}
    type="success"
    duration={3000}
    onDismiss={() => {
      errorMessage = "";
    }}
  />
{/if}

<form class="space-y-5" onsubmit={submit}>
  <div>
    <FormLabel for="username">帳號</FormLabel><FormInput
      id="username"
      bind:value={username}
      maxlength={50}
      autocomplete="username"
      aria-required="true"
    />
  </div>

  <div>
    <FormLabel for="email">Email</FormLabel><FormInput
      id="email"
      type="text"
      inputmode="email"
      bind:value={email}
      autocomplete="email"
      aria-required="true"
    />
  </div>
  <div>
    <FormLabel for="password">密碼</FormLabel><FormInput
      id="password"
      type="password"
      bind:value={password}
      autocomplete="new-password"
      aria-required="true"
    />
  </div>
  <SubmitButton {isSubmitting} label="建立帳號" />
</form>
