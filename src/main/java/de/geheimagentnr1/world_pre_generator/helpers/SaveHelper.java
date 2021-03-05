package de.geheimagentnr1.world_pre_generator.helpers;

import net.minecraft.server.MinecraftServer;


public class SaveHelper {
	
	
	public static void saveWorld( MinecraftServer server ) {
		
		server.save( false, true, true );
	}
}
