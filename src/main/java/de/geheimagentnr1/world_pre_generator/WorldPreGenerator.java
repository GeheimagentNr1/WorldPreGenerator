package de.geheimagentnr1.world_pre_generator;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( WorldPreGenerator.MODID )
public class WorldPreGenerator {
	
	
	public static final String MODID = "world_pre_generator";
	
	public WorldPreGenerator() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
		ModLoadingContext.get().registerExtensionPoint(
			IExtensionPoint.DisplayTest.class,
			() -> new IExtensionPoint.DisplayTest(
				() -> FMLNetworkConstants.IGNORESERVERONLY,
				( remote, isServer ) -> true
			)
		);
	}
}
