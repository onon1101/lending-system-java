<script lang="ts">
  import { RegistrationForm } from "$lib/features/users";
  import { AuthCard } from "$lib/shared/components";
</script>

<svelte:head><title>註冊 | Lending System</title></svelte:head><AuthCard
  title="建立帳號"
  description="填寫資料以註冊 Lending System"
  ><RegistrationForm /><a
    class="mt-6 block text-center text-sm text-sky-400"
    href="/login">返回登入</a
  ></AuthCard
>
