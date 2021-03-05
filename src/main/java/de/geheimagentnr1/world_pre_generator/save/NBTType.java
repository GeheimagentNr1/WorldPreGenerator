package de.geheimagentnr1.world_pre_generator.save;

public enum NBTType {
	INT( 3 ),
	STRING( 8 ),
	LIST( 9 ),
	COMPOUND( 10 );
	
	private final int id;
	
	//private
	NBTType( int _id ) {
		
		id = _id;
	}
	
	public int getId() {
		
		return id;
	}
}
