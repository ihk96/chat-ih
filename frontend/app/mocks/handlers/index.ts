import { providerHandlers } from './providerHandlers'
import { modelHandlers } from './modelHandlers'
import { authHandlers } from './authHandlers'

export const handlers = [
	...providerHandlers,
	...modelHandlers,
	...authHandlers
]
