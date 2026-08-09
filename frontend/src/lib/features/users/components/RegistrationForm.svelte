<script lang="ts">
	import { ApiError } from '$lib/infrastructure/http/api-error';
	import { Alert, FormInput, FormLabel, SubmitButton } from '$lib/shared/components';
	import { registerUser } from '../api/register-user';
	let username = $state(''); let email = $state(''); let password = $state(''); let isSubmitting = $state(false); let message = $state(''); let errorMessage = $state('');
	async function submit(event: SubmitEvent) {
		event.preventDefault(); message = ''; errorMessage = '';
		if (!username.trim() || !email.trim() || !password) { errorMessage = '請完整填寫註冊資料。'; return; }
		isSubmitting = true;
		try { const result = await registerUser({ username: username.trim(), email: email.trim(), password }); message = `註冊成功，使用者 ID：${result.userId}`; password = ''; }
		catch (error) { errorMessage = error instanceof ApiError ? `註冊失敗（${error.errorCode ?? error.status}）` : '目前無法連線至註冊服務。'; }
		finally { isSubmitting = false; }
	}
</script>
{#if errorMessage}<Alert message={errorMessage} />{/if}{#if message}<Alert message={message} type="success" />{/if}
<form class="space-y-5" onsubmit={submit}><div><FormLabel for="username">帳號</FormLabel><FormInput id="username" bind:value={username} maxlength={50} autocomplete="username" required /></div><div><FormLabel for="email">Email</FormLabel><FormInput id="email" type="email" bind:value={email} maxlength={255} autocomplete="email" required /></div><div><FormLabel for="password">密碼</FormLabel><FormInput id="password" type="password" bind:value={password} maxlength={100} autocomplete="new-password" required /></div><SubmitButton {isSubmitting} label="建立帳號" /></form>
