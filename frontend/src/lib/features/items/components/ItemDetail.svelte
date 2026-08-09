<script lang="ts">
	import { onMount } from 'svelte';
	import { Alert, LoadingIndicator } from '$lib/shared/components';
	import { formatDate } from '$lib/shared/utils/format-date';
	import { getItem } from '../api/get-item';
	import type { ItemDetail as Item } from '../types/item.types';
	let { itemId }: { itemId: string } = $props(); let item = $state<Item | null>(null); let errorMessage = $state('');
	onMount(async () => { try { item = await getItem(itemId); } catch { errorMessage = '無法取得物品資料。'; } });
</script>
{#if errorMessage}<Alert message={errorMessage} />{:else if !item}<LoadingIndicator />{:else}<article class="rounded-2xl border border-white/10 bg-white/5 p-6"><h1 class="text-2xl font-bold">{item.name}</h1><p class="mt-3 text-slate-300">{item.description || '沒有描述'}</p><dl class="mt-6 grid gap-3 text-sm"><div><dt class="text-slate-500">擁有者</dt><dd>{item.ownerUsername}</dd></div><div><dt class="text-slate-500">狀態</dt><dd>{item.availability}</dd></div><div><dt class="text-slate-500">更新時間</dt><dd>{formatDate(item.updatedAt)}</dd></div></dl></article>{/if}
