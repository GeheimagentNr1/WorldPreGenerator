package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.helpers.SaveHelper;
import net.minecraft.server.MinecraftServer;


public class SaverSubTask extends TimedSubTask {
	
	
	private MinecraftServer server;
	
	private final PrinterSubTask printer;
	
	public SaverSubTask( PrinterSubTask _printer ) {
		
		printer = _printer;
	}
	
	//package-private
	int getDelay() {
		
		return ServerConfig.getSaveDelay();
	}
	
	@SuppressWarnings( "CallToSystemGC" )
	@Override
	public void execute() {
		
		printer.pause();
		SaveHelper.saveWorld( server );
		System.gc();
		printer.resume();
	}
	
	public void setServer( MinecraftServer _server ) {
		
		server = _server;
	}
}
