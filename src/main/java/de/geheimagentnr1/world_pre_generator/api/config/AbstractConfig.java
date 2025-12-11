package de.geheimagentnr1.world_pre_generator.api.config;

import de.geheimagentnr1.world_pre_generator.api.AbstractMod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public abstract class AbstractConfig {
	
	
	@NotNull
	private final AbstractMod abstractMod;
	
	@NotNull
	private final ModConfigSpec.Builder builder;
	
	@NotNull
	private final Map<String, ModConfigSpec.ConfigValue<?>> configValues;
	
	private ModConfigSpec spec;
	
	protected AbstractConfig( @NotNull AbstractMod abstractMod ) {
		
		this.abstractMod = abstractMod;
		this.builder = new ModConfigSpec.Builder();
		this.configValues = new HashMap<>();
	}
	
	@NotNull
	public abstract ModConfig.Type type();
	
	public abstract boolean isEarlyLoad();
	
	protected abstract void registerConfigValues();
	
	@NotNull
	public ModConfigSpec build() {
		
		registerConfigValues();
		spec = builder.build();
		return spec;
	}
	
	protected void push( @NotNull String comment, @NotNull String key ) {
		
		builder.comment( comment ).push( key );
	}
	
	protected void pop() {
		
		builder.pop();
	}
	
	protected void registerConfigValue( @NotNull String comment, @NotNull String key, boolean defaultValue ) {
		
		ModConfigSpec.BooleanValue value = builder.comment( comment ).define( key, defaultValue );
		configValues.put( key, value );
	}
	
	protected void registerConfigValue(
		@NotNull String comment,
		@NotNull List<String> path,
		@NotNull BiFunction<ModConfigSpec.Builder, String, ModConfigSpec.ConfigValue<?>> definer ) {
		
		String key = String.join( ".", path );
		ModConfigSpec.ConfigValue<?> value = definer.apply( builder.comment( comment ), key );
		configValues.put( key, value );
	}
	
	protected void registerConfigValue(
		@NotNull List<String> comments,
		@NotNull List<String> path,
		@NotNull BiFunction<ModConfigSpec.Builder, String, ModConfigSpec.ConfigValue<?>> definer ) {
		
		String key = String.join( ".", path );
		ModConfigSpec.ConfigValue<?> value = definer.apply(
			builder.comment( comments.toArray( new String[0] ) ),
			key
		);
		configValues.put( key, value );
	}
	
	@SuppressWarnings( "unchecked" )
	protected <T> T getValue( @NotNull Class<T> clazz, @NotNull String key ) {
		
		ModConfigSpec.ConfigValue<?> configValue = configValues.get( key );
		if( configValue == null ) {
			throw new IllegalArgumentException( "Config value not found: " + key );
		}
		return (T)configValue.get();
	}
	
	@SuppressWarnings( "unchecked" )
	protected <T> T getValue( @NotNull Class<T> clazz, @NotNull List<String> path ) {
		
		String key = String.join( ".", path );
		return getValue( clazz, key );
	}
	
	@SuppressWarnings( "unchecked" )
	protected <T> void setValue( @NotNull Class<T> clazz, @NotNull String key, @NotNull T value ) {
		
		ModConfigSpec.ConfigValue<T> configValue = (ModConfigSpec.ConfigValue<T>)configValues.get( key );
		if( configValue == null ) {
			throw new IllegalArgumentException( "Config value not found: " + key );
		}
		configValue.set( value );
	}
	
	@NotNull
	protected AbstractMod getAbstractMod() {
		
		return abstractMod;
	}
}
