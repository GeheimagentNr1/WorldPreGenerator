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
	private static final String GENERATION_KEY = "generation";
	
	@NotNull
	private static final List<String> GENERATION_TYPE_KEY = List.of( GENERATION_KEY, "type" );
	
	@NotNull
	private static final List<String> GENERATION_SEMI_PARALLEL_TASK_COUNT_KEY = List.of(
		GENERATION_KEY,
		"semi_parallel_task_count"
	);
	
	@NotNull
	private static final String DELAYS_KEY = "delays";
	
	@NotNull
	private static final List<String> PRINT_DELAY_KEY = List.of( DELAYS_KEY, "print" );
	
	@NotNull
	private static final List<String> SAVE_DELAY_KEY = List.of( DELAYS_KEY, "save" );
	
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
		
		registerConfigValue( "Shall a pre generation feedback send to all online players?", SEND_FEEDBACK_KEY, true );
		push( "Parameters for generation", GENERATION_KEY );
		registerConfigValue(
			List.of(
				"Type of generation",
				GenerationType.SERIAL + ": Every chunk one after another is generated.",
				GenerationType.SEMI_PARALLEL + ": Multiple chunk generation tasks are created, " +
					"but the generation is still serial (speed up the chunk generation)."
			),
			GENERATION_TYPE_KEY,
			( builder, path ) -> builder.defineEnum( path, GenerationType.SERIAL )
		);
		registerConfigValue(
			"How many chunk generation tasks shall be start in parallel? " +
				"If the value is \"0\", the number of processor cores is used.",
			GENERATION_SEMI_PARALLEL_TASK_COUNT_KEY,
			( builder, path ) -> builder.defineInRange( path, 0, 0, getProcessorCount() << 1 )
		);
		pop();
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
	
	@NotNull
	public GenerationType getGenerationType() {
		
		return getValue( GenerationType.class, GENERATION_TYPE_KEY );
	}
	
	public int getGenerationSemiParallelTaskCount() {
		
		int threadCount = getValue( Integer.class, GENERATION_SEMI_PARALLEL_TASK_COUNT_KEY );
		return threadCount == 0 ? getProcessorCount() : threadCount;
	}
	
	private int getProcessorCount() {
		
		return Runtime.getRuntime().availableProcessors();
	}
}
