package de.geheimagentnr1.world_pre_generator.elements.queues;

import de.geheimagentnr1.world_pre_generator.elements.queues.lists.PregenTaskList;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.SaveHelper;
import de.geheimagentnr1.world_pre_generator.save.NBTType;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;


public class PregenTaskQueue implements Savable<CompoundNBT> {
	
	
	private static final String activeTasksName = "active_tasks";
	
	private static final String pausedTasksName = "paused_tasks";
	
	private MinecraftServer server;
	
	private final PregenTaskList active_tasks = new PregenTaskList();
	
	private final PregenTaskList paused_tasks = new PregenTaskList();
	
	public synchronized Optional<PregenTask> getCurrentTask() {
		
		return active_tasks.getFirst();
	}
	
	public ArrayList<PregenTask> getActiveTasks() {
		
		return active_tasks.getTaskList();
	}
	
	public ArrayList<PregenTask> getPausedTasks() {
		
		return paused_tasks.getTaskList();
	}
	
	public boolean isNotEmpty() {
		
		return !active_tasks.isEmpty();
	}
	
	public boolean noTasks() {
		
		return active_tasks.isEmpty() && paused_tasks.isEmpty();
	}
	
	public synchronized void startTask( PregenTask new_task ) {
		
		active_tasks.addOrReplace( new_task );
		paused_tasks.removeBy( new_task.getDimension() );
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void resumeTask( RegistryKey<World> dimension ) {
		
		paused_tasks.getAndRemoveBy( dimension ).ifPresent( active_tasks::addOrReplace );
		SaveHelper.saveWorld( server );
		
	}
	
	public synchronized void pauseTask( RegistryKey<World> dimension ) {
		
		active_tasks.getAndRemoveBy( dimension ).ifPresent( paused_tasks::addOrReplace );
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void cancelTask( RegistryKey<World> dimension ) {
		
		active_tasks.runFor( dimension, ( list, index ) -> list.get( index ).cancel() );
		paused_tasks.removeBy( dimension );
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void removeCurrentTask() {
		
		active_tasks.removeFirst();
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void clearUp() {
		
		active_tasks.clear();
		paused_tasks.clear();
	}
	
	public synchronized void clear() {
		
		clearUp();
		SaveHelper.saveWorld( server );
	}
	
	@Nonnull
	@Override
	public synchronized CompoundNBT writeNBT() {
		
		CompoundNBT compound = new CompoundNBT();
		compound.put( activeTasksName, active_tasks.writeNBT() );
		compound.put( pausedTasksName, paused_tasks.writeNBT() );
		return compound;
	}
	
	public synchronized void readNBT( @Nonnull CompoundNBT nbt ) {
		
		if( nbt.contains( activeTasksName, NBTType.LIST.getId() ) ) {
			active_tasks.readNBT( nbt.getList( activeTasksName, NBTType.COMPOUND.getId() ) );
		}
		paused_tasks.readNBT( nbt.getList( pausedTasksName, NBTType.COMPOUND.getId() ) );
	}
	
	public synchronized void setServer( MinecraftServer _server ) {
		
		server = _server;
		active_tasks.checkTasks( server );
		paused_tasks.checkTasks( server );
	}
}
