package raven.api.hooks.impl;

import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import raven.api.client.handler.ClientHandler;

/**
 * Created by r4v3n6101 on 19.03.2016.
 */
public class ClientHook {

    @Hook(targetMethod = "tick")
    public static void onPreTick(WorldClient worldClient) {
        ClientHandler.INSTANCE.onPreClientTick(worldClient);
    }

    @Hook(targetMethod = "tick", injectOnExit = true)
    public static void onPostTick(WorldClient worldClient) {
        ClientHandler.INSTANCE.onPostClientTick(worldClient);
    }

    @Hook(returnCondition = ReturnCondition.ON_TRUE)
    public static boolean shutdown(Minecraft mc) {
        return ClientHandler.INSTANCE.onExit();
    }

}
