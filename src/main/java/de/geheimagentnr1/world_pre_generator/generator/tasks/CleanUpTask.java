package de.geheimagentnr1.world_pre_generator.generator.tasks;

import com.google.common.base.Stopwatch;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.TimeUnit;


public class CleanUpTask {
	
	
	private final MinecraftServer server;
	
	private final Stopwatch timer = Stopwatch.createStarted();
	
	private final PrintTask printTask;
	
	public CleanUpTask( MinecraftServer _server, PrintTask _printTask ) {
		
		server = _server;
		printTask = _printTask;
	}
	
	public void run() {
		
		if( timer.elapsed( TimeUnit.SECONDS ) < 180 ) {
			return;
		}
		clean();
		timer.reset().start();
	}
	
	@SuppressWarnings( "CallToSystemGC" )
	public void clean() {
		
		printTask.stop();
		server.save( false, true, true );
		System.gc();
		printTask.start();
	}
	
	public void stop() {
		
		if( timer.isRunning() ) {
			timer.stop();
		}
	}
}
