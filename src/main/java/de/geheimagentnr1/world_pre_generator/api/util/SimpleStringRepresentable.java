package de.geheimagentnr1.world_pre_generator.api.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;


public interface SimpleStringRepresentable extends StringRepresentable {
	
	
	@NotNull
	@Override
	default String getSerializedName() {
		
		if( this instanceof Enum<?> enumValue ) {
			return enumValue.name().toLowerCase();
		}
		throw new UnsupportedOperationException( "SimpleStringRepresentable must be implemented by an Enum" );
	}
}
