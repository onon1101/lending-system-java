<script lang="ts">
  import RegistrationSuccessNotice from "$lib/features/users/components/RegistrationSuccessNotice.svelte";
  import { AuthCard } from "$lib/shared/components";
</script>

<svelte:head>
  <title>註冊成功 | Lending System</title>
</svelte:head>

<AuthCard title="建立成功" description="請前往信箱完成 Email 驗證">
  <div class="space-y-5">
    <div class="flex flex-col gap-3 text-center mb-5">
      <RegistrationSuccessNotice />

      <a
        href="/login"
        class="flex w-full items-center justify-center rounded-xl bg-sky-500 px-4 py-3 font-semibold text-white transition hover:bg-sky-400"
        >前往登入</a
      >

      <a
        href="/resend-verification"
        class="text-xs text-slate-400 transition hover:text-slate-300"
        >沒有收到信件？重新寄送驗證信</a
      >
    </div>
  </div>
  <p class="text-center text-xs leading-5 text-slate-500">
    若沒有看到驗證信，請先檢查垃圾郵件。
  </p>
</AuthCard>
