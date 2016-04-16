package com.jsn_man.ac;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EntityCamera extends EntityLivingBase{
	
	public EntityCamera(World world){
		super(world);
		setSize(0.0F, 0.0F);
		boundingBox.setBounds(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
		yOffset = 0.0F;
		deathDelay = 20;
	}
	
	public EntityCamera(EntityArrow arrow){
		this(arrow.worldObj);
		setTarget(arrow);
		target.renderDistanceWeight *= 4.0; //This might make the arrow render farther from the player
		setPositionAndRotation(target.posX, target.posY, target.posZ, 360.0F - target.rotationYaw, 360.0F - target.rotationPitch);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if(target != null){
			//Takes the midpoint from the current position and the target's position so it's not so choppy
			setPosition((posX + target.posX)/2.0F, (posY + target.posY)/2.0F, (posZ + target.posZ)/2.0F);
			
			//Should also take the average of the rotations, but for some reason it's more complicated than you'd think
			setRotation(360.0F - target.rotationYaw, 360.0F - target.rotationPitch);
			
			//Is the arrow dead or is it in the ground? If so, is the delay <= 0 yet?
			if((!target.isEntityAlive() || ArrowCamMod.isArrowInGround(target)) && --deathDelay <= 0){
				ArrowCamMod.instance.stopArrowCam();
				return;
			}
		}
		
		//Counteract gravity
		if(!onGround && motionY < 0.0){
			motionY = 0.0;
		}
		
		//The camera will get stuck if it is in an unloaded chunk
		Chunk chunk = worldObj.getChunkFromBlockCoords((int)posX, (int)posY);
		if(!chunk.isChunkLoaded){
			worldObj.getChunkProvider().loadChunk(chunk.xPosition, chunk.zPosition);
		}
		
		if(Minecraft.getMinecraft().thePlayer == null || !Minecraft.getMinecraft().thePlayer.isSneaking()){
			ArrowCamMod.instance.stopArrowCam();
		}
	}
	
	public void setTarget(EntityArrow arrow){
		target = arrow;
	}
	
	public EntityArrow getTarget(){
		return target;
	}
	
	//For some reason the regular method throws an NPE, so I rerouted it to the player
	@Override
	public boolean isPotionActive(Potion potion){
		return Minecraft.getMinecraft().thePlayer.isPotionActive(potion);
	}
	
	//Don't want fall damage
	@Override
	protected void fall(float blocks){
		
	}
	
	@Override
	public boolean isEntityInvulnerable(){
		return true;
	}
	
	@Override
	protected boolean canTriggerWalking(){
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return false;
	}
	
	@Override
	public boolean canBePushed(){
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderOnFire(){
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public float getShadowSize(){
		return 0.0F;
	}
	
	@Override
	public ItemStack getHeldItem(){
		return null;
	}
	
	@Override
	public ItemStack getEquipmentInSlot(int i){
		return null;
	}
	
	@Override
	public void setCurrentItemOrArmor(int i, ItemStack itemstack){
		
	}
	
	@Override
	public ItemStack[] getLastActiveItems(){
		return new ItemStack[]{null, null, null, null, null};
	}
	
	/** How many ticks the camera will stay around for after the arrow is dead or in a block */
	public int deathDelay;
	
	/** The arrow that the camera is following */
	public EntityArrow target;
}