package de.geheimagentnr1.world_pre_generator.elements.queues.lists;

import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.save.NBTType;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;


public class PregenTaskList implements Savable<ListTag> {
	
	
	private static final Logger LOGGER = LogManager.getLogger( PregenTaskList.class );
	
	private final ArrayList<PregenTask> task_list = new ArrayList<>();
	
	public Optional<PregenTask> runFor(
		ResourceKey<Level> dimension,
		BiConsumer<ArrayList<PregenTask>, Integer> runner ) {
		
		for( int i = 0; i < task_list.size(); i++ ) {
			PregenTask task = task_list.get( i );
			if( task.getDimension() == dimension ) {
				runner.accept( task_list, i );
				return Optional.of( task );
			}
		}
		return Optional.empty();
	}
	
	public void addOrReplace( PregenTask new_task ) {
		
		if( new_task == null ) {
			return;
		}
		if( runFor( new_task.getDimension(), ( list, index ) -> list.set( index, new_task ) ).isEmpty() ) {
			task_list.add( new_task );
		}
	}
	
	public ArrayList<PregenTask> getTaskList() {
		
		return task_list;
	}
	
	public Optional<PregenTask> getFirst() {
		
		return task_list.isEmpty() ? Optional.empty() : Optional.of( task_list.get( 0 ) );
	}
	
	public boolean isEmpty() {
		
		return task_list.isEmpty();
	}
	
	public Optional<PregenTask> getAndRemoveBy( ResourceKey<Level> dimension ) {
		
		return runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ) );
	}
	
	public void removeFirst() {
		
		task_list.remove( 0 );
	}
	
	public void removeBy( ResourceKey<Level> dimension ) {
		
		runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ) );
	}
	
	public void clear() {
		
		task_list.clear();
	}
	
	@Nonnull
	@Override
	public ListTag writeNBT() {
		
		ListTag nbt = new ListTag();
		for( PregenTask task : task_list ) {
			nbt.add( task.writeNBT() );
		}
		return nbt;
	}
	
	@Override
	public void readNBT( @Nonnull ListTag nbt ) {
		
		clear();
		for( Tag inbt : nbt ) {
			if( inbt.getId() == NBTType.COMPOUND.getId() ) {
				PregenTask task = new PregenTask();
				try {
					task.readNBT( (CompoundTag)inbt );
					addOrReplace( task );
				} catch( IllegalArgumentException exception ) {
					LOGGER.error( "Invalid task: Task is not added to queue.", exception );
				}
			}
		}
	}
	
	public void checkTasks( MinecraftServer server ) {
		
		for( int i = 0; i < task_list.size(); i++ ) {
			PregenTask task = task_list.get( i );
			if( task.isDimensionInvalid( server ) ) {
				task_list.remove( i );
				i--;
				LOGGER.error( "Invalid task: Task is removed from queue." );
			}
		}
	}
}
