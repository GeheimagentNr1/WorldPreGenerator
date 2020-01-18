package de.geheimagentnr1.world_pre_generator.handlers;

import de.geheimagentnr1.world_pre_generator.commands.PregenCommand;
import de.geheimagentnr1.world_pre_generator.generator.PreGenWorker;
import de.geheimagentnr1.world_pre_generator.generator.util.TasksSaver;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE )
public class ForgeRegistryEventHandler {
	
	
	@SubscribeEvent
	public static void handlerServerStartingEvent( FMLServerStartingEvent event ) {
		
		PregenCommand.register( event.getCommandDispatcher() );
		WorldWorkerManager.addWorker( new PreGenWorker() );
		TasksSaver.loadTasks( event.getServer() );
	}
	
	@SubscribeEvent
	public static void handleServerStoppedEvent( FMLServerStoppedEvent event ) {
		
		TasksSaver.saveTasks();
	}
}
