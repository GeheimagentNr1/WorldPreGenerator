package de.geheimagentnr1.world_pre_generator.handlers;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.commands.PregenCommand;
import de.geheimagentnr1.world_pre_generator.elements.commands.arguments.ModArgumentTypes;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@Mod.EventBusSubscriber( modid = WorldPreGenerator.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ForgeEventHandler {
	
	
	@SubscribeEvent
	public static void handleServerAboutToStartEvent( FMLServerAboutToStartEvent event ) {
		
		PregenWorker.getInstance().setServer( event.getServer() );
		PregenWorker.getInstance().clearUp();
		WorldWorkerManager.addWorker( PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handlerServerStartingEvent( FMLServerStartingEvent event ) {
		
		ModArgumentTypes.registerArgumentTypes();
		PregenCommand.register( event.getCommandDispatcher(), PregenWorker.getInstance() );
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( FMLServerStoppedEvent event ) {
		
		PregenWorker.getInstance().clearUp();
	}
}
