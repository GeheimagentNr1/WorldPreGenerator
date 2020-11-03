package de.geheimagentnr1.world_pre_generator.generator.tasks;

import com.google.common.base.Stopwatch;
import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.TimeUnit;


public class PrintTask {
	
	
	private final MinecraftServer server;
	
	private final Stopwatch timer = Stopwatch.createStarted();
	
	private static boolean isFeedbackEnabled = true;
	
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
		ITextComponent message = new StringTextComponent( "pregen " )
			.appendString( DimensionHelper.getNameOfDim( task.getDimension() ) )
			.appendString( " " ).appendString( String.valueOf( task.getChunkIndex() ) )
			.appendString( "/" ).appendString( String.valueOf( task.getChunkCount() ) )
			.appendString( "(" ).appendString( String.valueOf( task.getProgress() ) )
			.appendString( "%) " ).appendString( String.valueOf( ( task.getChunkIndex() - chunks ) / TIMING ) )
			.appendString( " chunks/s" );
		if( isFeedbackEnabled() ) {
			server.getPlayerList().func_232641_a_( message, ChatType.SYSTEM, Util.DUMMY_UUID );
		} else {
			server.sendMessage( message, Util.DUMMY_UUID );
		}
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
	
	public static void setIsFeedbackEnabled( boolean _isFeedbackEnabled ) {
		
		isFeedbackEnabled = _isFeedbackEnabled;
	}
	
	public static boolean isFeedbackEnabled() {
		
		return isFeedbackEnabled;
	}
}
