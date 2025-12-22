import type {ProviderSchema} from "~/features/provider/schemes";
import {z} from "zod/v4";

export type Provider = z.infer<typeof ProviderSchema>;
