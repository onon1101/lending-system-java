export function reportError(error: unknown, context: string): void {
	console.error(`[${context}]`, error);
}
