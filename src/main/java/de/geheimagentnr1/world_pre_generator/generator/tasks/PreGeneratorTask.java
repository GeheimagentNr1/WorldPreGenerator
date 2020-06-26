package de.geheimagentnr1.world_pre_generator.generator.tasks;

import de.geheimagentnr1.world_pre_generator.generator.region.ChunkRegion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.ArrayList;
import java.util.Objects;


public class PreGeneratorTask {
	
	
	private final MinecraftServer server;
	
	private final int center_x;
	
	private final int center_z;
	
	private final int radius;
	
	private final RegistryKey<World> dimension;
	
	private int chunk_index;
	
	private final int chunk_count;
	
	private int region_index;
	
	private final ChunkRegion[] regions;
	
	private boolean canceled = false;
	
	public PreGeneratorTask( MinecraftServer _server, int _center_x, int _center_z, int _radius,
		RegistryKey<World> _dimension ) {
		
		server = _server;
		center_x = _center_x;
		center_z = _center_z;
		radius = _radius;
		dimension = _dimension;
		chunk_index = -1;
		region_index = 0;
		int start_x = center_x - radius;
		int start_z = center_z - radius;
		int stop_x = center_x + radius - 1;
		int stop_z = center_z + radius - 1;
		int diameter = radius << 1;//* 2
		int start_x_region = start_x / 32 - ( start_x < 0 ? 1 : 0 );
		int start_z_region = start_z / 32 - ( start_z < 0 ? 1 : 0 );
		int diameter_region = ChunkRegion.calculateRadius( _radius ) << 1;
		chunk_count = diameter * diameter;
		ArrayList<ChunkRegion> chunkRegions = new ArrayList<>();
		
		for( int x = start_x_region; x <= start_x_region + diameter_region; x++ ) {
			for( int z = start_z_region; z <= start_z_region + diameter_region; z++ ) {
				ChunkRegion chunkRegion = new ChunkRegion( x, z, start_x, start_z, stop_x, stop_z );
				if( chunkRegion.isNotEmpty() ) {
					chunkRegions.add( chunkRegion );
				}
			}
		}
		regions = chunkRegions.toArray( new ChunkRegion[0] );
	}
	
	public PreGeneratorTask( MinecraftServer _server, int _center_x, int _center_z, int _radius,
		RegistryKey<World> _dimension, int _chunk_index ) {
		
		this( _server, _center_x, _center_z, _radius, _dimension );
		chunk_index = _chunk_index;
	}
	
	private ChunkPos next() {
		
		chunk_index++;
		if( chunk_index >= chunk_count ) {
			return null;
		}
		ChunkPos chunkPos = regions[region_index].nextChunk();
		if( chunkPos == null && region_index + 1 < regions.length ) {
			region_index++;
			chunkPos = regions[region_index].nextChunk();
		}
		return chunkPos;
	}
	
	public boolean generateNext() {
		
		if( canceled ) {
			return true;
		}
		ChunkPos current_pos = next();
		if( current_pos == null ) {
			return true;
		}
		Objects.requireNonNull( server.getWorld( dimension ) ).getChunkProvider()
			.getChunk( current_pos.x, current_pos.z, ChunkStatus.FULL,
			true );
		return chunk_index == chunk_count;
	}
	
	public MinecraftServer getServer() {
		
		return server;
	}
	
	public int getCenterX() {
		
		return center_x;
	}
	
	public int getCenterZ() {
		
		return center_z;
	}
	
	public int getRadius() {
		
		return radius;
	}
	
	public int getProgress() {
		
		return chunk_index * 100 / chunk_count;
	}
	
	public int getChunkIndex() {
		
		return chunk_index;
	}
	
	public int getChunkCount() {
		
		return chunk_count;
	}
	
	public RegistryKey<World> getDimension() {
		
		return dimension;
	}
	
	public void cancel() {
		
		canceled = true;
	}
}
