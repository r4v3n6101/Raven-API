package com.jsn_man.ac;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import example.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;

@Mod(modid = ArrowCamMod.MODID, name = ArrowCamMod.NAME, version = ArrowCamMod.VERSION)
public class ArrowCamMod {

    public static final String MODID = "ArrowCamMod",
            NAME = "ArrowCamMod",
            VERSION = "2.0.0";
    @Instance(ArrowCamMod.MODID)
    public static ArrowCamMod instance;
    /**
     * There should only ever be one camera in the game
     */
    @SideOnly(Side.CLIENT)
    public EntityCamera camera;
    /**
     * Basically just processes tasks at the end of each tick. See ArrowListener for why this is necessary
     */
    public TickHandler ticker;

    /**
     * The "inGround" field in EntityArrow is private, so we have to get around that one way or another
     *
     * @param arrow Is this arrow in the ground?
     * @return If the arrow is in the ground
     */
    public static boolean isArrowInGround(EntityArrow arrow) {
        try {
            Field inGround = EntityArrow.class.getDeclaredField("inGround");
            inGround.setAccessible(true);

            return (Boolean) inGround.get(arrow);
        } catch (Exception e) {
            NBTTagCompound tag = new NBTTagCompound();
            arrow.writeEntityToNBT(tag);

            return tag.getByte("inGround") == 1;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //TODO: Load a config if I want to have one
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityCamera.class, "ArrowCamera", EntityRegistry.findGlobalUniqueEntityId(), this, 256, 1, true);

        if (event.getSide().equals(Side.CLIENT)) {
            MinecraftForge.EVENT_BUS.register(new ArrowListener());

            ticker = new TickHandler();
            FMLCommonHandler.instance().bus().register(ticker);

        } else if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            FMLLog.severe("The Arrow Cam Mod is a client only mod. Running it on a server will cause undefined behavior! Please remove this mod from your server ASAP.");
        }
    }

    /**
     * TickHandler will run the task at the end of the current tick.
     * Because of the thread-safe nature of queues, this method is also thread-safe
     *
     * @param task The task to be processed
     */
    @SideOnly(Side.CLIENT)
    public void processAtTickEnd(Runnable task) {
        ticker.tasks.offer(task);
    }

    /**
     * Called when ArrowListener confirms the local player fires an arrow
     *
     * @param arrow The arrow fired
     */
    @SideOnly(Side.CLIENT)
    public void startArrowCam(EntityArrow arrow) {
        if (!isInArrowCam()) {
            camera = new EntityCamera(arrow);

            if (camera.worldObj.spawnEntityInWorld(camera)) {
                TestMod.Companion.setFboEnt(camera);
            } else {
                camera = null;
            }
        }
    }

    /**
     * Called when the EntityCamera has decided it can no longer follow its target arrow
     */
    @SideOnly(Side.CLIENT)
    public void stopArrowCam() {
        if (isInArrowCam()) {
            TestMod.Companion.setFboEnt(null);

            camera.setDead();
            camera = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isInArrowCam() {
        return camera != null;
    }
}