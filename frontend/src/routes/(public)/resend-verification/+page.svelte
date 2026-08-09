<script lang="ts">
  import { resendEmailVerification } from "$lib/features/auth/api/resend-email-verification";
  import { ApiError } from "$lib/infrastructure/http";
  import Toast from "$lib/shared/components/feedback/Toast.svelte";
  import FormInput from "$lib/shared/components/forms/FormInput.svelte";
  import FormLabel from "$lib/shared/components/forms/FormLabel.svelte";
  import SubmitButton from "$lib/shared/components/forms/SubmitButton.svelte";
  import AuthCard from "$lib/shared/components/layout/AuthCard.svelte";

  let email = $state("");
  let isSubmitting = $state(false);
  let message = $state("");
  let errorMessage = $state("");

  function validateEmail(): string | null {
    const normalizedEmail = email.trim();

    if (!normalizedEmail) {
      return "請輸入 Email";
    }

    if (normalizedEmail.length > 255) {
      return "Email 不可超過 255 個字元";
    }

    if (!normalizedEmail.includes("@")) {
      return "Email 格式不正確";
    }

    return null;
  }

  async function submit(event: SubmitEvent) {
    event.preventDefault();

    message = "";
    errorMessage = "";

    const validationError = validateEmail();

    if (validationError) {
      errorMessage = validationError;
      return;
    }

    isSubmitting = true;

    try {
      const result = await resendEmailVerification({
        email: email.trim(),
      });

      message = result.message;
    } catch (error) {
      errorMessage =
        error instanceof ApiError
          ? "目前無法重新寄送驗證信，請稍後再試。"
          : "目前無法連線至驗證服務。";
    } finally {
      isSubmitting = false;
    }
  }
</script>

<svelte:head>
  <title>重新寄送驗證信 | Lending System</title>
</svelte:head>

{#if errorMessage}
  <Toast
    message={errorMessage}
    type="error"
    duration={5000}
    onDismiss={() => (errorMessage = "")}
  ></Toast>
{/if}

{#if message}
  <Toast
    {message}
    type="success"
    duration={3000}
    onDismiss={() => (message = "")}
  ></Toast>
{/if}

<AuthCard title="重新寄送驗證信" description="輸入註冊時使用的 Email">
  <form class="space-y-5" onsubmit={submit} novalidate>
    <div>
      <FormLabel for="email">Email</FormLabel>

      <FormInput
        id="email"
        name="email"
        type="text"
        inputmode="email"
        autocomplete="email"
        bind:value={email}
        disabled={isSubmitting}
        aria-required="true"
      ></FormInput>
    </div>

    <SubmitButton
      {isSubmitting}
      label="重新寄送驗證信"
      loadingLabel="寄送中..."
    />
  </form>

  <a
    href="/login"
    class="mt-6 block text-center text-sm text-sky-400 hover:text-sky-300"
    >返回登入
  </a>
</AuthCard>
