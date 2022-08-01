package de.geheimagentnr1.world_pre_generator;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( WorldPreGenerator.MODID )
public class WorldPreGenerator {
	
	
	public static final String MODID = "world_pre_generator";
	
	public WorldPreGenerator() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
	}
}
