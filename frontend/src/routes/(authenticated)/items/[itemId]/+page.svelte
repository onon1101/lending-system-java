<script lang="ts">import { page } from '$app/state'; import { ItemDetail } from '$lib/features/items'; const itemId = $derived(page.params.itemId ?? '');</script>
<svelte:head><title>物品資料 | Lending System</title></svelte:head>
<div class="w-full px-4 py-4 sm:px-6">
  <ItemDetail {itemId} />
</div>
