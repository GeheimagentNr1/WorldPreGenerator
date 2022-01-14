package de.geheimagentnr1.world_pre_generator.save;

import com.google.gson.*;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.helpers.JsonHelper;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class PregenerationWorldPersistencer {
	
	
	private static final Logger LOGGER = LogManager.getLogger( PregenerationWorldPersistencer.class );
	
	private static final PregenerationWorldPersistencer INSTANCE = new PregenerationWorldPersistencer();
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private static final LevelResource WORLD_PRE_GENERATOR_RESOURCE = new LevelResource( "world_pre_generator.dat" );
	
	private static final String queueName = "queue";
	
	private static final PregenWorker PREGEN_WORKER = PregenWorker.getInstance();
	
	private PregenerationWorldPersistencer() {
	
	}
	
	public static PregenerationWorldPersistencer getInstance() {
		
		return INSTANCE;
	}
	
	private String getFilePath() {
		
		return ServerLifecycleHooks.getCurrentServer().getWorldPath( WORLD_PRE_GENERATOR_RESOURCE ).toString();
	}
	
	public void load() {
		
		try(FileReader reader = new FileReader( getFilePath() )) {
			JsonObject json = GSON.fromJson( reader, JsonObject.class );
			
			if( JsonHelper.isJsonObject( json, queueName ) ) {
				PREGEN_WORKER.clearUp();
				PREGEN_WORKER.getQueue().read( json.getAsJsonObject( queueName ) );
			}
		} catch( FileNotFoundException ignored ) {
			LOGGER.debug( "File {} not found", getFilePath() );
		} catch( JsonParseException | IOException exception ) {
			LOGGER.error( "{} could not be readed", getFilePath(), exception );
		}
	}
	
	public void save() {
		
		JsonObject json = new JsonObject();
		json.add( queueName, PREGEN_WORKER.getQueue().write() );
		
		try( FileWriter writer = new FileWriter( getFilePath() ) ) {
			
			GSON.toJson( json, writer );
		} catch( IOException | JsonIOException exception ) {
			LOGGER.error( "{} could not be written", getFilePath(), exception );
		}
	}
}
