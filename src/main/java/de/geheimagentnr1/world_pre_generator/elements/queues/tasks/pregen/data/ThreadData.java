package de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data;

public class ThreadData {
	
	
	private int count = 0;
	
	public synchronized void incCount() {
		
		count++;
	}
	
	public synchronized void decCount() {
		
		count--;
	}
	
	public synchronized int getCount() {
		
		return count;
	}
}
