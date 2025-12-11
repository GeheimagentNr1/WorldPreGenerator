package de.geheimagentnr1.world_pre_generator.api;

import de.geheimagentnr1.world_pre_generator.api.config.AbstractConfig;
import de.geheimagentnr1.world_pre_generator.api.events.NeoForgeEventHandlerInterface;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


public abstract class AbstractMod {
	
	
	@NotNull
	private final ModContainer modContainer;
	
	@NotNull
	private final IEventBus modEventBus;
	
	protected AbstractMod( @NotNull ModContainer modContainer, @NotNull IEventBus modEventBus ) {
		
		this.modContainer = modContainer;
		this.modEventBus = modEventBus;
		initMod();
	}
	
	@NotNull
	public abstract String getModId();
	
	protected abstract void initMod();
	
	protected <T extends AbstractConfig> T registerConfig( @NotNull Function<AbstractMod, T> configFactory ) {
		
		T config = configFactory.apply( this );
		modContainer.registerConfig( config.type(), config.build() );
		return config;
	}
	
	protected void registerEventHandler( @NotNull NeoForgeEventHandlerInterface eventHandler ) {
		
		NeoForge.EVENT_BUS.register( eventHandler );
	}
	
	protected void registerModEventHandler( @NotNull Object eventHandler ) {
		
		modEventBus.register( eventHandler );
	}
	
	@NotNull
	public ModContainer getModContainer() {
		
		return modContainer;
	}
	
	@NotNull
	public IEventBus getModEventBus() {
		
		return modEventBus;
	}
}
