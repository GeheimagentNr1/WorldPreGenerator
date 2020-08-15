package de.geheimagentnr1.world_pre_generator.generator.queue;

import de.geheimagentnr1.world_pre_generator.generator.tasks.PreGeneratorTask;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;


public class TaskQueue {
	
	
	private static final ArrayList<PreGeneratorTask> tasks = new ArrayList<>();
	
	public static synchronized void add( PreGeneratorTask task ) {
		
		for( int i = 0; i < tasks.size(); i++ ) {
			PreGeneratorTask queuedtask = tasks.get( i );
			if( queuedtask.getDimension() == task.getDimension() ) {
				tasks.set( i, task );
				return;
			}
		}
		tasks.add( task );
	}
	
	public static synchronized void clear() {
		
		tasks.clear();
	}
	
	public static synchronized PreGeneratorTask getCurrentTask() {
		
		return tasks.isEmpty() ? null : tasks.get( 0 );
	}
	
	public static synchronized void remove( PreGeneratorTask task ) {
		
		tasks.remove( task );
	}
	
	public static boolean isNotEmpty() {
		
		return !tasks.isEmpty();
	}
	
	public static ArrayList<PreGeneratorTask> getTasks() {
		
		return tasks;
	}
	
	public static void cancelTask( DimensionType dimension ) {
		
		for( PreGeneratorTask task : tasks ) {
			if( task.getDimension() == dimension ) {
				task.cancel();
				return;
			}
		}
	}
}
