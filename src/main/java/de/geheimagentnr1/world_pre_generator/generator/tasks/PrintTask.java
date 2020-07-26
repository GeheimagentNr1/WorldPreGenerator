package de.geheimagentnr1.world_pre_generator.generator.tasks;

import com.google.common.base.Stopwatch;
import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.TimeUnit;


public class PrintTask {
	
	
	private final MinecraftServer server;
	
	private final Stopwatch timer = Stopwatch.createStarted();
	
	private static final int TIMING = 1;
	
	private int chunks;
	
	public PrintTask( MinecraftServer _server ) {
		
		server = _server;
	}
	
	public void run() {
		
		if( timer.elapsed( TimeUnit.SECONDS ) < TIMING ) {
			return;
		}
		print();
		timer.reset().start();
	}
	
	public void print() {
		
		PreGeneratorTask task = TaskQueue.getCurrentTask();
		if( task == null ) {
			return;
		}
		if( task.getChunkIndex() < chunks ) {
			chunks = 0;
		}
		server.getPlayerList().func_232641_a_( new StringTextComponent( "pregen " )
			.func_240702_b_( DimensionHelper.getNameOfDim( task.getDimension() ) )
			.func_240702_b_( " " ).func_240702_b_( String.valueOf( task.getChunkIndex() ) )
			.func_240702_b_( "/" ).func_240702_b_( String.valueOf( task.getChunkCount() ) )
			.func_240702_b_( "(" ).func_240702_b_( String.valueOf( task.getProgress() ) )
			.func_240702_b_( "%) " ).func_240702_b_( String.valueOf( ( task.getChunkIndex() - chunks ) / TIMING ) )
			.func_240702_b_( " chunks/s" ), ChatType.SYSTEM, Util.field_240973_b_ );
		chunks = task.getChunkIndex();
	}
	
	public void stop() {
		
		if( timer.isRunning() ) {
			timer.stop();
		}
	}
	
	//package-private
	void start() {
		
		timer.start();
	}
}
