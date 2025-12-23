import {type LLModelSchema, UserLLModelSchema} from "~/features/model/schemes";
import {z} from "zod/v4";

export type LLModel = z.infer<typeof LLModelSchema>
export type UserLLModel = z.infer<typeof UserLLModelSchema>