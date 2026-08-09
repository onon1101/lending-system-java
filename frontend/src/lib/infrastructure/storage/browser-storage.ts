import { browser } from '$app/environment';

export function readSessionValue(key: string): string | null {
	return browser ? sessionStorage.getItem(key) : null;
}

export function writeSessionValue(key: string, value: string): void {
	if (browser) sessionStorage.setItem(key, value);
}
