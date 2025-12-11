package de.geheimagentnr1.world_pre_generator.api.commands;

import de.geheimagentnr1.world_pre_generator.api.events.NeoForgeEventHandlerInterface;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class CommandsRegisterFactory implements NeoForgeEventHandlerInterface {
	
	
	@NotNull
	public abstract List<CommandInterface> commands();
	
	public void onRegisterCommands( @NotNull RegisterCommandsEvent event ) {
		
		for( CommandInterface command : commands() ) {
			event.getDispatcher().register( command.build() );
		}
	}
}
