<script lang="ts">
	import { ApiError } from '$lib/infrastructure/http/api-error';
	import { Alert, FormInput, FormLabel, SubmitButton } from '$lib/shared/components';
	import { forgotPassword } from '../api/forgot-password';
	import { getAuthErrorMessage } from '../utils/auth-error-messages';
	let email = $state(''); let isSubmitting = $state(false); let message = $state(''); let errorMessage = $state('');
	async function submit(event: SubmitEvent) {
		event.preventDefault(); message = ''; errorMessage = '';
		if (!email.trim()) { errorMessage = '請輸入 Email。'; return; }
		isSubmitting = true;
		try { const result = await forgotPassword({ email: email.trim() }); message = result.message; }
		catch (error) { errorMessage = error instanceof ApiError ? getAuthErrorMessage(error.errorCode) : '目前無法連線至服務。'; }
		finally { isSubmitting = false; }
	}
</script>
{#if errorMessage}<Alert message={errorMessage} />{/if}{#if message}<Alert message={message} type="success" />{/if}
<form class="space-y-5" onsubmit={submit}><div><FormLabel for="email">Email</FormLabel><FormInput id="email" type="email" bind:value={email} maxlength={255} required autocomplete="email" /></div><SubmitButton {isSubmitting} label="寄送重設連結" /></form>
