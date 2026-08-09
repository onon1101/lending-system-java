<script lang="ts">
  import { goto } from "$app/navigation";
  import { ApiError } from "$lib/infrastructure/http/api-error";
  import { storeTokens } from "$lib/infrastructure/storage/token-storage";
  import {
    FormInput,
    FormLabel,
    PasswordVisibilityIcon,
    SubmitButton,
  } from "$lib/shared/components";
  import Toast from "$lib/shared/components/feedback/Toast.svelte";
  import { login } from "../api/login";
  import { validateLogin } from "../schemas/login.schema";
  import { getAuthErrorMessage } from "../utils/auth-error-messages";

  let username = $state("");
  let password = $state("");
  let showPassword = $state(false);
  let isSubmitting = $state(false);
  let errorMessage = $state("");

  async function handleSubmit(event: SubmitEvent) {
    event.preventDefault();
    errorMessage = "";
    const body = { username: username.trim(), password };
    const validationError = validateLogin(body);
    if (validationError) {
      errorMessage = validationError;
      return;
    }
    isSubmitting = true;
    try {
      const result = await login(body);
      storeTokens(result);
      password = "";
      await goto("/dashboard");
    } catch (error) {
      errorMessage =
        error instanceof ApiError
          ? getAuthErrorMessage(error.errorCode)
          : "目前無法連線至登入服務，請確認後端是否已啟動。";
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
    onDismiss={() => (errorMessage = "")}
  />
{/if}

<form class="space-y-5" onsubmit={handleSubmit}>
  <div>
    <FormLabel for="username">帳號</FormLabel><FormInput
      id="username"
      name="username"
      bind:value={username}
      autocomplete="username"
      maxlength={50}
      required
      disabled={isSubmitting}
      placeholder="請輸入帳號"
    />
  </div>
  <div>
    <FormLabel for="password">密碼</FormLabel>
    <div class="relative">
      <FormInput
        id="password"
        name="password"
        type={showPassword ? "text" : "password"}
        bind:value={password}
        autocomplete="current-password"
        maxlength={100}
        required
        disabled={isSubmitting}
        placeholder="請輸入密碼"
        hasTrailingAction
      />
      <button
        type="button"
        aria-label={showPassword ? "隱藏密碼" : "顯示密碼"}
        aria-pressed={showPassword}
        onclick={() => (showPassword = !showPassword)}
        class="absolute inset-y-0 right-0 grid w-12 place-items-center text-slate-400 hover:text-white"
        ><PasswordVisibilityIcon visible={showPassword} /></button
      >
    </div>
  </div>
  <SubmitButton {isSubmitting} label="登入" loadingLabel="登入中..." />
</form>
<nav class="mt-6 flex justify-between text-sm">
  <a class="text-sky-400 hover:text-sky-300" href="/register">建立帳號</a><a
    class="text-sky-400 hover:text-sky-300"
    href="/forgot-password">忘記密碼</a
  >
</nav>
