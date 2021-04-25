package de.geheimagentnr1.world_pre_generator.helpers;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;


public class DimensionHelper {
	
	
	public static String getNameOfDim( RegistryKey<World> dimension ) {
		
		return Objects.requireNonNull( dimension.location() ).toString();
	}
	
	public static RegistryKey<World> getDimFromName( String registry_name ) {
		
		return RegistryKey.create( Registry.DIMENSION_REGISTRY, new ResourceLocation( registry_name ) );
	}
}
