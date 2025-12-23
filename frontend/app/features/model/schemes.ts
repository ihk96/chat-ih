import {z} from "zod/v4";

export const LLModelSchema = z.object({
	id: z.string(),
	publicName: z.string(),
	originName: z.string(),
	providerId: z.string(),
	completionUrl: z.string()
})

export const UserLLModelSchema = z.object({
	id: z.string(),
	modelName: z.string(),
	providerId: z.string(),
	providerName: z.string(),
})