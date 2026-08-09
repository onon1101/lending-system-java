<script lang="ts">import type { Snippet } from 'svelte'; import AppHeader from './AppHeader.svelte'; import AppSidebar from './AppSidebar.svelte'; let { children }: { children: Snippet } = $props();</script>
<div class="min-h-screen bg-slate-950 text-slate-100"><AppHeader /><AppSidebar /><main>{@render children()}</main></div>
