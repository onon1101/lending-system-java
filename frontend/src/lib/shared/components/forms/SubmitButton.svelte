<script lang="ts">
	type Props = { isSubmitting?: boolean; label?: string; loadingLabel?: string; disabled?: boolean };
	let { isSubmitting = false, label = '送出', loadingLabel = '處理中...', disabled = false }: Props = $props();
</script>

<button type="submit" disabled={disabled || isSubmitting} aria-busy={isSubmitting} class="flex w-full items-center justify-center gap-2 rounded-xl bg-sky-500 px-4 py-3 font-semibold text-white shadow-lg shadow-sky-500/20 transition hover:bg-sky-400 disabled:cursor-not-allowed disabled:opacity-60">
	{isSubmitting ? loadingLabel : label}
</button>
