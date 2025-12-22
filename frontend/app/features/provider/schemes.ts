import {z} from "zod/v4";

export const ProviderEnum = z.enum(["OPENAI", "OPENAI_COMPATIBLE", "GOOGLE","ANTHROPIC"])

export const ProviderSchema = z.object({
	id: z.string(),
	name: z.string(),
	provider: ProviderEnum,
	baseUrl: z.string().optional(),
	apiKey: z.string().optional(),
})



