export type ItemAvailability = string;

export type CreateItemRequest = { name: string; description: string };
export type CreateItemResponse = { itemId: string };
export type UpdateItemRequest = { itemId: string; name: string; description: string };
export type DeleteItemRequest = { itemId: string };

export type ItemMutationResponse = {
	itemId: string;
	name?: string;
	description?: string;
	updatedAt?: string;
	archivedAt?: string;
};

export type ItemDetail = {
	itemId: string;
	ownerId: string;
	ownerUsername: string;
	name: string;
	description: string;
	availability: ItemAvailability;
	ownedByCurrentUser: boolean;
	canRequestBorrow: boolean;
	createdAt: string;
	updatedAt: string;
};
