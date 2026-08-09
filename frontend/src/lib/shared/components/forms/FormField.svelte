<script lang="ts">
	import type { Snippet } from 'svelte';
	type Props = { label: string; for: string; children: Snippet };
	let { label, for: htmlFor, children }: Props = $props();
</script>

<div><label for={htmlFor} class="mb-2 block text-sm font-medium text-slate-200">{label}</label>{@render children()}</div>
