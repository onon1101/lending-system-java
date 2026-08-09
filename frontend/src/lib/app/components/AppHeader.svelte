<script lang="ts">
	import { goto } from '$app/navigation';
	import { logout } from '$lib/features/auth/api/logout';
	import { clearTokens, getRefreshToken } from '$lib/infrastructure/storage/token-storage';
	let busy = $state(false);
	async function signOut() {
		busy = true;
		const refreshToken = getRefreshToken();
		try { if (refreshToken) await logout({ refreshToken }); } catch { /* Local logout still completes. */ }
		finally { clearTokens(); busy = false; await goto('/login'); }
	}
</script>
<header class="border-b border-white/10 bg-slate-950"><div class="mx-auto flex max-w-6xl items-center justify-between px-4 py-4 sm:px-6"><a href="/dashboard" class="font-bold text-white">Lending System</a><button onclick={signOut} disabled={busy} class="rounded-lg border border-white/15 px-3 py-2 text-sm text-slate-200 hover:bg-white/10 disabled:opacity-50">{busy ? '登出中...' : '登出'}</button></div></header>
