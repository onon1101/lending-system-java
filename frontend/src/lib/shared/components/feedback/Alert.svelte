<script lang="ts">
	type AlertType = 'error' | 'success' | 'info';
	type Props = { message: string; type?: AlertType };
	let { message, type = 'error' }: Props = $props();
	const styles: Record<AlertType, string> = {
		error: 'border-rose-400/20 bg-rose-500/10 text-rose-200',
		success: 'border-emerald-400/20 bg-emerald-500/10 text-emerald-200',
		info: 'border-sky-400/20 bg-sky-500/10 text-sky-200'
	};
</script>

<div class="mb-5 rounded-xl border p-4 text-sm {styles[type]}" role={type === 'error' ? 'alert' : 'status'}>
	{message}
</div>
