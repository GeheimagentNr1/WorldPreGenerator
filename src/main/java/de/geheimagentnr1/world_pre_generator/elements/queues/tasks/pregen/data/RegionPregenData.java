package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


//package-private
class RegionPregenData {
	
	
	@NotNull
	private final WorldPos start_gen_chunk_pos;
	
	private final int x_chunk_count;
	
	private int chunk_index = 0;
	
	private final int chunk_count;
	
	//package-private
	RegionPregenData(
		@NotNull WorldPos region_pos,
		@NotNull WorldPos start_chunk_pos,
		@NotNull WorldPos end_chunk_pos ) {
		
		WorldPos start_region_chunk_pos = new WorldPos(
			region_pos.getX() << 5,//* 32
			region_pos.getZ() << 5//* 32
		);
		WorldPos end_region_chunk_pos = new WorldPos(
			start_region_chunk_pos.getX() + 31,
			start_region_chunk_pos.getZ() + 31
		);
		start_gen_chunk_pos = new WorldPos(
			Math.max( start_chunk_pos.getX(), start_region_chunk_pos.getX() ),
			Math.max( start_chunk_pos.getZ(), start_region_chunk_pos.getZ() )
		);
		WorldPos end_gen_chunk_pos = new WorldPos(
			Math.min( end_chunk_pos.getX(), end_region_chunk_pos.getX() ),
			Math.min( end_chunk_pos.getZ(), end_region_chunk_pos.getZ() )
		);
		x_chunk_count = end_gen_chunk_pos.getX() - start_gen_chunk_pos.getX() + 1;
		int z_chunk_count = end_gen_chunk_pos.getZ() - start_gen_chunk_pos.getZ() + 1;
		chunk_count = x_chunk_count * z_chunk_count;
	}
	
	//package-private
	@NotNull
	Optional<WorldPos> nextChunk() {
		
		if( isFullyGenerated() ) {
			return Optional.empty();
		}
		WorldPos pos = new WorldPos(
			start_gen_chunk_pos.getX() + chunk_index % x_chunk_count,
			start_gen_chunk_pos.getZ() + chunk_index / x_chunk_count
		);
		chunk_index++;
		return Optional.of( pos );
	}
	
	//package-private
	boolean isFullyGenerated() {
		
		return chunk_index >= chunk_count;
	}
}
