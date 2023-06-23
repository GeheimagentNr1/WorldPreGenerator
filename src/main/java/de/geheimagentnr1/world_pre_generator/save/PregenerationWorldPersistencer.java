package de.geheimagentnr1.world_pre_generator.save;

import com.google.gson.*;
import de.geheimagentnr1.minecraft_forge_api.events.ForgeEventHandlerInterface;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.helpers.JsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


@Log4j2
@RequiredArgsConstructor
public class PregenerationWorldPersistencer implements ForgeEventHandlerInterface {
	
	
	@NotNull
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	@NotNull
	private static final LevelResource WORLD_PRE_GENERATOR_RESOURCE = new LevelResource( "world_pre_generator.dat" );
	
	@NotNull
	private static final String queueName = "queue";
	
	@NotNull
	private final PregenWorker pregenWorker;
	
	@NotNull
	private String getFilePath() {
		
		return ServerLifecycleHooks.getCurrentServer().getWorldPath( WORLD_PRE_GENERATOR_RESOURCE ).toString();
	}
	
	private void load() {
		
		try( FileReader reader = new FileReader( getFilePath() ) ) {
			JsonObject json = GSON.fromJson( reader, JsonObject.class );
			
			if( JsonHelper.isJsonObject( json, queueName ) ) {
				pregenWorker.clearUp();
				pregenWorker.getQueue().read( json.getAsJsonObject( queueName ) );
			}
		} catch( FileNotFoundException ignored ) {
			log.debug( "File {} not found", getFilePath() );
		} catch( JsonParseException | IOException exception ) {
			log.error( "{} could not be readed", getFilePath(), exception );
		}
	}
	
	private void save() {
		
		JsonObject json = new JsonObject();
		json.add( queueName, pregenWorker.getQueue().write() );
		
		try( FileWriter writer = new FileWriter( getFilePath() ) ) {
			
			GSON.toJson( json, writer );
		} catch( IOException | JsonIOException exception ) {
			log.error( "{} could not be written", getFilePath(), exception );
		}
	}
	
	@SubscribeEvent
	@Override
	public void handleServerStartingEvent( @NotNull ServerStartingEvent event ) {
		
		pregenWorker.setServer( event.getServer() );
		load();
	}
	
	@SubscribeEvent
	@Override
	public void handleServerStoppedEvent( @NotNull ServerStoppedEvent event ) {
		
		save();
		pregenWorker.clearUp();
	}
}
