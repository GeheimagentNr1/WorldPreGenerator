package de.geheimagentnr1.world_pre_generator;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos.WorldPosArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


@SuppressWarnings( "UtilityClassWithPublicConstructor" )
@Mod( WorldPreGenerator.MODID )
public class WorldPreGenerator {
	
	
	public static final String MODID = "world_pre_generator";
	
	private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(
		Registry.COMMAND_ARGUMENT_TYPE_REGISTRY,
		MODID
	);
	
	private static final RegistryObject<SingletonArgumentInfo<WorldPosArgument>> WORLD_POS_COMMAND_ARGUMENT_TYPE =
		COMMAND_ARGUMENT_TYPES.register(
			WorldPosArgument.registry_name,
			() -> ArgumentTypeInfos.registerByClass(
				WorldPosArgument.class,
				SingletonArgumentInfo.contextFree( WorldPosArgument::worldPos )
			)
		);
	
	public WorldPreGenerator() {
		
		ModLoadingContext.get().registerConfig( ModConfig.Type.SERVER, ServerConfig.CONFIG );
		ModLoadingContext.get().registerExtensionPoint(
			IExtensionPoint.DisplayTest.class,
			() -> new IExtensionPoint.DisplayTest(
				() -> NetworkConstants.IGNORESERVERONLY,
				( remote, isServer ) -> true
			)
		);
		COMMAND_ARGUMENT_TYPES.register( FMLJavaModLoadingContext.get().getModEventBus());
	}
}
