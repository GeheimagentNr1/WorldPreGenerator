package de.geheimagentnr1.world_pre_generator.generator.tasks;

import com.google.common.base.Stopwatch;
import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
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
			.appendText( DimensionHelper.getNameOfDim( task.getDimension() ) )
			.appendText( " " ).appendText( String.valueOf( task.getChunkIndex() ) )
			.appendText( "/" ).appendText( String.valueOf( task.getChunkCount() ) )
			.appendText( "(" ).appendText( String.valueOf( task.getProgress() ) )
			.appendText( "%) " ).appendText( String.valueOf( ( task.getChunkIndex() - chunks ) / TIMING ) )
			.appendText( " chunks/s" );
		if( isFeedbackEnabled() ) {
			server.getPlayerList().sendMessage( message );
		} else {
			server.sendMessage( message );
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
