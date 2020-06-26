package de.geheimagentnr1.world_pre_generator.generator;

import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.generator.tasks.CleanUpTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PreGeneratorTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PrintTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
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
			task.getServer().getPlayerList().func_232641_a_( new StringTextComponent( "Generation of " )
					.func_240702_b_( DimensionHelper.getNameOfDim( task.getDimension() ) ).func_240702_b_( " started" +
						"." ),
				ChatType.SYSTEM, Util.field_240973_b_ );
		}
		boolean finished = task.generateNext();
		cleanUpTask.run();
		printTask.run();
		if( finished ) {
			cleanUpTask.stop();
			printTask.stop();
			printTask.print();
			task.getServer().getPlayerList().func_232641_a_( new StringTextComponent( "Generation of " )
					.func_240702_b_( DimensionHelper.getNameOfDim( task.getDimension() ) )
					.func_240702_b_( " finished." ),
				ChatType.SYSTEM, Util.field_240973_b_ );
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
