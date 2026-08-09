<script lang="ts">
	import { onMount } from 'svelte';
	import { ApiError } from '$lib/infrastructure/http/api-error';
	import { Alert, LoadingIndicator } from '$lib/shared/components';
	import { confirmEmail } from '../api/confirm-email';
	import { getAuthErrorMessage } from '../utils/auth-error-messages';
	let { token }: { token: string } = $props();
	let loading = $state(true); let message = $state(''); let errorMessage = $state('');
	onMount(async () => {
		if (!token) { errorMessage = 'Email 驗證連結缺少 token。'; loading = false; return; }
		try { message = (await confirmEmail({ token })).message; }
		catch (error) { errorMessage = error instanceof ApiError ? getAuthErrorMessage(error.errorCode) : '目前無法連線至驗證服務。'; }
		finally { loading = false; }
	});
</script>
{#if loading}<LoadingIndicator />{:else if errorMessage}<Alert message={errorMessage} />{:else}<Alert message={message} type="success" />{/if}
