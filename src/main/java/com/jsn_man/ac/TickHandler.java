package com.jsn_man.ac;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class TickHandler{
	
	public TickHandler(){
		tasks = new ConcurrentLinkedQueue<Runnable>();
	}
	
	@SubscribeEvent
	public void onTick(TickEvent event){
		if(event.side == Side.CLIENT){
			Runnable task = tasks.poll();
			while(task != null){
				task.run();
				task = tasks.poll();
			}
		}
	}
	
	public Queue<Runnable> tasks;
}