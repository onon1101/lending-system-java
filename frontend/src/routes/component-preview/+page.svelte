<script lang="ts">
  import Toast from "$lib/shared/components/feedback/Toast.svelte";

  let showError = $state(true);
  let showSuccess = $state(true);
</script>

<svelte:head>
  <title>Component Preview</title>
</svelte:head>

<div class="min-h-screen bg-slate-950 p-8 text-white">
  <h1 class="text-2xl font-bold">Toast Preview</h1>

  <div class="mt-6 flex gap-3">
    <button
      class="rounded-lg bg-rose-500 px-4 py-2"
      onclick={() => (showError = true)}
    >
      顯示錯誤
    </button>

    <button
      class="rounded-lg bg-emerald-500 px-4 py-2"
      onclick={() => (showSuccess = true)}
    >
      顯示成功
    </button>
  </div>

  {#if showError}
    <Toast
      message="Email 格式不正確。"
      type="error"
      onDismiss={() => (showError = false)}
    />
  {/if}

  {#if showSuccess}
    <Toast
      message="帳號建立成功。"
      type="success"
      onDismiss={() => (showSuccess = false)}
    />
  {/if}
</div>
