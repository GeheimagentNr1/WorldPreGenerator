package de.geheimagentnr1.world_pre_generator.save;

import com.google.gson.JsonElement;

import javax.annotation.Nonnull;


//Methods are only used in there subclasses, but not in this interface.
@SuppressWarnings( "unused" )
public interface Savable<T extends JsonElement> {
	
	
	//public
	@Nonnull
	T write();
	
	//public
	void read( @Nonnull T nbt );
}
