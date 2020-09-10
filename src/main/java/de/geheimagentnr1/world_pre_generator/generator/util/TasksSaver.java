package de.geheimagentnr1.world_pre_generator.generator.util;

import com.google.gson.*;
import de.geheimagentnr1.world_pre_generator.WorldPreGenerator;
import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PreGeneratorTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PrintTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class TasksSaver {
	
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String isFeedbackEnabledName = "isFeedbackEnabled";
	
	private static final String tasksName = "tasks";
	
	private static final String dimensionName = "dimension";
	
	private static final String centerXName = "centerX";
	
	private static final String centerZName = "centerZ";
	
	private static final String radiusName = "radius";
	
	private static final String chunkIndexName = "chunkIndex";
	
	public static void saveTasks() {
		
		if( TaskQueue.isNotEmpty() ) {
			File tasksFile = getTasksFile();
			
			try {
				if( tasksFile.exists() || tasksFile.getParentFile().mkdirs() && tasksFile.createNewFile() ) {
					FileWriter fileWriter = new FileWriter( tasksFile );
					JsonObject saveJson = new JsonObject();
					saveJson.addProperty( isFeedbackEnabledName, PrintTask.isFeedbackEnabled() );
					saveJson.add( tasksName, getTasksAsJson() );
					new GsonBuilder().setPrettyPrinting().create().toJson( saveJson, fileWriter );
					fileWriter.flush();
					fileWriter.close();
				}
			} catch( IOException exception ) {
				LOGGER.error( "World Pregen Tasks could no be saved", exception );
			}
		}
	}
	
	private static JsonArray getTasksAsJson() {
		
		JsonArray jsonTasks = new JsonArray();
		
		for( PreGeneratorTask task : TaskQueue.getTasks() ) {
			JsonObject jsonTask = new JsonObject();
			jsonTask.addProperty( dimensionName, DimensionHelper.getNameOfDim( task.getDimension() ) );
			jsonTask.addProperty( centerXName, task.getCenterX() );
			jsonTask.addProperty( centerZName, task.getCenterZ() );
			jsonTask.addProperty( radiusName, task.getRadius() );
			jsonTask.addProperty( chunkIndexName, task.getChunkIndex() );
			jsonTasks.add( jsonTask );
		}
		return jsonTasks;
	}
	
	public static void loadTasks( MinecraftServer server ) {
		
		File tasksFile = getTasksFile();
		
		if( tasksFile.exists() ) {
			try {
				FileReader reader = new FileReader( tasksFile );
				JsonElement saveJsonElement = new JsonParser().parse( reader );
				if( saveJsonElement.isJsonObject() ) {
					JsonObject saveJson = saveJsonElement.getAsJsonObject();
					JsonElement tasksJsonElement = saveJson.get( tasksName );
					if( tasksJsonElement.isJsonArray() ) {
						PrintTask.setIsFeedbackEnabled( saveJson.get( isFeedbackEnabledName ).getAsBoolean() );
						loadTasksFromJson( server, tasksJsonElement.getAsJsonArray() );
					} else {
						corruptSaveFileError();
					}
				} else {
					corruptSaveFileError();
				}
				reader.close();
			} catch( IOException exception ) {
				LOGGER.error( "World Pregen Tasks could no be loaded", exception );
			}
			if( !tasksFile.delete() || !tasksFile.getParentFile().delete() ) {
				LOGGER.error( "Tasks File could not be deleted" );
			}
		}
	}
	
	private static void loadTasksFromJson( MinecraftServer server, JsonArray jsonTasks ) {
		
		ArrayList<PreGeneratorTask> tasks = new ArrayList<>();
		
		for( int i = 0; i < jsonTasks.size(); i++ ) {
			if( jsonTasks.get( i ).isJsonObject() ) {
				JsonObject jsonTask = jsonTasks.get( i ).getAsJsonObject();
				tasks.add( new PreGeneratorTask( server, jsonTask.get( centerXName ).getAsInt(),
					jsonTask.get( centerZName ).getAsInt(), jsonTask.get( radiusName ).getAsInt(),
					DimensionHelper.getDimFromName( jsonTask.get( dimensionName ).getAsString() ),
					jsonTask.get( chunkIndexName ).getAsInt() ) );
			} else {
				corruptSaveFileError();
				return;
			}
		}
		for( PreGeneratorTask task : tasks ) {
			TaskQueue.add( task );
		}
	}
	
	private static void corruptSaveFileError() {
		
		LOGGER.error( "World Pregen corrupt tasks save file" );
	}
	
	private static File getTasksFile() {
		
		return new File( "." + File.separator + WorldPreGenerator.MODID + File.separator + "preGenTasks.json" );
	}
}
