<script lang="ts">
  import AuthCard from "$lib/shared/components/layout/AuthCard.svelte";
</script>

<svelte:head>
  <title>重新寄送驗證信 | Lending System</title>
</svelte:head>

<AuthCard title="重新寄送驗證信" description="輸入註冊時使用的 Email">
  <form class="space-y-5">
    <div>
      <label for="email" class="mb-2 block text-sm font-medium text-slate-200">
        Email
      </label>

      <input
        id="email"
        name="email"
        type="text"
        inputmode="email"
        autocomplete="email"
        class="block w-full rounded-xl border border-white/10 bg-slate-950/50 py-4 px-3 text-white outline-none transition focus:border-sky-400 focus:ring-4 focus:ring-sky-400/10"
      />
    </div>

    <button
      type="submit"
      class="w-full rounded-xl bg-sky-500 px-4 py-3 font-semibold text-white transition hover:bg-sky-400"
    >
      重新寄送驗證信
    </button>
  </form>

  <a
    href="/login"
    class="mt-6 block text-center text-sm text-sky-400 hover:text-sky-300"
    >返回登入</a
  >
</AuthCard>
