import {z} from "zod/v4";

export const ChatMessageTypeEnum = z.enum(["SYSTEM","USER","AI","TOOL_EXECUTION_RESULT","CUSTOM"])

export const ChatMessageSchema = z.object({
	type : ChatMessageTypeEnum,
	text : z.string(),
	thinking: z.string().optional(),
	toolRequests : z.array(z.string()).optional()
})

export const ChatSessionSchema = z.object({
	id : z.string(),
	userId : z.string(),
	title: z.string(),
	messages : z.array(ChatMessageSchema)

})


