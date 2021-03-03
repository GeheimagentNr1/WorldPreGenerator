package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data;

import java.util.Optional;


public class WorldPregenData {
	
	
	private final WorldPos start_chunk_pos;
	
	private final WorldPos end_chunk_pos;
	
	private final int x_region_count;
	
	private final WorldPos start_region_pos;
	
	private int region_index = 0;
	
	private int chunk_index = 0;
	
	private final int chunk_count;
	
	private RegionPregenData current_region;
	
	public WorldPregenData( int center_x, int center_z, int radius ) {
		
		start_chunk_pos = new WorldPos( center_x - radius, center_z - radius );
		end_chunk_pos = new WorldPos( center_x + radius - 1, center_z + radius - 1 );
		int diameter = radius << 1;//* 2
		start_region_pos = new WorldPos(
			(int)Math.floor( ( start_chunk_pos.getX() + ( start_chunk_pos.getX() < 0 ? 0 : 1 ) ) / 32.0 ),
			(int)Math.floor( ( start_chunk_pos.getZ() + ( start_chunk_pos.getZ() < 0 ? 0 : 1 ) ) / 32.0 )
		);
		WorldPos end_region_pos = new WorldPos(
			(int)Math.ceil( ( end_chunk_pos.getX() + ( end_chunk_pos.getX() < 0 ? 0 : 1 ) ) / 32.0 ),
			(int)Math.ceil( ( end_chunk_pos.getZ() + ( end_chunk_pos.getZ() < 0 ? 0 : 1 ) ) / 32.0 )
		);
		x_region_count = end_region_pos.getX() - start_region_pos.getX();
		chunk_count = diameter * diameter;
		current_region = new RegionPregenData( start_region_pos, start_chunk_pos, end_chunk_pos );
	}
	
	public synchronized Optional<WorldPos> nextChunk() {
		
		if( current_region.isFullyGenerated() ) {
			region_index++;
			current_region = new RegionPregenData(
				new WorldPos(
					start_region_pos.getX() + region_index % x_region_count,
					start_region_pos.getZ() + region_index / x_region_count
				),
				start_chunk_pos,
				end_chunk_pos
			);
		}
		Optional<WorldPos> pos = current_region.nextChunk();
		chunk_index++;
		return pos;
	}
	
	public synchronized boolean fullyGenerated() {
		
		return chunk_index >= chunk_count;
	}
	
	public synchronized int getChunkIndex() {
		
		return chunk_index;
	}
	
	public synchronized void setChunkIndex( int _chunk_index ) {
		
		chunk_index = _chunk_index;
	}
	
	public int getChunkCount() {
		
		return chunk_count;
	}
	
	public synchronized int getProgess() {
		
		return chunk_index * 100 / chunk_count;
	}
}
