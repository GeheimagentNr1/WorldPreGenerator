package de.geheimagentnr1.world_pre_generator.elements.workers;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.PrinterSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.SaverSubTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.WorldWorkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class PregenWorker implements WorldWorkerManager.IWorker {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final PregenTaskQueue queue;
	
	@NotNull
	private final PrinterSubTask printer;
	
	@NotNull
	private final SaverSubTask saver;
	
	private MinecraftServer server;
	
	private boolean startingNewTask = true;
	
	public PregenWorker( @NotNull ServerConfig _serverConfig ) {
		
		serverConfig = _serverConfig;
		queue = new PregenTaskQueue();
		printer = new PrinterSubTask( serverConfig, queue );
		saver = new SaverSubTask( serverConfig, printer );
	}
	
	public void setServer( @NotNull MinecraftServer _server ) {
		
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
					false
				);
				printer.start();
				saver.start();
			}
			boolean finished = task.generate( server, serverConfig );
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
					false
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
	
	@NotNull
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
