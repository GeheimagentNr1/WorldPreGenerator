package de.geheimagentnr1.world_pre_generator.elements.commands.arguments;

import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos.WorldPosArgument;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;


public class ModArgumentTypes {
	
	
	public static void registerArgumentTypes() {
		
		ArgumentTypes.register(
			WorldPreGenerator.MODID + ":" + WorldPosArgument.registry_name,
			WorldPosArgument.class,
			new ArgumentSerializer<>( WorldPosArgument::new )
		);
	}
}
