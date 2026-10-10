<script lang="ts">
  import { goto } from "$app/navigation";
  import {
    ApiContractError,
    ApiError,
  } from "$lib/infrastructure/http/api-error";
  import {
    Alert,
    FormInput,
    FormLabel,
    SubmitButton,
  } from "$lib/shared/components";
  import { createItem } from "../api/create-item";
  import { uploadItemImage } from "../api/upload-item-image";
  import ItemImagePicker from "./ItemImagePicker.svelte";
  import { getItemDraftState } from "../state/item-draft-state.svelte";
  import type { CreateItemRequest } from "../types/item.types";

  const draft = getItemDraftState();

  let selectedFile = $state<File | null>(null);
  let uploadedFile = $state<File | null>(null);

  let isSubmitting = $state(false);
  let errorMessage = $state("");

  let pendingRequest: {
    body: CreateItemRequest;
    key: string;
  } | null = null;

  let createdItemId = $state<string | null>(null);

  async function submit(event: SubmitEvent) {
    event.preventDefault();

    if (isSubmitting) return;

    errorMessage = "";

    const body: CreateItemRequest = {
      name: draft.name.trim(),
      description: draft.description.trim(),
    };

    if (!createdItemId && !body.name) {
      errorMessage = "請輸入物品名稱。";
      return;
    }

    isSubmitting = true;

    try {
      if (!createdItemId) {
        const sameRequest =
          pendingRequest !== null &&
          pendingRequest.body.name === body.name &&
          pendingRequest.body.description === body.description;

        if (!sameRequest) {
          pendingRequest = {
            body,
            key: crypto.randomUUID(),
          };
        }
        const pending = pendingRequest!;
        const result = await createItem(pending.body, pending.key);

        createdItemId = result.itemId;
        pendingRequest = null;
      }

      if (selectedFile && selectedFile !== uploadedFile) {
        const fileToUpload = selectedFile;
        try {
          await uploadItemImage(createdItemId!, fileToUpload);
          uploadedFile = fileToUpload;
        } catch {
          errorMessage =
            "物品已建立，但圖片上傳失敗。請重試，或取消選圖後前往物品頁面。";
          return;
        }
      }

      await goto(`/items/${createdItemId}`);
      draft.clear();
    } catch (error) {
      if (createdItemId) {
        errorMessage = "物品已建立，但頁面跳轉失敗。請再次按下按鈕重試跳轉。";
      } else if (error instanceof ApiError) {
        switch (error.errorCode) {
          case "Idempotency.RequestInProgress":
            errorMessage = "建立請求仍在處理中，請稍後重試。";
            break;

          case "Idempotency.KeyReusedWithDifferentRequest":
            errorMessage = "請求識別碼與先前內容不一致，請確認後再送出。";
            break;

          case "Validation.InvalidRequest":
            errorMessage = "請求資料驗證失敗，請確認物品資料。";
            break;

          default:
            errorMessage = `建立失敗（${error.errorCode ?? error.status}）`;
        }
      } else if (error instanceof ApiContractError) {
        errorMessage = "伺服器回應格式異常，請稍後重試。";
      } else {
        errorMessage = "請求未完成，請確認連線後重試。";
      }
    } finally {
      isSubmitting = false;
    }
  }
</script>

{#if errorMessage}
  <Alert message={errorMessage} />
{/if}

<form class="max-w-xl space-y-5" onsubmit={submit}>
  <div>
    <FormLabel for="name">物品名稱</FormLabel><FormInput
      id="name"
      bind:value={draft.name}
      maxlength={100}
      required={!createdItemId}
      disabled={isSubmitting || createdItemId !== null}
    />
  </div>
  <div>
    <FormLabel for="description">物品描述</FormLabel><FormInput
      id="description"
      bind:value={draft.description}
      maxlength={2000}
      disabled={isSubmitting || createdItemId !== null}
    />
  </div>
  <ItemImagePicker bind:file={selectedFile} disabled={isSubmitting} />

  {#if createdItemId}
    <p role="status" class="text-sm text-emerald-400">
      物品已建立，接下來可完成圖片上傳。
    </p>
  {/if}

  <SubmitButton
    {isSubmitting}
    label={createdItemId
      ? selectedFile && selectedFile !== uploadedFile
        ? "上傳圖片並前往物品頁面"
        : "前往物品頁面"
      : selectedFile
        ? "建立物品並上傳圖片"
        : "建立物品"}
  />
</form>
