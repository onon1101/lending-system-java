export type RegisterUserRequest = {
	username: string;
	password: string;
	email: string;
};

export type RegisterUserResponse = { userId: string };

export type CurrentUser = {
	userId: string;
	username: string;
	email: string;
	status: string;
};
