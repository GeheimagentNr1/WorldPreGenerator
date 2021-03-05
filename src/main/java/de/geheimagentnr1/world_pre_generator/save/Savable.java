package de.geheimagentnr1.world_pre_generator.save;

import net.minecraft.nbt.INBT;

import javax.annotation.Nonnull;


//Methods are only used in there subclasses, but not in this interface.
@SuppressWarnings( "unused" )
public interface Savable<T extends INBT> {
	
	
	//public
	@Nonnull
	T writeNBT();
	
	//public
	void readNBT( @Nonnull T nbt );
}
