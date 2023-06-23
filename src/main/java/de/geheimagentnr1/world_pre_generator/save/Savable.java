package de.geheimagentnr1.world_pre_generator.save;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;


//Methods are only used in their subclasses, but not in this interface.
@SuppressWarnings( "unused" )
public interface Savable<T extends JsonElement> {
	
	
	//public
	@NotNull
	T write();
	
	//public
	void read( @NotNull T nbt );
}
