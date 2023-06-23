package de.geheimagentnr1.world_pre_generator.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractConfig;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ServerConfig extends AbstractConfig {
	
	
	@NotNull
	private static final String SEND_FEEDBACK_KEY = "send_feedback";
	
	@NotNull
	private static final String DELAYS_KEY = "delays";
	
	@NotNull
	private static final List<String> PRINT_DELAY_KEY = List.of( DELAYS_KEY, "print" );
	
	@NotNull
	private static final List<String> SAVE_DELAY_KEY = List.of( DELAYS_KEY, "save" );
	
	@NotNull
	private static final String PARALLEL_KEY = "parallel";
	
	@NotNull
	private static final List<String> PARALLEL_ENABLED_KEY = List.of( PARALLEL_KEY, "enabled" );
	
	@NotNull
	private static final List<String> PARALLEL_COUNT_KEY = List.of( PARALLEL_KEY, "count" );
	
	public ServerConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@NotNull
	@Override
	public ModConfig.Type type() {
		
		return ModConfig.Type.SERVER;
	}
	
	@Override
	public boolean isEarlyLoad() {
		
		return false;
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( "Shall a pregeneration feedback send to all online players?", SEND_FEEDBACK_KEY, true );
		push( "Delays of the print and save tasks", DELAYS_KEY );
		registerConfigValue(
			"Time between 2 status prints",
			PRINT_DELAY_KEY,
			( builder, path ) -> builder.defineInRange( path, 1, 1, Integer.MAX_VALUE )
		);
		registerConfigValue(
			"Time between 2 saving events",
			SAVE_DELAY_KEY,
			( builder, path ) -> builder.defineInRange( path, 180, 1, Integer.MAX_VALUE )
		);
		pop();
		push( "Parameters for the parallel generation of several chunks", PARALLEL_KEY );
		registerConfigValue( "Should the chunks be generated in parallel?", PARALLEL_ENABLED_KEY, false );
		registerConfigValue(
			"How many chunk generation shall run in parallel? " +
				"If the value is \"0\", the number of processor cores is used.",
			PARALLEL_COUNT_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, getProcessorCount() << 1 )
		);
		pop();
	}
	
	public boolean isSendFeedbackEnabled() {
		
		return getValue( Boolean.class, SEND_FEEDBACK_KEY );
	}
	
	public void setSendFeedback( boolean sendFeedback ) {
		
		setValue( Boolean.class, SEND_FEEDBACK_KEY, sendFeedback );
	}
	
	public int getPrintDelay() {
		
		return getValue( Integer.class, PRINT_DELAY_KEY );
	}
	
	public int getSaveDelay() {
		
		return getValue( Integer.class, SAVE_DELAY_KEY );
	}
	
	public boolean isRunParallel() {
		
		return getValue( Boolean.class, PARALLEL_ENABLED_KEY );
	}
	
	public int getThreadCount() {
		
		int threadCount = getValue( Integer.class, PARALLEL_COUNT_KEY );
		return threadCount == 0 ? getProcessorCount() : threadCount;
	}
	
	private int getProcessorCount() {
		
		return Runtime.getRuntime().availableProcessors();
	}
}
