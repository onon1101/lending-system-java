<script lang="ts">
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { Alert, LoadingIndicator } from "$lib/shared/components";
  import { getItem } from "../api/get-item";
  import { uploadCover } from "../api/upload-cover";
  import ItemCoverPicker from "./ItemCoverPicker.svelte";
  import type { ItemDetail } from "../types/item.types";

  let { itemId }: { itemId: string } = $props();
  let item = $state<ItemDetail | null>(null);
  let loadError = $state("");
  let uploadError = $state("");
  let selectedFile = $state<File | null>(null);
  let uploading = $state(false);
  let uploaded = $state(false);
  const detailUrl = $derived(`/items/${encodeURIComponent(itemId)}`);

  onMount(() => {
    let active = true;
    void getItem(itemId)
      .then((result) => { if (active) item = result; })
      .catch(() => { if (active) loadError = "無法取得物品資料。"; });
    return () => { active = false; };
  });

  async function upload(event: SubmitEvent) {
    event.preventDefault();
    if (!item?.ownedByCurrentUser || !selectedFile || uploading || uploaded) return;
    uploading = true;
    uploadError = "";
    try {
      const result = await uploadCover(item.itemId, selectedFile);
      item = { ...item, imageUrl: result.imageUrl };
      selectedFile = null;
      uploaded = true;
    } catch {
      uploadError = "封面上傳失敗，請確認連線後重試。";
    } finally {
      uploading = false;
    }
    if (uploaded) {
      try {
        await goto(detailUrl);
      } catch {
        // 封面已儲存，保留成功訊息與返回連結。
      }
    }
  }
</script>

<div class="mx-auto w-full max-w-4xl space-y-6 px-4 py-8 sm:px-6">
  <a href={detailUrl} class="text-sm text-sky-400 hover:underline">← 返回物品詳細頁</a>
  {#if loadError}
    <Alert message={loadError} />
  {:else if !item}
    <LoadingIndicator />
  {:else if !item.ownedByCurrentUser}
    <Alert message="只有物品擁有者可以變更封面。" />
  {:else}
    <section class="space-y-6 rounded-2xl border border-white/10 bg-white/5 p-5 sm:p-8">
      <header class="space-y-2">
        <h1 class="text-2xl font-bold">變更物品封面</h1>
        <p class="break-words text-slate-300">{item.name}</p>
      </header>
      <form class="space-y-4" onsubmit={upload}>
        <ItemCoverPicker bind:file={selectedFile} currentImageUrl={item.imageUrl ?? null}
          disabled={uploading || uploaded} fullWidth />
        {#if uploadError}<Alert message={uploadError} />{/if}
        {#if uploaded}<p role="status" class="text-sm text-emerald-400">封面已更新，請返回物品詳細頁。</p>{/if}
        <div class="flex justify-end">
          <button type="submit" disabled={uploading || uploaded || !selectedFile}
            class="rounded-lg bg-sky-500 px-4 py-2 font-semibold text-white hover:bg-sky-600
              disabled:cursor-not-allowed disabled:opacity-50">
            {uploading ? "上傳中…" : "儲存封面"}
          </button>
        </div>
      </form>
    </section>
  {/if}
</div>
