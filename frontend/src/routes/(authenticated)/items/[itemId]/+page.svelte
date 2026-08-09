<script lang="ts">import { page } from '$app/state'; import { ItemDetail } from '$lib/features/items'; import { PageContainer } from '$lib/shared/components'; const itemId = $derived(page.params.itemId ?? '');</script>
<svelte:head><title>物品資料 | Lending System</title></svelte:head><PageContainer><ItemDetail {itemId} /></PageContainer>
