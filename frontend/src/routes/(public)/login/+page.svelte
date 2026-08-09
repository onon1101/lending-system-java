<script lang="ts">
  import { LoginForm } from "$lib/features/auth";
  import { AuthCard } from "$lib/shared/components";
</script>

<svelte:head
  ><title>登入 | Lending System</title><meta
    name="description"
    content="登入 Lending System"
  /></svelte:head
>
<AuthCard title="歡迎回來" description="請輸入帳號與密碼以繼續使用系統"
  ><LoginForm /></AuthCard
>
