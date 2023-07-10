package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen;

import com.google.gson.JsonObject;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPregenData;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import de.geheimagentnr1.world_pre_generator.helpers.JsonHelper;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import lombok.Getter;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class PregenTask implements Savable<JsonObject> {
	
	
	@NotNull
	private static final String taskTypeName = "type";
	
	@NotNull
	private static final String centerXName = "center_x";
	
	@NotNull
	private static final String centerZName = "center_z";
	
	@NotNull
	private static final String radiusName = "radius";
	
	@NotNull
	private static final String chunkIndexName = "chunk_index";
	
	@NotNull
	private static final String dimensionName = "dimension";
	
	@Getter
	private TaskType taskType;
	
	@Getter
	private int centerX;
	
	@Getter
	private int centerZ;
	
	@Getter
	private int radius;
	
	@Getter
	private boolean forceGeneration;
	
	@Getter
	private ResourceKey<Level> dimension;
	
	private boolean canceled = false;
	
	private WorldPregenData worldPregenData;
	
	private long generated_chunks_count;
	
	private ThreadPoolExecutor executor;
	
	
	public PregenTask(
		@NotNull TaskType _taskType,
		@NotNull WorldPos center,
		int _radius,
		@NotNull ResourceKey<Level> _dimension,
		boolean _forceGeneration ) {
		
		taskType = _taskType;
		centerX = center.getX();
		centerZ = center.getZ();
		radius = _radius;
		dimension = _dimension;
		forceGeneration = _forceGeneration;
		worldPregenData = new WorldPregenData( centerX, centerZ, radius );
	}
	
	public PregenTask() {
		
		//No Op
	}
	
	public boolean generate( @NotNull MinecraftServer server, @NotNull ServerConfig serverConfig ) {
		
		if( canceled ) {
			return true;
		}
		switch( serverConfig.getGenerationType() ) {
			case SERIAL -> worldPregenData.nextChunk().ifPresent( currentPos -> {
				if( shouldBeGenerated( server, currentPos ) ) {
					generate( server, currentPos );
				} else {
					incGeneratedChunksCount();
				}
			} );
			case SEMI_PARALLEL -> {
				if( executor == null || executor.isShutdown() ) {
					executor =
						(ThreadPoolExecutor)Executors.newFixedThreadPool( serverConfig.getGenerationSemiParallelTaskCount() );
				}
				if( (long)serverConfig.getGenerationSemiParallelTaskCount() << 1 >
					executor.getTaskCount() - executor.getCompletedTaskCount() ) {
					worldPregenData.nextChunk().ifPresent( currentPos -> {
						if( shouldBeGenerated( server, currentPos ) ) {
							executor.submit( () -> {
								try {
									generate( server, currentPos );
								} catch( Exception ignored ) {
								
								}
							} );
						} else {
							incGeneratedChunksCount();
						}
					} );
				}
			}
		}
		return getGeneratedChunksCount() >= getChunkCount();
	}
	
	private boolean shouldBeGenerated( @NotNull MinecraftServer server, @NotNull WorldPos pos ) {
		
		ServerLevel serverLevel = Objects.requireNonNull( server.getLevel( dimension ) );
		return forceGeneration || !serverLevel.hasChunk( pos.getX(), pos.getZ() )
			&& Optional.ofNullable( serverLevel.getChunk( pos.getX(), pos.getZ(), ChunkStatus.EMPTY, true ) ).stream()
			.noneMatch( chunk -> chunk.getStatus().isOrAfter( ChunkStatus.FULL ) );
	}
	
	private void generate( @NotNull MinecraftServer server, @NotNull WorldPos pos ) {
		
		Objects.requireNonNull( server.getLevel( dimension ) ).getChunkSource()
			.getChunk( pos.getX(), pos.getZ(), ChunkStatus.FULL, true );
		incGeneratedChunksCount();
	}
	
	public void cancel() {
		
		canceled = true;
	}
	
	@NotNull
	@Override
	public JsonObject write() {
		
		JsonObject compound = new JsonObject();
		compound.addProperty( taskTypeName, taskType.name() );
		compound.addProperty( centerXName, centerX );
		compound.addProperty( centerZName, centerZ );
		compound.addProperty( radiusName, radius );
		compound.addProperty( dimensionName, DimensionHelper.getNameOfDim( dimension ) );
		compound.addProperty( chunkIndexName, getGeneratedChunksCount() );
		return compound;
	}
	
	@Override
	public void read( @NotNull JsonObject json ) {
		
		if( json.has( taskTypeName ) ) {
			if( JsonHelper.isString( json, taskTypeName ) ) {
				taskType = TaskType.valueOf( JsonHelper.getString( json, taskTypeName ) );
			} else {
				throw new IllegalArgumentException( "Invalid type value." );
			}
		} else {
			taskType = TaskType.CHUNK;
		}
		if( JsonHelper.isInt( json, centerXName ) ) {
			centerX = JsonHelper.getInt( json, centerXName );
		} else {
			throw new IllegalArgumentException( "Invalid center x value." );
		}
		if( JsonHelper.isInt( json, centerZName ) ) {
			centerZ = JsonHelper.getInt( json, centerZName );
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
		worldPregenData = new WorldPregenData( centerX, centerZ, radius );
		if( JsonHelper.isInt( json, chunkIndexName ) ) {
			worldPregenData.setChunkIndex( JsonHelper.getInt( json, chunkIndexName ) );
			// Needed because of Synchronization
			// noinspection CallToSimpleSetterFromWithinClass
			setGeneratedChunksCount( worldPregenData.getChunkIndex() );
		} else {
			throw new IllegalArgumentException( "Invalid chunk index value." );
		}
	}
	
	public boolean isDimensionInvalid( @NotNull MinecraftServer server ) {
		
		return server.getLevel( dimension ) == null;
	}
	
	private synchronized void incGeneratedChunksCount() {
		
		generated_chunks_count++;
	}
	
	private synchronized void setGeneratedChunksCount( long _generated_chunks_count ) {
		
		generated_chunks_count = _generated_chunks_count;
	}
	
	private synchronized long getGeneratedChunksCount() {
		
		return generated_chunks_count;
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
	
	public void shutdown() {
		
		if( executor != null && !executor.isShutdown() ) {
			executor.shutdownNow();
		}
	}
}
