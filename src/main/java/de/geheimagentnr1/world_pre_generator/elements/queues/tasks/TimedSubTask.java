package de.geheimagentnr1.world_pre_generator.elements.queues.tasks;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;


public abstract class TimedSubTask {
	
	
	private final Stopwatch timer = Stopwatch.createStarted();
	
	public void start() {
		
		timer.reset().start();
	}
	
	//package-private
	void resume() {
		
		timer.reset().start();
	}
	
	//package-private
	void pause() {
		
		timer.reset();
	}
	
	public void stop() {
		
		timer.reset();
	}
	
	public void run() {
		
		if( timer.elapsed( TimeUnit.SECONDS ) >= getDelay() ) {
			execute();
			timer.reset().start();
		}
	}
	
	//package-private
	abstract int getDelay();
	
	protected abstract void execute();
}
