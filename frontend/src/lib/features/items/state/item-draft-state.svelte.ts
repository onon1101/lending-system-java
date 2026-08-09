import { getContext, setContext } from "svelte";

const ITEM_DRAFT_CONTEXT = Symbol("item-draft");

export class ItemDraftState {
  name = $state("");
  description = $state("");

  get hasContext(): boolean {
    return this.name.trim() !== "" || this.description.trim() !== "";
  }

  clear(): void {
    this.name = "";
    this.description = "";
  }
}

export function provideItemDraftState(): ItemDraftState {
  const state = new ItemDraftState();

  setContext(ITEM_DRAFT_CONTEXT, state);

  return state;
}

export function getItemDraftState(): ItemDraftState {
  const state = getContext<ItemDraftState | undefined>(ITEM_DRAFT_CONTEXT);

  if (!state) {
    throw new Error(
      "ItemDraftState must be used inside the authenticated layout.",
    );
  }

  return state;
}
