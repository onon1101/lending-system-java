<script lang="ts">
  import { goto } from "$app/navigation";
  import { onMount } from "svelte";
  import AppShell from "$lib/app/components/AppShell.svelte";
  import { getAccessToken } from "$lib/infrastructure/storage/token-storage";
  import { provideItemDraftState } from "$lib/features/items/state/item-draft-state.svelte";

  let { children } = $props();
  let ready = $state(false);

  provideItemDraftState();

  onMount(() => {
    if (!getAccessToken()) {
      void goto("/login");
      return;
    }
    ready = true;
  });
</script>

{#if ready}
  <AppShell>
    {@render children()}
  </AppShell>
{/if}
