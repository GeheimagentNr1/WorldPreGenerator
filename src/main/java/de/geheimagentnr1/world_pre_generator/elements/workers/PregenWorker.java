package de.geheimagentnr1.world_pre_generator.elements.workers;

import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.PrinterSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.SaverSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.WorldWorkerManager;

import java.util.Optional;


public class PregenWorker implements WorldWorkerManager.IWorker {
	
	
	private static final PregenWorker INSTANCE = new PregenWorker();
	
	private MinecraftServer server;
	
	private PregenTaskQueue queue;
	
	private PrinterSubTask printer;
	
	private SaverSubTask saver;
	
	private boolean startingNewTask = true;
	
	public static PregenWorker getInstance() {
		
		return INSTANCE;
	}
	
	public void setServer( MinecraftServer _server ) {
		
		server = _server;
		queue = new PregenTaskQueue( server );
		printer = new PrinterSubTask( server, queue );
		saver = new SaverSubTask( server, printer );
	}
	
	
	@Override
	public boolean hasWork() {
		
		return true;
	}
	
	@Override
	public boolean doWork() {
		
		Optional<PregenTask> next_task = queue.getCurrentTask();
		
		if( next_task.isPresent() ) {
			PregenTask task = next_task.get();
			if( startingNewTask ) {
				startingNewTask = false;
				server.getPlayerList().sendMessage(
					new StringTextComponent( String.format(
						"Generation of %s started.",
						DimensionHelper.getNameOfDim( task.getDimension() )
					) )
				);
				printer.start();
				saver.start();
			}
			boolean finished = task.generate( server );
			printer.run();
			saver.run();
			if( finished ) {
				printer.stop();
				saver.stop();
				printer.execute();
				server.getPlayerList().sendMessage( new StringTextComponent( String.format(
					"Generation of %s finished.",
					DimensionHelper.getNameOfDim( task.getDimension() )
				) ) );
				saver.execute();
				queue.removeCurrentTask();
				startingNewTask = true;
				return queue.isNotEmpty();
			}
			return true;
		}
		return false;
	}
	
	public PregenTaskQueue getQueue() {
		
		return queue;
	}
	
	public void clearUp() {
		
		queue.clearUp();
		startingNewTask = true;
		printer.stop();
		saver.stop();
	}
}
