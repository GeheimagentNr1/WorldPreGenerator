package de.geheimagentnr1.world_pre_generator.helpers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;


public class DimensionHelper {
	
	
	public static String getNameOfDim( DimensionType dimension ) {
		
		return Objects.requireNonNull( dimension.getRegistryName() ).toString();
	}
	
	public static DimensionType getDimFromName( String registry_name ) {
		
		return DimensionType.byName( new ResourceLocation( registry_name ) );
	}
}
