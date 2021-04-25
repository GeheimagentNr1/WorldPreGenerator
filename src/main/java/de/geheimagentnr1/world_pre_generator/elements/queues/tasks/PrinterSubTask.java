package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;

import java.time.Duration;
import java.time.LocalDateTime;


public class PrinterSubTask extends TimedSubTask {
	
	
	private final MinecraftServer server;
	
	private final PregenTaskQueue queue;
	
	private LocalDateTime old_time;
	
	private int old_chunks;
	
	public PrinterSubTask( MinecraftServer _server, PregenTaskQueue _queue ) {
		
		server = _server;
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
			
			ITextComponent message = new StringTextComponent( String.format(
				"pregen %s %d/%d(%d%%) %d chunks/s",
				DimensionHelper.getNameOfDim( task.getDimension() ),
				task.getChunkIndex(),
				task.getChunkCount(),
				task.getProgress(),
				Math.max( 0, task.getChunkIndex() - old_chunks ) / ( duration == 0 ? 1 : duration )
			) ).func_230530_a_( Style.field_240709_b_.func_240712_a_( TextFormatting.GRAY ) );
			if( ServerConfig.isSendFeedbackEnabled() ) {
				server.getPlayerList().func_232641_a_( message, ChatType.SYSTEM, Util.field_240973_b_ );
			} else {
				server.sendMessage( message, Util.field_240973_b_ );
			}
			old_time = new_time;
			old_chunks = task.getChunkIndex();
		} );
	}
}
