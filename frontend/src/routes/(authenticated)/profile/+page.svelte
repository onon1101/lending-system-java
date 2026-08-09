<script lang="ts">
	import { onMount } from 'svelte'; import { getCurrentUser } from '$lib/features/users'; import { Alert, LoadingIndicator, PageContainer } from '$lib/shared/components'; import type { CurrentUser } from '$lib/features/users';
	let user = $state<CurrentUser | null>(null); let errorMessage = $state('');
	onMount(async () => { try { user = await getCurrentUser(); } catch { errorMessage = '無法取得目前使用者資料。'; } });
</script>
<svelte:head><title>個人資料 | Lending System</title></svelte:head><PageContainer><h1 class="mb-6 text-3xl font-bold">個人資料</h1>{#if errorMessage}<Alert message={errorMessage} />{:else if !user}<LoadingIndicator />{:else}<dl class="max-w-xl space-y-4 rounded-2xl border border-white/10 bg-white/5 p-6"><div><dt class="text-sm text-slate-500">帳號</dt><dd>{user.username}</dd></div><div><dt class="text-sm text-slate-500">Email</dt><dd>{user.email}</dd></div><div><dt class="text-sm text-slate-500">狀態</dt><dd>{user.status}</dd></div></dl>{/if}</PageContainer>
