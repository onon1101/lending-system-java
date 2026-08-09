<script lang="ts">
	type Props = { visible: boolean; class?: string };
	let { visible, class: className = 'size-5' }: Props = $props();
</script>

<svg class={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
	{#if visible}
		<path d="m3 3 18 18"></path><path d="M10.6 10.7a2 2 0 0 0 2.7 2.7"></path>
		<path d="M9.9 4.2A10.6 10.6 0 0 1 12 4c5.5 0 9 5 9 5a16.6 16.6 0 0 1-2.1 2.7"></path>
		<path d="M6.6 6.6C4.4 8.1 3 10 3 10s3.5 5 9 5a9.8 9.8 0 0 0 3.4-.6"></path>
	{:else}
		<path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12Z"></path><circle cx="12" cy="12" r="3"></circle>
	{/if}
</svg>
