<script lang="ts">
  let {
    file = $bindable<File | null>(null),
    currentImageUrl = null,
    disabled = false,
  }: {
    file?: File | null;
    currentImageUrl?: string | null;
    disabled?: boolean;
  } = $props();

  const allowedTypes = new Set(["image/jpeg", "image/png", "image/webp"]);
  const maxSize = 5 * 1024 * 1024;

  let previewUrl = $state<string | null>(null);
  let errorMessage = $state("");
  let failedUrl = $state<string | null>(null);

  const displayedUrl = $derived(previewUrl ?? currentImageUrl);

  $effect(() => {
    const selected = file;
    const url = selected ? URL.createObjectURL(selected) : null;

    previewUrl = url;

    return () => {
      if (url) URL.revokeObjectURL(url);
    };
  });

  function selectFile(event: Event) {
    const input = event.currentTarget as HTMLInputElement;
    const selected = input.files?.[0];
    input.value = "";

    if (!selected) return;

    errorMessage = "";

    if (!allowedTypes.has(selected.type)) {
      errorMessage = "請選擇 JPG、PNG 或 WebP 圖片。";
      return;
    }

    if (selected.size === 0 || selected.size > maxSize) {
      errorMessage = "圖片不可為空，且大小不得超過 5 MB。";
      return;
    }

    file = selected;
  }
</script>

<div class="space-y-3">
  <p class="font-medium">物品圖片</p>

  <div
    class="flex aspect-[4/3] max-w-lg items-center justify-center
      overflow-hidden rounded-xl border border-white/10 bg-slate-800"
  >
    {#if displayedUrl && failedUrl !== displayedUrl}
      {#key displayedUrl}
        <img
          src={displayedUrl}
          alt="物品圖片預覽"
          class="h-full w-full object-contain"
          onerror={() => {
            failedUrl = displayedUrl;
          }}
        />
      {/key}
    {:else}
      <span class="text-sm text-slate-400">
        {displayedUrl ? "圖片無法顯示" : "尚未選擇圖片"}
      </span>
    {/if}
  </div>

  <label
    class="inline-flex cursor-pointer rounded-lg border
      border-white/15 px-4 py-2 hover:bg-white/5"
    class:opacity-50={disabled}
  >
    {currentImageUrl ? "選擇新圖片" : "選擇圖片"}
    <input
      type="file"
      accept="image/jpeg,image/png,image/webp"
      class="sr-only"
      {disabled}
      onchange={selectFile}
    />
  </label>

  {#if file}
    <div class="flex items-center gap-3 text-sm">
      <span class="break-all text-slate-300">{file.name}</span>
      <button
        type="button"
        class="shrink-0 text-sky-400 hover:underline"
        {disabled}
        onclick={() => {
          file = null;
          errorMessage = "";
        }}
      >
        取消選取
      </button>
    </div>
  {/if}

  <p class="text-sm text-slate-400">
    支援 JPG、PNG、WebP，最大 5 MB。選取後可先預覽。
  </p>

  {#if errorMessage}
    <p role="alert" class="text-sm text-red-400">{errorMessage}</p>
  {/if}
</div>
