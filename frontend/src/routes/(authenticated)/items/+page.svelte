<script lang="ts">
  import { onMount } from "svelte";
  import { listItems } from "$lib/features/items";
  import type { ItemDetail } from "$lib/features/items/types/item.types";
  import ItemCard from "$lib/features/items/components/ItemCard.svelte";
  import {
    Alert,
    EmptyState,
    LoadingIndicator,
    PageContainer,
  } from "$lib/shared/components";

  let items = $state<ItemDetail[]>([]);
  let currentPage = $state(0);
  let hasNext = $state(false);
  let loading = $state(true);
  let errorMessage = $state("");
  let controller: AbortController | undefined;

  async function loadItems(targetPage: number) {
    controller?.abort();
    const requestController = new AbortController();
    controller = requestController;

    loading = true;
    errorMessage = "";

    try {
      const result = await listItems(targetPage, 20, requestController.signal);

      if (requestController.signal.aborted) return;

      items = result.items;
      currentPage = result.response.page;
      hasNext = result.response.hasNext;
    } catch {
      if (requestController.signal.aborted) return;

      errorMessage = "無法載入物品列表，請稍後重試。";
    } finally {
      if (controller === requestController) {
        loading = false;
      }
    }
  }

  onMount(() => {
    void loadItems(0);
    return () => controller?.abort();
  });
</script>

<svelte:head>
  <title>物品 | Lending System</title>
</svelte:head>

<PageContainer>
  <div class="mb-6 flex flex-wrap items-center justify-between gap-4">
    <h1 class="text-3xl font-bold">物品</h1>

    <a
      href="/items/create"
      class="rounded-lg bg-sky-500 px-4 py-2 font-semibold text-white hover:bg-sky-600"
    >
      建立物品
    </a>
  </div>

  <section aria-label="物品列表" aria-busy={loading}>
    {#if loading}
      <LoadingIndicator />
    {:else if errorMessage}
      <Alert message={errorMessage} />

      <button
        type="button"
        class="rounded-lg border border-white/15 px-4 py-2 hover:bg-white/5"
        onclick={() => loadItems(currentPage)}
      >
        重新載入
      </button>
    {:else if items.length === 0}
      <EmptyState message="目前沒有可查看的物品。" />
    {:else}
      <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {#each items as item (item.itemId)}
          <ItemCard {item} />
        {/each}
      </div>
    {/if}
  </section>

  <nav
    aria-label="物品列表分頁"
    class="mt-6 flex items-center justify-center gap-4"
  >
    <button
      type="button"
      disabled={loading || currentPage === 0}
      onclick={() => loadItems(currentPage - 1)}
      class="rounded-lg border border-white/15 px-4 py-2 hover:bg-white/5 disabled:cursor-not-allowed disabled:opacity-40"
    >
      上一頁
    </button>

    <span class="text-sm text-slate-400" aria-live="polite">
      第 {currentPage + 1} 頁
    </span>

    <button
      type="button"
      disabled={loading || !hasNext}
      onclick={() => loadItems(currentPage + 1)}
      class="rounded-lg border border-white/15 px-4 py-2 hover:bg-white/5 disabled:cursor-not-allowed disabled:opacity-40"
    >
      下一頁
    </button>
  </nav>
</PageContainer>
