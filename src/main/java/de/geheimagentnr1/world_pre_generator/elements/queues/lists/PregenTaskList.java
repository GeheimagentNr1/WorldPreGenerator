package de.geheimagentnr1.world_pre_generator.elements.queues.lists;

import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.save.NBTType;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;


public class PregenTaskList implements Savable<ListNBT> {
	
	
	private static final Logger LOGGER = LogManager.getLogger( PregenTaskList.class );
	
	private final ArrayList<PregenTask> task_list = new ArrayList<>();
	
	public Optional<PregenTask> runFor(
		RegistryKey<World> dimension,
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
		if( !runFor( new_task.getDimension(), ( list, index ) -> list.set( index, new_task ) ).isPresent() ) {
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
	
	public Optional<PregenTask> getAndRemoveBy( RegistryKey<World> dimension ) {
		
		return runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ) );
	}
	
	public void removeFirst() {
		
		task_list.remove( 0 );
	}
	
	public void removeBy( RegistryKey<World> dimension ) {
		
		runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ) );
	}
	
	public void clear() {
		
		task_list.clear();
	}
	
	@Nonnull
	@Override
	public ListNBT writeNBT() {
		
		ListNBT nbt = new ListNBT();
		for( PregenTask task : task_list ) {
			nbt.add( task.writeNBT() );
		}
		return nbt;
	}
	
	@Override
	public void readNBT( @Nonnull ListNBT nbt ) {
		
		clear();
		for( INBT inbt : nbt ) {
			if( inbt.getId() == NBTType.COMPOUND.getId() ) {
				PregenTask task = new PregenTask();
				try {
					task.readNBT( (CompoundNBT)inbt );
					addOrReplace( task );
				} catch( IllegalArgumentException exception ) {
					LOGGER.error( "Invalid task: Task is not added to queue.", exception );
				}
			}
		}
	}
}
