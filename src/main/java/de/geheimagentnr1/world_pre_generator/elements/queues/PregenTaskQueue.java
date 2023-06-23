package de.geheimagentnr1.world_pre_generator.elements.queues;

import com.google.gson.JsonObject;
import de.geheimagentnr1.world_pre_generator.elements.queues.lists.PregenTaskList;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.helpers.JsonHelper;
import de.geheimagentnr1.world_pre_generator.helpers.SaveHelper;
import de.geheimagentnr1.world_pre_generator.save.Savable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;


public class PregenTaskQueue implements Savable<JsonObject> {
	
	
	@NotNull
	private static final String activeTasksName = "active_tasks";
	
	@NotNull
	private static final String pausedTasksName = "paused_tasks";
	
	@NotNull
	private final PregenTaskList active_tasks = new PregenTaskList();
	
	@NotNull
	private final PregenTaskList paused_tasks = new PregenTaskList();
	
	private MinecraftServer server;
	
	@NotNull
	public synchronized Optional<PregenTask> getCurrentTask() {
		
		return active_tasks.getFirst();
	}
	
	@NotNull
	public ArrayList<PregenTask> getActiveTasks() {
		
		return active_tasks.getTaskList();
	}
	
	@NotNull
	public ArrayList<PregenTask> getPausedTasks() {
		
		return paused_tasks.getTaskList();
	}
	
	public boolean isNotEmpty() {
		
		return !active_tasks.isEmpty();
	}
	
	public boolean noTasks() {
		
		return active_tasks.isEmpty() && paused_tasks.isEmpty();
	}
	
	public synchronized void startTask( @NotNull PregenTask new_task ) {
		
		active_tasks.addOrReplace( new_task );
		paused_tasks.removeBy( new_task.getDimension() );
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void resumeTask( @NotNull ResourceKey<Level> dimension ) {
		
		paused_tasks.getAndRemoveBy( dimension ).ifPresent( active_tasks::addOrReplace );
		SaveHelper.saveWorld( server );
		
	}
	
	public synchronized void pauseTask( @NotNull ResourceKey<Level> dimension ) {
		
		active_tasks.getAndRemoveBy( dimension ).ifPresent( paused_tasks::addOrReplace );
		SaveHelper.saveWorld( server );
	}
	
	public synchronized void cancelTask( @NotNull ResourceKey<Level> dimension ) {
		
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
	
	@NotNull
	@Override
	public synchronized JsonObject write() {
		
		JsonObject json = new JsonObject();
		json.add( activeTasksName, active_tasks.write() );
		json.add( pausedTasksName, paused_tasks.write() );
		return json;
	}
	
	public synchronized void read( @NotNull JsonObject json ) {
		
		if( JsonHelper.isJsonArray( json, activeTasksName ) ) {
			active_tasks.read( json.getAsJsonArray( activeTasksName ) );
		}
		if( JsonHelper.isJsonArray( json, pausedTasksName ) ) {
			paused_tasks.read( json.getAsJsonArray( pausedTasksName ) );
		}
	}
	
	public synchronized void setServer( @NotNull MinecraftServer _server ) {
		
		server = _server;
		active_tasks.checkTasks( server );
		paused_tasks.checkTasks( server );
	}
}
