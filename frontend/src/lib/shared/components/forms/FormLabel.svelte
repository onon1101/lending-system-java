<script lang="ts">
	import type { Snippet } from 'svelte';
	type Props = { for: string; children: Snippet };
	let { for: htmlFor, children }: Props = $props();
</script>

<label for={htmlFor} class="mb-2 block text-sm font-medium text-slate-200">{@render children()}</label>
