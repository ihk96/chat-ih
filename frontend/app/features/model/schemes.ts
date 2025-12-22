import {z} from "zod/v4";

export const LLModelSchema = z.object({
	id: z.string(),
	publicName: z.string(),
	originName: z.string(),
	providerId: z.string(),
	completionUrl: z.string()
})