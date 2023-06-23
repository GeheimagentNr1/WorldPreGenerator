package de.geheimagentnr1.world_pre_generator.elements.commands;

import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandsRegisterFactory;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import lombok.RequiredArgsConstructor;
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
}
