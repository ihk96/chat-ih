import {z} from "zod/v4";

export const ChatMessageTypeEnum = z.enum(["SYSTEM","USER","AI","TOOL_EXECUTION_RESULT","CUSTOM"])
export const ChatAttachmentTypeEnum = z.enum(["IMAGE","PDF","DOCUMENT"])

export const ChatAttachmentSchema = z.object({
	id : z.string(),
	fileName : z.string(),
	contentType : ChatAttachmentTypeEnum
})

export const ChatMessageSchema = z.object({
	type : ChatMessageTypeEnum,
	text : z.string(),
	thinking: z.string().optional(),
	toolRequests : z.array(z.string()).optional(),
	attachments : z.array(ChatAttachmentSchema).optional(),
})

export const ChatSessionSchema = z.object({
	id : z.string(),
	userId : z.string(),
	title: z.string()
})


