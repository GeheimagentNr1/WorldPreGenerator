package de.geheimagentnr1.world_pre_generator.helpers;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class DimensionHelper {
	
	
	@NotNull
	public static String getNameOfDim( @NotNull ResourceKey<Level> dimension ) {
		
		return Objects.requireNonNull( dimension.location() ).toString();
	}
	
	@NotNull
	public static ResourceKey<Level> getDimFromName( @NotNull String registry_name ) {
		
		return ResourceKey.create( Registries.DIMENSION, new ResourceLocation( registry_name ) );
	}
}
