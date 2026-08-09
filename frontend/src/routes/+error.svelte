<script lang="ts">import { page } from '$app/state';</script>
<svelte:head><title>發生錯誤 | Lending System</title></svelte:head>
<main class="grid min-h-screen place-items-center bg-slate-950 p-6 text-center text-white"><div><p class="text-5xl font-bold text-sky-400">{page.status}</p><h1 class="mt-4 text-2xl font-semibold">頁面無法顯示</h1><p class="mt-2 text-slate-400">{page.error?.message ?? '發生未預期錯誤'}</p><a href="/" class="mt-6 inline-block text-sky-400">返回首頁</a></div></main>
