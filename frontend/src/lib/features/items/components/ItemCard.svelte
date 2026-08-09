<script lang="ts">import type { ItemDetail } from '../types/item.types'; let { item }: { item: ItemDetail } = $props();</script>
<a href={`/items/${item.itemId}`} class="block rounded-xl border border-white/10 p-4 hover:bg-white/5"><h2 class="font-semibold">{item.name}</h2><p class="mt-1 text-sm text-slate-400">{item.description}</p></a>
