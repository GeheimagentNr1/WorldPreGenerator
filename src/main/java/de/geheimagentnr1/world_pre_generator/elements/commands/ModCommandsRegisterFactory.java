package de.geheimagentnr1.world_pre_generator.elements.commands;

import de.geheimagentnr1.world_pre_generator.api.commands.CommandInterface;
import de.geheimagentnr1.world_pre_generator.api.commands.CommandsRegisterFactory;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@RequiredArgsConstructor
public class ModCommandsRegisterFactory extends CommandsRegisterFactory {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final PregenWorker pregenWorker;
	
	@NotNull
	@Override
	public List<CommandInterface> commands() {
		
		return List.of(
			new PregenCommand( serverConfig, pregenWorker )
		);
	}
	
	@SubscribeEvent
	@Override
	public void onRegisterCommands( @NotNull RegisterCommandsEvent event ) {
		
		super.onRegisterCommands( event );
	}
}
