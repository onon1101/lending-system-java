<script lang="ts">
	import type { Snippet } from 'svelte';
	type Props = { title: string; description: string; children: Snippet };
	let { title, description, children }: Props = $props();
</script>

<section class="relative w-full max-w-md rounded-3xl border border-white/10 bg-white/[0.07] p-6 shadow-2xl shadow-black/30 backdrop-blur-xl sm:p-8">
	<header class="mb-8 text-center">
		<div class="mx-auto mb-5 grid size-14 place-items-center rounded-2xl bg-sky-500 text-xl font-bold text-white">LS</div>
		<p class="text-sm font-semibold tracking-[0.22em] text-sky-400 uppercase">Lending System</p>
		<h1 class="mt-3 text-3xl font-bold tracking-tight text-white">{title}</h1>
		<p class="mt-2 text-sm leading-6 text-slate-400">{description}</p>
	</header>
	{@render children()}
</section>
