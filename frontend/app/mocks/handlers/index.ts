import { providerHandlers } from './providerHandlers'
import { modelHandlers } from './modelHandlers'
import { authHandlers } from './authHandlers'
import {userHandlers} from "~/mocks/handlers/userHandlers";
import { chatHandlers } from './chatHandlers'

export const handlers = [
	...providerHandlers,
	...modelHandlers,
	...authHandlers,
	...userHandlers,
	...chatHandlers
]
