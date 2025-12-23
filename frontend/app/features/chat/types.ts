import {z} from "zod/v4";
import {ChatMessageSchema, ChatMessageTypeEnum, type ChatSessionSchema} from "~/features/chat/schemes";

export type ChatSession = z.infer<typeof ChatSessionSchema>
export type ChatMessage = z.infer<typeof ChatMessageSchema>
export type ChatMessageType = z.infer<typeof ChatMessageTypeEnum>
