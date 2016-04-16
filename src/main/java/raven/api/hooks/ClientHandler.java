package raven.api.hooks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;
import raven.api.events.ExitEvent;

/**
 * Created by Raven6101 on 19.03.2016.
 */
@SideOnly(Side.CLIENT)
public class ClientHandler {

    public static boolean onPreClientTick(WorldClient worldClient) {
        return FMLCommonHandler.instance().bus().post(new TickEvent.WorldTickEvent(Side.CLIENT, TickEvent.Phase.START, worldClient));
    }

    public static boolean onPostClientTick(WorldClient worldClient) {
        return FMLCommonHandler.instance().bus().post(new TickEvent.WorldTickEvent(Side.CLIENT, TickEvent.Phase.END, worldClient));
    }

    public static boolean onExit() {
        return MinecraftForge.EVENT_BUS.post(new ExitEvent());
    }
}
