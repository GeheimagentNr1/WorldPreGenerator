package de.geheimagentnr1.world_pre_generator.handlers;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.commands.PregenCommand;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.save.PregenerationWorldPersistencer;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber( modid = WorldPreGenerator.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerStartingEvent( ServerStartingEvent event ) {
		
		PregenWorker.getInstance().setServer( event.getServer() );
		PregenerationWorldPersistencer.getInstance().load();
		WorldWorkerManager.addWorker( PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handleRegisterCommandsEvent( RegisterCommandsEvent event ) {
		
		PregenCommand.register( event.getDispatcher(), PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( ServerStoppedEvent event ) {
		
		PregenerationWorldPersistencer.getInstance().save();
		PregenWorker.getInstance().clearUp();
	}
}
