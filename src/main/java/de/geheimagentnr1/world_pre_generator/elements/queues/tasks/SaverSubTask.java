package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.helpers.SaveHelper;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class SaverSubTask extends TimedSubTask {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final PrinterSubTask printer;
	
	private MinecraftServer server;
	
	//package-private
	int getDelay() {
		
		return serverConfig.getSaveDelay();
	}
	
	@SuppressWarnings( "CallToSystemGC" )
	@Override
	public void execute() {
		
		printer.pause();
		SaveHelper.saveWorld( server );
		System.gc();
		printer.resume();
	}
	
	public void setServer( @NotNull MinecraftServer _server ) {
		
		server = _server;
	}
}
