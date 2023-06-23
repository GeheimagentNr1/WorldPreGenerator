package de.geheimagentnr1.world_pre_generator;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.commands.ModCommandsRegisterFactory;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.save.PregenerationWorldPersistencer;
import net.minecraftforge.common.WorldWorkerManager;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;


@Mod( WorldPreGenerator.MODID )
public class WorldPreGenerator extends AbstractMod {
	
	
	@NotNull
	static final String MODID = "world_pre_generator";
	
	@NotNull
	@Override
	public String getModId() {
		
		return MODID;
	}
	
	@Override
	protected void initMod() {
		
		ServerConfig serverConfig = registerConfig( ServerConfig::new );
		PregenWorker pregenWorker = new PregenWorker( serverConfig );
		WorldWorkerManager.addWorker( pregenWorker );
		registerEventHandler( new ModCommandsRegisterFactory( serverConfig, pregenWorker ) );
		registerEventHandler( new PregenerationWorldPersistencer( pregenWorker ) );
	}
}
