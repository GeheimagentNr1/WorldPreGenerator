package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;
import java.time.LocalDateTime;


public class PrinterSubTask extends TimedSubTask {
	
	
	private MinecraftServer server;
	
	private final PregenTaskQueue queue;
	
	private LocalDateTime old_time;
	
	private int old_chunks;
	
	public PrinterSubTask( PregenTaskQueue _queue ) {
		
		queue = _queue;
	}
	
	@Override
	public void start() {
		
		super.start();
		old_time = LocalDateTime.now();
		old_chunks = 0;
	}
	
	@Override
	public void resume() {
		
		super.resume();
		old_time = LocalDateTime.now();
	}
	
	//package-private
	int getDelay() {
		
		return ServerConfig.getPrintDelay();
	}
	
	@Override
	public void execute() {
		
		queue.getCurrentTask().ifPresent( task -> {
			LocalDateTime new_time = LocalDateTime.now();
			long duration = Duration.between( old_time, new_time ).getSeconds();
			
			Component message = Component.literal( String.format(
				"pregen %s %d/%d(%d%%) %d chunks/s",
				DimensionHelper.getNameOfDim( task.getDimension() ),
				task.getChunkIndex(),
				task.getChunkCount(),
				task.getProgress(),
				Math.max( 0, task.getChunkIndex() - old_chunks ) / ( duration == 0 ? 1 : duration )
			) ).setStyle( Style.EMPTY.withColor( TextColor.fromLegacyFormat( ChatFormatting.GRAY ) ) );
			if( ServerConfig.isSendFeedbackEnabled() ) {
				server.getPlayerList().broadcastSystemMessage( message, ChatType.SYSTEM );
			} else {
				server.sendSystemMessage( message );
			}
			old_time = new_time;
			old_chunks = task.getChunkIndex();
		} );
	}
	
	public void setServer( MinecraftServer _server ) {
		
		server = _server;
	}
}
