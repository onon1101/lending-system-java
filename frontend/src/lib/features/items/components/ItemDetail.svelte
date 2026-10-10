<script lang="ts">
  import { onMount } from "svelte";
  import { Alert, LoadingIndicator } from "$lib/shared/components";
  import { formatDate } from "$lib/shared/utils/format-date";
  import { getItem } from "../api/get-item";
  import { uploadItemImage } from "../api/upload-item-image";
  import ItemImagePicker from "./ItemImagePicker.svelte";
  import type { ItemDetail as Item } from "../types/item.types";

  let { itemId }: { itemId: string } = $props();
  let item = $state<Item | null>(null);
  let loadError = $state("");
  let uploadError = $state("");
  let successMessage = $state("");
  let selectedFile = $state<File | null>(null);
  let uploading = $state(false);
  let failedUrl = $state<string | null>(null);

  onMount(() => {
    let active = true;
    void getItem(itemId)
      .then((result) => { if (active) item = result; })
      .catch(() => { if (active) loadError = "無法取得物品資料。"; });
    return () => { active = false; };
  });

  async function upload(event: SubmitEvent) {
    event.preventDefault();
    if (!item?.ownedByCurrentUser || !selectedFile || uploading) return;
    uploading = true;
    uploadError = "";
    successMessage = "";
    try {
      const result = await uploadItemImage(item.itemId, selectedFile);
      item = { ...item, imageUrl: result.imageUrl };
      failedUrl = null;
      selectedFile = null;
      successMessage = "圖片已更新。";
    } catch {
      uploadError = "圖片上傳失敗，請確認連線後重試。";
    } finally {
      uploading = false;
    }
  }
</script>

{#if loadError}
  <Alert message={loadError} />
{:else if !item}
  <LoadingIndicator />
{:else}
  <article class="rounded-2xl border border-white/10 bg-white/5 p-6">
    <h1 class="text-2xl font-bold">{item.name}</h1>
    <div class="mt-5">
      {#if item.ownedByCurrentUser}
        <form class="space-y-4" onsubmit={upload}>
          <ItemImagePicker bind:file={selectedFile} currentImageUrl={item.imageUrl ?? null} disabled={uploading} />
          {#if uploadError}<Alert message={uploadError} />{/if}
          {#if successMessage && !selectedFile}
            <p role="status" class="text-sm text-emerald-400">{successMessage}</p>
          {/if}
          <button type="submit" disabled={uploading || !selectedFile}
            class="rounded-lg bg-sky-500 px-4 py-2 font-semibold text-white hover:bg-sky-600 disabled:cursor-not-allowed disabled:opacity-50">
            {uploading ? "上傳中…" : item.imageUrl ? "更新圖片" : "上傳圖片"}
          </button>
        </form>
      {:else}
        <div class="flex aspect-[4/3] max-w-lg items-center justify-center overflow-hidden rounded-xl bg-slate-800">
          {#if item.imageUrl && failedUrl !== item.imageUrl}
            <img src={item.imageUrl} alt={`${item.name}的圖片`} class="h-full w-full object-contain"
              onerror={() => { failedUrl = item?.imageUrl ?? null; }} />
          {:else}
            <span class="text-sm text-slate-400">{item.imageUrl ? "圖片無法顯示" : "尚未提供圖片"}</span>
          {/if}
        </div>
      {/if}
    </div>
    <p class="mt-5 text-slate-300">{item.description || "沒有描述"}</p>
    <dl class="mt-6 grid gap-3 text-sm">
      <div><dt class="text-slate-500">擁有者</dt><dd>{item.ownerUsername}</dd></div>
      <div><dt class="text-slate-500">狀態</dt><dd>{item.availability}</dd></div>
      <div><dt class="text-slate-500">更新時間</dt><dd>{formatDate(item.updatedAt)}</dd></div>
    </dl>
  </article>
{/if}
