package de.geheimagentnr1.world_pre_generator.elements.commands.arguments;

import de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos.WorldPosArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;


public class ModArgumentTypes {
	
	
	public static void registerArgumentTypes() {
		
		ArgumentTypeInfos.registerByClass(
			WorldPosArgument.class,
			SingletonArgumentInfo.contextFree( WorldPosArgument::new )
		);
	}
}
