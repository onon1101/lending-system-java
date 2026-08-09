<script lang="ts">import { navigationItems } from '../navigation/navigation-items';</script>
<nav class="flex gap-2 border-b border-white/10 bg-slate-900 px-4 py-3 sm:justify-center">{#each navigationItems as item}<a href={item.href} class="rounded-lg px-3 py-2 text-sm text-slate-300 hover:bg-white/10 hover:text-white">{item.label}</a>{/each}</nav>
