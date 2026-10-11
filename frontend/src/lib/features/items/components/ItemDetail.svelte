<script lang="ts">
  import { onMount } from "svelte";
  import { Alert, LoadingIndicator } from "$lib/shared/components";
  import { formatDate } from "$lib/shared/utils/format-date";
  import { getItem } from "../api/get-item";
  import type { ItemDetail as Item } from "../types/item.types";

  let { itemId }: { itemId: string } = $props();
  let item = $state<Item | null>(null);
  let loadError = $state("");
  let failedUrl = $state<string | null>(null);

  onMount(() => {
    let active = true;
    void getItem(itemId)
      .then((result) => { if (active) item = result; })
      .catch(() => { if (active) loadError = "無法取得物品資料。"; });
    return () => { active = false; };
  });

</script>

{#if loadError}
  <Alert message={loadError} />
{:else if !item}
  <LoadingIndicator />
{:else}
  <article
    class="grid items-start gap-6 lg:grid-cols-[minmax(0,3fr)_minmax(0,7fr)]"
  >
    <!-- 左側：物品描述與相關資訊 -->
    <div class="min-w-0 space-y-8 rounded-2xl border border-white/10 bg-white/5 p-5 sm:p-6">
      <header class="space-y-4">
        <div class="flex flex-wrap items-center gap-3">
          <span
            class={`rounded-full border px-3 py-1 text-xs font-medium ${
              item.availability === "AVAILABLE"
                ? "border-emerald-400/20 bg-emerald-400/10 text-emerald-300"
                : "border-slate-400/20 bg-slate-400/10 text-slate-300"
            }`}
          >
            {item.availability === "AVAILABLE"
              ? "可借閱"
              : item.availability === "UNAVAILABLE"
                ? "目前不可借閱"
                : item.availability}
          </span>

          {#if item.ownedByCurrentUser}
            <span class="text-xs text-slate-400">你的物品</span>
          {/if}
        </div>

        <h1 class="break-words text-3xl font-bold tracking-tight">
          {item.name}
        </h1>
      </header>

      <section aria-labelledby="item-description-heading" class="space-y-3">
        <h2
          id="item-description-heading"
          class="text-lg font-semibold"
        >
          物品描述
        </h2>

        <p class="whitespace-pre-wrap break-words leading-7 text-slate-300">
          {item.description || "尚未提供物品描述。"}
        </p>
      </section>

      <section
        aria-labelledby="item-information-heading"
        class="border-t border-white/10 pt-6"
      >
        <h2
          id="item-information-heading"
          class="text-lg font-semibold"
        >
          基本資訊
        </h2>

        <dl class="mt-4 space-y-4 text-sm">
          <div class="grid grid-cols-[5rem_minmax(0,1fr)] gap-4">
            <dt class="text-slate-400">擁有者</dt>
            <dd class="break-words text-slate-200">
              {item.ownerUsername}
            </dd>
          </div>

          <div class="grid grid-cols-[5rem_minmax(0,1fr)] gap-4">
            <dt class="text-slate-400">是否可用</dt>
            <dd class="text-slate-200">
              {item.availability === "AVAILABLE"
                ? "可用"
                : item.availability === "UNAVAILABLE"
                  ? "不可用"
                  : item.availability}
            </dd>
          </div>

          <div class="grid grid-cols-[5rem_minmax(0,1fr)] gap-4">
            <dt class="text-slate-400">建立時間</dt>
            <dd class="break-words text-slate-200">
              {formatDate(item.createdAt)}
            </dd>
          </div>

          <div class="grid grid-cols-[5rem_minmax(0,1fr)] gap-4">
            <dt class="text-slate-400">更新時間</dt>
            <dd class="break-words text-slate-200">
              {formatDate(item.updatedAt)}
            </dd>
          </div>
        </dl>
      </section>

      <section
        aria-labelledby="item-history-heading"
        class="border-t border-white/10 pt-6"
      >
        <h2 id="item-history-heading" class="text-lg font-semibold">
          借閱歷史
        </h2>

        <!-- 串接 API 後，在此放入借閱紀錄清單 -->
        <div
          class="mt-4 rounded-xl border border-dashed border-white/15
            bg-slate-900/30 px-4 py-6"
        >
          <p class="text-sm text-slate-400">
            尚未串接借閱歷史。
          </p>
        </div>
      </section>
    </div>

    <!-- 右側：純封面展示，齒輪連結至封面設定頁 -->
    <section
      aria-label="物品封面"
      class="relative min-w-0 lg:sticky lg:top-4"
    >
      <div
        class="flex aspect-[4/3] w-full items-start justify-center
          lg:aspect-auto lg:h-[max(12rem,calc(100dvh-10rem))]"
      >
        <div class="relative flex max-h-full max-w-full items-center justify-center">
        {#if item.imageUrl && failedUrl !== item.imageUrl}
          <img
            src={item.imageUrl}
            alt={`${item.name}的封面`}
            class="block h-auto w-auto max-h-[70dvh] max-w-full object-contain
              lg:max-h-[max(12rem,calc(100dvh-10rem))]"
            onerror={() => { failedUrl = item?.imageUrl ?? null; }}
          />
        {:else}
          <svg aria-label={item.imageUrl ? "封面無法顯示" : "尚未提供封面"}
            role="img" viewBox="0 0 24 24" fill="none" stroke="currentColor"
            stroke-width="1.5" class="h-16 w-16 text-slate-600">
            <rect x="3" y="3" width="18" height="18" rx="3" />
            <circle cx="8.5" cy="8.5" r="1.5" />
            <path d="m3 17 5-5 4 4 4-6 5 7" />
          </svg>
        {/if}
      {#if item.ownedByCurrentUser}
        <a
          href={`/items/${encodeURIComponent(item.itemId)}/cover`}
          aria-label="變更物品封面"
          title="變更物品封面"
          class="absolute right-3 top-3 flex h-10 w-10 items-center justify-center
            rounded-full border border-white/15 bg-slate-950/75 text-slate-200
            shadow-lg backdrop-blur hover:bg-slate-800 hover:text-white
            focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-sky-400"
        >
          <svg aria-hidden="true" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="1.5" stroke-linecap="round"
            stroke-linejoin="round" class="h-5 w-5">
            <path d="m9 3-.5 3-2 .9L4 6l-2 3 2 2v2l-2 2 2 3 2.5-.9 2 .9.5 3h4l.5-3 2-.9L18 18l2-3-2-2v-2l2-2-2-3-2.5.9-2-.9L13 3Z" />
            <circle cx="11" cy="12" r="3" />
          </svg>
        </a>
      {/if}
        </div>
      </div>
    </section>
  </article>
{/if}
