import type { CurrentUser } from '$lib/features/users/types/user.types';

class AuthState {
	currentUser = $state<CurrentUser | null>(null);
	initialized = $state(false);

	get authenticated(): boolean {
		return this.currentUser !== null;
	}

	setCurrentUser(user: CurrentUser): void {
		this.currentUser = user;
		this.initialized = true;
	}

	clear(): void {
		this.currentUser = null;
		this.initialized = true;
	}
}

export const authState = new AuthState();
