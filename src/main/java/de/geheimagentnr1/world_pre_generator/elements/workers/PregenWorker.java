package de.geheimagentnr1.world_pre_generator.elements.workers;

import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.PrinterSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.SaverSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.WorldWorkerManager;

import java.util.Optional;


public class PregenWorker implements WorldWorkerManager.IWorker {
	
	
	private static final PregenWorker INSTANCE = new PregenWorker();
	
	private MinecraftServer server;
	
	private final PregenTaskQueue queue;
	
	private final PrinterSubTask printer;
	
	private final SaverSubTask saver;
	
	private boolean startingNewTask = true;
	
	private PregenWorker() {
		
		queue = new PregenTaskQueue();
		printer = new PrinterSubTask( queue );
		saver = new SaverSubTask( printer );
	}
	
	public static PregenWorker getInstance() {
		
		return INSTANCE;
	}
	
	public void setServer( MinecraftServer _server ) {
		
		server = _server;
		queue.setServer( server );
		printer.setServer( server );
		saver.setServer( server );
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
				server.getPlayerList().broadcastSystemMessage(
					Component.literal( String.format(
						"Generation of %s started.",
						DimensionHelper.getNameOfDim( task.getDimension() )
					) ).setStyle( Style.EMPTY.withColor( TextColor.fromLegacyFormat( ChatFormatting.GRAY ) ) ),
					ChatType.SYSTEM
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
				server.getPlayerList().broadcastSystemMessage(
					Component.literal( String.format(
						"Generation of %s finished.",
						DimensionHelper.getNameOfDim( task.getDimension() )
					) ).setStyle( Style.EMPTY.withColor( TextColor.fromLegacyFormat( ChatFormatting.GRAY ) ) ),
					ChatType.SYSTEM
				);
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
