package de.geheimagentnr1.world_pre_generator.elements.queues.lists;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class PregenTaskList implements Savable<JsonArray> {
	
	
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
		
		return runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ).shutdown() );
	}
	
	public void removeFirst() {
		
		task_list.remove( 0 ).shutdown();
	}
	
	public void removeBy( ResourceKey<Level> dimension ) {
		
		runFor( dimension, ( list, index ) -> task_list.remove( index.intValue() ).shutdown() );
	}
	
	public void clear() {
		
		task_list.forEach( PregenTask::shutdown );
		task_list.clear();
	}
	
	@Nonnull
	@Override
	public JsonArray write() {
		
		JsonArray json = new JsonArray();
		task_list.forEach( task -> {
			task.shutdown();
			json.add( task.write() );
		} );
		return json;
	}
	
	@Override
	public void read( @Nonnull JsonArray jsonArray ) {
		
		clear();
		for( JsonElement element : jsonArray ) {
			if( element.isJsonObject() ) {
				JsonObject json = element.getAsJsonObject();
				PregenTask task = new PregenTask();
				try {
					task.read( json );
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
