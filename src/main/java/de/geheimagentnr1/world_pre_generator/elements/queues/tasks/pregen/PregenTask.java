package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.ThreadData;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPregenData;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import de.geheimagentnr1.world_pre_generator.save.NBTType;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;

import javax.annotation.Nonnull;
import java.util.Objects;


public class PregenTask implements Savable<CompoundTag> {
	
	
	private static final String centerXName = "center_x";
	
	private static final String centerZName = "center_z";
	
	private static final String radiusName = "radius";
	
	private static final String chunkIndexName = "chunk_index";
	
	private static final String dimensionName = "dimension";
	
	private int center_x;
	
	private int center_z;
	
	private int radius;
	
	private ResourceKey<Level> dimension;
	
	private boolean canceled = false;
	
	private WorldPregenData worldPregenData;
	
	private final ThreadData threadData = new ThreadData();
	
	public PregenTask( WorldPos center, int _radius, ResourceKey<Level> _dimension ) {
		
		center_x = center.getX();
		center_z = center.getZ();
		radius = _radius;
		dimension = _dimension;
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
			if( threadData.getCount() < ServerConfig.getThreadCount() ) {
				worldPregenData.nextChunk().ifPresent( currentPos -> {
					if( isNotGenerated( server, currentPos ) ) {
						threadData.incCount();
						new Thread( () -> {
							generate( server, currentPos );
							threadData.decCount();
						} ).start();
					}
				} );
			}
		} else {
			worldPregenData.nextChunk().ifPresent( currentPos -> {
				if( isNotGenerated( server, currentPos ) ) {
					generate( server, currentPos );
				}
			} );
		}
		return worldPregenData.fullyGenerated();
	}
	
	private boolean isNotGenerated( MinecraftServer server, WorldPos pos ) {
		
		return Objects.requireNonNull( server.getLevel( dimension ) ).getChunkSource()
			.getChunk( pos.getX(), pos.getZ(), ChunkStatus.FULL, false ) == null;
	}
	
	private void generate( MinecraftServer server, WorldPos pos ) {
		
		Objects.requireNonNull( server.getLevel( dimension ) ).getChunkSource()
			.getChunk( pos.getX(), pos.getZ(), ChunkStatus.FULL, true );
	}
	
	public void cancel() {
		
		canceled = true;
	}
	
	@Nonnull
	@Override
	public CompoundTag writeNBT() {
		
		CompoundTag compound = new CompoundTag();
		compound.putInt( centerXName, center_x );
		compound.putInt( centerZName, center_z );
		compound.putInt( radiusName, radius );
		compound.putString( dimensionName, DimensionHelper.getNameOfDim( dimension ) );
		compound.putInt( chunkIndexName, getChunkIndex() );
		return compound;
	}
	
	@Override
	public void readNBT( @Nonnull CompoundTag nbt ) {
		
		if( nbt.contains( centerXName, NBTType.INT.getId() ) ) {
			center_x = nbt.getInt( centerXName );
		} else {
			throw new IllegalArgumentException( "Invalid center x value." );
		}
		if( nbt.contains( centerZName, NBTType.INT.getId() ) ) {
			center_z = nbt.getInt( centerZName );
		} else {
			throw new IllegalArgumentException( "Invalid center z value." );
		}
		if( nbt.contains( radiusName, NBTType.INT.getId() ) ) {
			radius = nbt.getInt( radiusName );
		} else {
			throw new IllegalArgumentException( "Invalid radius value." );
		}
		if( nbt.contains( dimensionName, NBTType.STRING.getId() ) ) {
			try {
				dimension = DimensionHelper.getDimFromName( nbt.getString( dimensionName ) );
			} catch( ResourceLocationException exception ) {
				throw new IllegalArgumentException( "Invalid dimension resource location.", exception );
			}
		} else {
			throw new IllegalArgumentException( "Invalid dimension value." );
		}
		worldPregenData = new WorldPregenData( center_x, center_z, radius );
		if( nbt.contains( chunkIndexName, NBTType.INT.getId() ) ) {
			worldPregenData.setChunkIndex( nbt.getInt( chunkIndexName ) );
		} else {
			throw new IllegalArgumentException( "Invalid chunk index value." );
		}
	}
	
	public boolean isDimensionInvalid( MinecraftServer server ) {
		
		return server.getLevel( dimension ) == null;
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
	
	public int getChunkIndex() {
		
		return worldPregenData.getChunkIndex();
	}
	
	public int getChunkCount() {
		
		return worldPregenData.getChunkCount();
	}
	
	public int getProgress() {
		
		return worldPregenData.getProgess();
	}
}
