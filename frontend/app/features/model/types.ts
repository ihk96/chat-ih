import type {LLModelSchema} from "~/features/model/schemes";
import {z} from "zod/v4";

export type LLModel = z.infer<typeof LLModelSchema>