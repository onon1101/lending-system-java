<script lang="ts">
  import type { ItemDetail } from "../types/item.types";

  let { item }: { item: ItemDetail } = $props();
  let failedUrl = $state<string | null>(null);
</script>

<a
  href={`/items/${item.itemId}`}
  class="block overflow-hidden rounded-xl border border-white/10 hover:bg-white/5"
>
  <div class="flex aspect-[4/3] items-center justify-center bg-slate-800">
    {#if item.imageUrl && failedUrl !== item.imageUrl}
      {#key item.imageUrl}
        <img
          src={item.imageUrl}
          alt={`${item.name}的封面`}
          class="h-full w-full object-contain"
          loading="lazy"
          decoding="async"
          onerror={() => {
            failedUrl = item.imageUrl ?? null;
          }}
        />
      {/key}
    {:else}
      <span class="text-sm text-slate-400">
        {item.imageUrl ? "封面暫時無法顯示" : "尚未提供封面"}
      </span>
    {/if}
  </div>

  <div class="p-4">
    <h2 class="font-semibold">{item.name}</h2>
    <p class="mt-1 line-clamp-2 text-sm text-slate-400">
      {item.description}
    </p>
  </div>
</a>
