package raven.api.client.events

import cpw.mods.fml.common.eventhandler.Cancelable
import cpw.mods.fml.common.eventhandler.Event

/**
 * Created by r4v3n6101 on 16.04.2016.
 * Fired when user wanna close game
 * If it's canceled, game won't be closed
 */
@Cancelable
class ExitEvent() : Event()