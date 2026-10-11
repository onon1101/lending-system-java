<script lang="ts">
  import { page } from "$app/state";
  import ItemCoverSettings from "$lib/features/items/components/ItemCoverSettings.svelte";
  const itemId = $derived(page.params.itemId ?? "");
</script>

<svelte:head><title>變更物品封面 | Lending System</title></svelte:head>
{#key itemId}
  <ItemCoverSettings {itemId} />
{/key}
