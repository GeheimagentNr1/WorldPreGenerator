package de.geheimagentnr1.world_pre_generator.handlers;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.commands.PregenCommand;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;


@Mod.EventBusSubscriber( modid = WorldPreGenerator.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerAboutToStartEvent( FMLServerStartingEvent event ) {
		
		PregenWorker.getInstance().setServer( event.getServer() );
		WorldWorkerManager.addWorker( PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handleRegisterCommandsEvent( RegisterCommandsEvent event ) {
		
		PregenCommand.register( event.getDispatcher(), PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( FMLServerStoppedEvent event ) {
		
		PregenWorker.getInstance().clearUp();
	}
}
