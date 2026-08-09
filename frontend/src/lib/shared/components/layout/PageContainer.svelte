<script lang="ts">import type { Snippet } from 'svelte'; let { children }: { children: Snippet } = $props();</script>
<div class="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6">{@render children()}</div>
