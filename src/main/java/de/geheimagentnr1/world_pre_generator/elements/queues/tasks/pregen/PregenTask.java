package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen;

import com.google.gson.JsonObject;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPregenData;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import de.geheimagentnr1.world_pre_generator.helpers.JsonHelper;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class PregenTask implements Savable<JsonObject> {
	
	
	private static final String centerXName = "center_x";
	
	private static final String centerZName = "center_z";
	
	private static final String radiusName = "radius";
	
	private static final String chunkIndexName = "chunk_index";
	
	private static final String dimensionName = "dimension";
	
	private int center_x;
	
	private int center_z;
	
	private int radius;
	
	private boolean forceGeneration;
	
	private ResourceKey<Level> dimension;
	
	private boolean canceled = false;
	
	private WorldPregenData worldPregenData;
	
	private long generated_chunks_count;
	
	private final ThreadPoolExecutor executor =
		(ThreadPoolExecutor)Executors.newFixedThreadPool( ServerConfig.getThreadCount() );
	
	
	public PregenTask( WorldPos center, int _radius, ResourceKey<Level> _dimension, boolean _forceGeneration ) {
		
		center_x = center.getX();
		center_z = center.getZ();
		radius = _radius;
		dimension = _dimension;
		forceGeneration = _forceGeneration;
		worldPregenData = new WorldPregenData( center_x, center_z, radius );
	}
	
	public PregenTask() {
		
		//No Op
	}
	
	public boolean generate( MinecraftServer server ) {
		
		if( canceled ) {
			return true;
		}
		if( ServerConfig.isRunParallel() ) {
			worldPregenData.nextChunk().ifPresent( currentPos -> {
				if( shouldBeGenerated( server, currentPos ) ) {
					executor.submit( () -> generate( server, currentPos ) );
				} else {
					incGeneratedChunksCount();
				}
			} );
		} else {
			worldPregenData.nextChunk().ifPresent( currentPos -> {
				if( shouldBeGenerated( server, currentPos ) ) {
					generate( server, currentPos );
				} else {
					incGeneratedChunksCount();
				}
			} );
		}
		return getGeneratedChunksCount() >= getChunkCount();
	}
	
	private boolean shouldBeGenerated( MinecraftServer server, WorldPos pos ) {
		
		return forceGeneration || Objects.requireNonNull( server.getLevel( dimension ) ).getChunkSource()
			.getChunk( pos.getX(), pos.getZ(), ChunkStatus.FULL, false ) == null;
	}
	
	private void generate( MinecraftServer server, WorldPos pos ) {
		
		Objects.requireNonNull( server.getLevel( dimension ) ).getChunkSource()
			.getChunk( pos.getX(), pos.getZ(), ChunkStatus.FULL, true );
		incGeneratedChunksCount();
	}
	
	public void cancel() {
		
		canceled = true;
	}
	
	@Nonnull
	@Override
	public JsonObject write() {
		
		JsonObject compound = new JsonObject();
		compound.addProperty( centerXName, center_x );
		compound.addProperty( centerZName, center_z );
		compound.addProperty( radiusName, radius );
		compound.addProperty( dimensionName, DimensionHelper.getNameOfDim( dimension ) );
		compound.addProperty( chunkIndexName, getGeneratedChunksCount() );
		return compound;
	}
	
	@Override
	public void read( @Nonnull JsonObject json ) {
		
		if( JsonHelper.isInt( json, centerXName ) ) {
			center_x = JsonHelper.getInt( json, centerXName );
		} else {
			throw new IllegalArgumentException( "Invalid center x value." );
		}
		if( JsonHelper.isInt( json, centerZName ) ) {
			center_z = JsonHelper.getInt( json, centerZName );
		} else {
			throw new IllegalArgumentException( "Invalid center z value." );
		}
		if( JsonHelper.isInt( json, radiusName ) ) {
			radius = JsonHelper.getInt( json, radiusName );
		} else {
			throw new IllegalArgumentException( "Invalid radius value." );
		}
		if( JsonHelper.isString( json, dimensionName ) ) {
			try {
				dimension = DimensionHelper.getDimFromName( JsonHelper.getString( json, dimensionName ) );
			} catch( ResourceLocationException exception ) {
				throw new IllegalArgumentException( "Invalid dimension resource location.", exception );
			}
		} else {
			throw new IllegalArgumentException( "Invalid dimension value." );
		}
		worldPregenData = new WorldPregenData( center_x, center_z, radius );
		if( JsonHelper.isInt( json, chunkIndexName ) ) {
			worldPregenData.setChunkIndex( JsonHelper.getInt( json, chunkIndexName ) );//TODO: Set generated chunks
			generated_chunks_count = worldPregenData.getChunkCount();
		} else {
			throw new IllegalArgumentException( "Invalid chunk index value." );
		}
	}
	
	public boolean isDimensionInvalid( MinecraftServer server ) {
		
		return server.getLevel( dimension ) == null;
	}
	
	private synchronized void incGeneratedChunksCount() {
		
		generated_chunks_count++;
	}
	
	private synchronized long getGeneratedChunksCount() {
		
		return generated_chunks_count;
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
	
	public ResourceKey<Level> getDimension() {
		
		return dimension;
	}
	
	public long getChunkIndex() {
		
		return getGeneratedChunksCount();
	}
	
	public long getChunkCount() {
		
		return worldPregenData.getChunkCount();
	}
	
	public long getProgress() {
		
		return getGeneratedChunksCount() * 100 / getChunkCount();
	}
	
	public ThreadPoolExecutor getExecutor() {
		
		return executor;
	}
	
	public void shutdown() {
		
		executor.shutdownNow();
	}
}
