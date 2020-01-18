package de.geheimagentnr1.world_pre_generator.generator.region;

import net.minecraft.util.math.ChunkPos;


public class ChunkRegion {
	
	
	private final static int SIZE = 32;
	
	private final int start_x;
	
	private final int start_z;
	
	private final int size_x;
	
	private final int size_z;
	
	private int chunk_index;
	
	private final int chunk_count;
	
	public ChunkRegion( int x, int z, int _start_x, int _start_z, int _stop_x, int _stop_z ) {
		
		start_x = Math.max( x * SIZE, _start_x );
		start_z = Math.max( z * SIZE, _start_z );
		int stop_x = Math.min( x * SIZE + SIZE - 1, _stop_x );
		int stop_z = Math.min( z * SIZE + SIZE - 1, _stop_z );
		size_x = stop_x - start_x + 1;
		size_z = stop_z - start_z + 1;
		chunk_index = -1;
		chunk_count = size_x * size_z;
	}
	
	public ChunkPos nextChunk() {
		
		chunk_index++;
		if( chunk_index > chunk_count ) {
			return null;
		}
		int dz = chunk_index / size_z;
		int dx = chunk_index - dz * size_x;
		return new ChunkPos( start_x + dx, start_z + dz );
	}
	
	public static int calculateRadius( int radius ) {
		
		return ( radius - 1 ) / 32 + 1;
	}
}
