package de.geheimagentnr1.world_pre_generator.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerConfig {
	
	
	private static final Logger LOGGER = LogManager.getLogger( ServerConfig.class );
	
	private static final String MOD_NAME = ModLoadingContext.get().getActiveContainer().getModInfo().getDisplayName();
	
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	
	public static final ForgeConfigSpec CONFIG;
	
	private static final ForgeConfigSpec.BooleanValue SEND_FEEDBACK;
	
	private static final ForgeConfigSpec.IntValue PRINT_DELAY;
	
	private static final ForgeConfigSpec.IntValue SAVE_DELAY;
	
	private static final ForgeConfigSpec.BooleanValue PARALLEL_ENABLED;
	
	private static final ForgeConfigSpec.IntValue PARALLEL_COUNT;
	
	static {
		
		SEND_FEEDBACK = BUILDER.comment( "Shall a pregeneration feedback send to all online players?" )
			.define( "send_feedback", true );
		BUILDER.comment( "Delays of the print and save tasks" )
			.push( "delays" );
		PRINT_DELAY = BUILDER.comment( "Time between 2 status prints" )
			.defineInRange( "print", 1, 1, Integer.MAX_VALUE );
		SAVE_DELAY = BUILDER.comment( "Time between 2 saving events" )
			.defineInRange( "save", 180, 1, Integer.MAX_VALUE );
		BUILDER.pop();
		BUILDER.comment( "Parameters for the parallel generation of several chunks" )
			.push( "parallel" );
		PARALLEL_ENABLED = BUILDER.comment( "Should the chunks be generated in parallel?" )
			.define( "enabled", false );
		PARALLEL_COUNT = BUILDER.comment( "How many chunk generation shall run in parallel? " +
				"If the value is \"0\", the number of processor cores is used." )
			.defineInRange( "count", 0, 0, getProcessorCount() << 1 );
		BUILDER.pop();
		
		CONFIG = BUILDER.build();
	}
	
	public static void printConfig() {
		
		LOGGER.info( "Loading \"{}\" Server Config", MOD_NAME );
		LOGGER.info( "{} = {}", SEND_FEEDBACK.getPath(), SEND_FEEDBACK.get() );
		LOGGER.info( "{} = {}", PRINT_DELAY.getPath(), PRINT_DELAY.get() );
		LOGGER.info( "{} = {}", SAVE_DELAY.getPath(), SAVE_DELAY.get() );
		LOGGER.info( "{} = {}", PARALLEL_ENABLED.getPath(), PARALLEL_ENABLED.get() );
		LOGGER.info( "{} = {}", PARALLEL_COUNT.getPath(), PARALLEL_COUNT.get() );
		LOGGER.info( "\"{}\" Server Config loaded", MOD_NAME );
	}
	
	public static boolean isSendFeedbackEnabled() {
		
		return SEND_FEEDBACK.get();
	}
	
	public static void setSendFeedback( boolean sendFeedback ) {
		
		SEND_FEEDBACK.set( sendFeedback );
	}
	
	public static int getPrintDelay() {
		
		return PRINT_DELAY.get();
	}
	
	public static int getSaveDelay() {
		
		return SAVE_DELAY.get();
	}
	
	public static boolean isRunParallel() {
		
		return PARALLEL_ENABLED.get();
	}
	
	public static int getThreadCount() {
		
		return PARALLEL_COUNT.get() == 0 ? getProcessorCount() : PARALLEL_COUNT.get();
	}
	
	private static int getProcessorCount() {
		
		return Runtime.getRuntime().availableProcessors();
	}
}
