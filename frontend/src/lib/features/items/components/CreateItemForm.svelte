<script lang="ts">
	import { goto } from '$app/navigation';
	import { ApiError } from '$lib/infrastructure/http/api-error';
	import { Alert, FormInput, FormLabel, SubmitButton } from '$lib/shared/components';
	import { createItem } from '../api/create-item';
	let name = $state(''); let description = $state(''); let isSubmitting = $state(false); let errorMessage = $state('');
	async function submit(event: SubmitEvent) {
		event.preventDefault(); errorMessage = '';
		if (!name.trim()) { errorMessage = '請輸入物品名稱。'; return; }
		isSubmitting = true;
		try { const result = await createItem({ name: name.trim(), description: description.trim() }); await goto(`/items/${result.itemId}`); }
		catch (error) { errorMessage = error instanceof ApiError ? `建立失敗（${error.errorCode ?? error.status}）` : '目前無法連線至物品服務。'; }
		finally { isSubmitting = false; }
	}
</script>
{#if errorMessage}<Alert message={errorMessage} />{/if}<form class="max-w-xl space-y-5" onsubmit={submit}><div><FormLabel for="name">物品名稱</FormLabel><FormInput id="name" bind:value={name} maxlength={100} required /></div><div><FormLabel for="description">物品描述</FormLabel><FormInput id="description" bind:value={description} maxlength={2000} /></div><SubmitButton {isSubmitting} label="建立物品" /></form>
