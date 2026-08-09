<script lang="ts">
	import { ApiError } from '$lib/infrastructure/http/api-error';
	import { Alert, FormInput, FormLabel, SubmitButton } from '$lib/shared/components';
	import { resetPassword } from '../api/reset-password';
	import { validateNewPassword } from '../schemas/password.schema';
	import { getAuthErrorMessage } from '../utils/auth-error-messages';
	let { token }: { token: string } = $props();
	let password = $state(''); let confirmation = $state(''); let isSubmitting = $state(false); let message = $state(''); let errorMessage = $state('');
	async function submit(event: SubmitEvent) {
		event.preventDefault(); message = ''; errorMessage = '';
		if (!token) { errorMessage = '重設密碼連結缺少 token。'; return; }
		const validationError = validateNewPassword(password);
		if (validationError) { errorMessage = validationError; return; }
		if (password !== confirmation) { errorMessage = '兩次輸入的密碼不一致。'; return; }
		isSubmitting = true;
		try { const result = await resetPassword({ resetToken: token, newPassword: password }); message = result.message; password = ''; confirmation = ''; }
		catch (error) { errorMessage = error instanceof ApiError ? getAuthErrorMessage(error.errorCode) : '目前無法連線至服務。'; }
		finally { isSubmitting = false; }
	}
</script>
{#if errorMessage}<Alert message={errorMessage} />{/if}{#if message}<Alert message={message} type="success" />{/if}
<form class="space-y-5" onsubmit={submit}><div><FormLabel for="new-password">新密碼</FormLabel><FormInput id="new-password" type="password" bind:value={password} minlength={12} maxlength={128} autocomplete="new-password" required /></div><div><FormLabel for="confirmation">確認新密碼</FormLabel><FormInput id="confirmation" type="password" bind:value={confirmation} minlength={12} maxlength={128} autocomplete="new-password" required /></div><SubmitButton {isSubmitting} label="重設密碼" /></form>
