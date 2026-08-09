<script lang="ts">
  import { onMount } from "svelte";
  import { fly } from "svelte/transition";

  type ToastType = "error" | "success" | "info";

  type Props = {
    message: string;
    type?: ToastType;
    duration?: number;
    onDismiss?: () => void;
  };

  let {
    message,
    type = "error",
    duration = 5000,
    onDismiss = () => {},
  }: Props = $props();

  const styles: Record<ToastType, string> = {
    error:
      "border-rose-400/30 bg-slate-900/95 text-rose-100 shadow-rose-950/30",
    success:
      "border-emerald-400/30 bg-slate-900/95 text-emerald-100 shadow-emerald-950/30",
    info: "border-sky-400/30 bg-slate-900/95 text-sky-100 shadow-sky-950/",
  };

  const titles: Record<ToastType, string> = {
    error: "註冊失敗",
    success: "操作成功",
    info: "系統通知",
  };

  onMount(() => {
    if (duration <= 0) {
      return;
    }

    const timerId = window.setTimeout(() => {
      onDismiss();
    }, duration);

    return () => {
      window.clearTimeout(timerId);
    };
  });

  function portal(node: HTMLElement) {
    document.body.appendChild(node);

    return {
      destroy() {
        node.remove();
      },
    };
  }
</script>

<div
  use:portal
  class="fixed right-4 bottom-4 left-4 z-50 sm:left-auto sm:w-full sm:max-w-sm"
  role={type === "error" ? "alert" : "status"}
  aria-live={type === "error" ? "assertive" : "polite"}
  transition:fly={{ y: 20, duration: 200 }}
>
  <div
    class="flex items-start gap-3 rounded-2xl border p-4 shadow-2xl backgrop-blur-md {styles[
      type
    ]}"
  >
    <div class="mix-w-0 flex-1">
      <p class="text-sm font-semibold">
        {titles[type]}
      </p>

      <p class="mt-1 wrap-break-words text-sm text-slate-300">
        {message}
      </p>
    </div>

    <button
      type="button"
      class="rounded-lg px-2 py-1 text-lg leading-none text-slate-400 transition hover:bg-white/10 hover:text-white focus:outline-none focus:ring-2 focus:ring-sky-400"
      aria-label="關閉通知"
      onclick={onDismiss}>×</button
    >
  </div>
</div>
