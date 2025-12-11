package de.geheimagentnr1.world_pre_generator.api.events;

import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.NotNull;


public interface NeoForgeEventHandlerInterface {
	
	
	default void handleServerStartingEvent( @NotNull ServerStartingEvent event ) {
		
	}
	
	default void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
	}
}
