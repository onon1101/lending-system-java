<script lang="ts">
	import type { HTMLInputAttributes } from 'svelte/elements';
	type Props = Omit<HTMLInputAttributes, 'value' | 'class'> & {
		value?: string;
		hasTrailingAction?: boolean;
		class?: string;
	};
	let { value = $bindable(''), hasTrailingAction = false, class: className = '', ...attributes }: Props = $props();
</script>

<input
	{...attributes}
	bind:value
	class="block w-full rounded-xl border border-white/10 bg-slate-950/50 py-3 text-white outline-none transition placeholder:text-slate-600 hover:border-white/20 focus:border-sky-400 focus:ring-4 focus:ring-sky-400/10 disabled:cursor-not-allowed disabled:opacity-60 {hasTrailingAction ? 'pr-12 pl-4' : 'px-4'} {className}"
/>
