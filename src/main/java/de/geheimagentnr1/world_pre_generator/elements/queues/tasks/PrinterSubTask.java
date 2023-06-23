package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.PregenTaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;


@RequiredArgsConstructor
public class PrinterSubTask extends TimedSubTask {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final PregenTaskQueue queue;
	
	private MinecraftServer server;
	
	private LocalDateTime old_time;
	
	private long old_chunks;
	
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
		
		return serverConfig.getPrintDelay();
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
			if( serverConfig.isSendFeedbackEnabled() ) {
				server.getPlayerList().broadcastSystemMessage( message, false );
			} else {
				server.sendSystemMessage( message );
			}
			old_time = new_time;
			old_chunks = task.getChunkIndex();
		} );
	}
	
	public void setServer( @NotNull MinecraftServer _server ) {
		
		server = _server;
	}
}
