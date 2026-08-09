export function formatDate(value: string): string {
	return new Intl.DateTimeFormat('zh-TW', { dateStyle: 'medium', timeStyle: 'short' }).format(
		new Date(value)
	);
}
