package de.geheimagentnr1.world_pre_generator.generator;

import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.generator.tasks.CleanUpTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PreGeneratorTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PrintTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.WorldWorkerManager;


public class PreGenWorker implements WorldWorkerManager.IWorker {
	
	
	private CleanUpTask cleanUpTask;
	
	private PrintTask printTask;
	
	private boolean startingNewTask = true;
	
	@Override
	public boolean hasWork() {
		
		return true;
	}
	
	/**
	 * Perform a task, returning true from this will have the manager call this function again this tick if there is
	 * time left.
	 * Returning false will skip calling this worker until next tick.
	 */
	@Override
	public boolean doWork() {
		
		PreGeneratorTask task = TaskQueue.getCurrentTask();
		
		if( task == null ) {
			return false;
		}
		if( startingNewTask ) {
			printTask = new PrintTask( task.getServer() );
			cleanUpTask = new CleanUpTask( task.getServer(), printTask );
			startingNewTask = false;
			task.getServer().getPlayerList().sendMessage( new StringTextComponent( "Generation of " )
				.appendText( DimensionHelper.getNameOfDim( task.getDimension() ) ).appendText( " started." ) );
		}
		boolean finished = task.generateNext();
		cleanUpTask.run();
		printTask.run();
		if( finished ) {
			cleanUpTask.stop();
			printTask.stop();
			printTask.print();
			task.getServer().getPlayerList().sendMessage( new StringTextComponent( "Generation of " )
				.appendText( DimensionHelper.getNameOfDim( task.getDimension() ) ).appendText( " finished." ) );
			cleanUpTask.clean();
			cleanUpTask = null;
			printTask = null;
			TaskQueue.remove( task );
			startingNewTask = true;
			return TaskQueue.isNotEmpty();
		}
		return true;
	}
}
