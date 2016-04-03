package raven.api.hooks.impl;

import gloomyfolken.hooklib.asm.Hook;
import net.minecraft.client.multiplayer.WorldClient;
import raven.api.hooks.ClientHandler;

/**
 * Created by Raven6101 on 19.03.2016.
 */
public class ClientHook {

    @Hook(targetMethod = "tick")
    public static void onPreTick(WorldClient worldClient) {
        ClientHandler.onPreClientTick(worldClient);
    }

    @Hook(targetMethod = "tick", injectOnExit = true)
    public static void onPostTick(WorldClient worldClient) {
        ClientHandler.onPostClientTick(worldClient);
    }
}
