package de.geheimagentnr1.world_pre_generator.helpers;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;


public class SaveHelper {
	
	
	public static void saveWorld( @NotNull MinecraftServer server ) {
		
		server.saveAllChunks( false, true, true );
	}
}
