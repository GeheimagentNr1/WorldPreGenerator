package de.geheimagentnr1.world_pre_generator.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos.WorldPosArgument;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;


@SuppressWarnings( "SameReturnValue" )
public class PregenCommand {
	
	
	private static PregenWorker pregenWorker;
	
	public static void register( CommandDispatcher<CommandSource> dispatcher, PregenWorker _pregenWorker ) {
		
		LiteralArgumentBuilder<CommandSource> pregenCommand = Commands.literal( "pregen" )
			.requires( source -> source.hasPermission( 2 ) );
		pregenCommand.then( Commands.literal( "list" ).executes( PregenCommand::printList ) );
		pregenCommand.then( Commands.literal( "start" )
			.then( Commands.argument( "center", WorldPosArgument.worldPos() )
				.then( Commands.argument( "radius", IntegerArgumentType.integer( 1 ) )
					.then( Commands.argument( "dimension", DimensionArgument.dimension() )
						.executes( PregenCommand::start ) ) ) ) );
		pregenCommand.then( Commands.literal( "resume" )
			.then( Commands.argument( "dimension", DimensionArgument.dimension() )
				.executes( PregenCommand::resume ) ) );
		pregenCommand.then( Commands.literal( "pause" )
			.then( Commands.argument( "dimension", DimensionArgument.dimension() )
				.executes( PregenCommand::pause ) ) );
		pregenCommand.then( Commands.literal( "cancel" )
			.then( Commands.argument( "dimension", DimensionArgument.dimension() )
				.executes( PregenCommand::cancel ) ) );
		pregenCommand.then( Commands.literal( "clear" )
			.executes( PregenCommand::clear ) );
		pregenCommand.then( Commands.literal( "sendFeedback" )
			.executes( PregenCommand::showSendFeedback )
			.then( Commands.argument( "isFeedbackEnabled", BoolArgumentType.bool() )
				.executes( PregenCommand::setSendFeedback ) ) );
		dispatcher.register( pregenCommand );
		pregenWorker = _pregenWorker;
	}
	
	private static void printTasks( CommandSource source, ArrayList<PregenTask> tasks ) {
		
		for( PregenTask task : tasks ) {
			source.sendSuccess(
				new StringTextComponent( String.format(
					"%s %d %d %d",
					DimensionHelper.getNameOfDim( task.getDimension() ),
					task.getCenterX(),
					task.getCenterZ(),
					task.getRadius()
				) ),
				false
			);
		}
	}
	
	private static int printList( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		if( pregenWorker.getQueue().noTasks() ) {
			source.sendSuccess( new StringTextComponent( "Pregeneration Tasklist is empty." ), false );
		} else {
			source.sendSuccess( new StringTextComponent( "Pregeneration Tasklist:" ), false );
			ArrayList<PregenTask> activeTasks = pregenWorker.getQueue().getActiveTasks();
			ArrayList<PregenTask> pausedTasks = pregenWorker.getQueue().getPausedTasks();
			if( !activeTasks.isEmpty() ) {
				source.sendSuccess( new StringTextComponent( "Queued Tasks:" ), false );
				printTasks( source, activeTasks );
			}
			if( !pausedTasks.isEmpty() ) {
				source.sendSuccess( new StringTextComponent( "Paused Tasks:" ), false );
				printTasks( source, pausedTasks );
			}
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int start( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		CommandSource source = context.getSource();
		WorldPos center = WorldPosArgument.getWorldPos( context, "center" );
		int radius = IntegerArgumentType.getInteger( context, "radius" );
		RegistryKey<World> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		
		pregenWorker.getQueue().startTask( new PregenTask( center, radius, dimension ) );
		source.sendSuccess(
			new StringTextComponent( String.format(
				"Task for %s got queued.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int resume( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		CommandSource source = context.getSource();
		RegistryKey<World> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().resumeTask( dimension );
		source.sendSuccess(
			new StringTextComponent( String.format(
				"Task for %s was resumed.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int pause( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		CommandSource source = context.getSource();
		RegistryKey<World> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().pauseTask( dimension );
		source.sendSuccess(
			new StringTextComponent( String.format(
				"Task for %s was paused.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int cancel( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		CommandSource source = context.getSource();
		RegistryKey<World> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().cancelTask( dimension );
		source.sendSuccess(
			new StringTextComponent( String.format(
				"Task for %s was canceled.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int clear( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		pregenWorker.getQueue().clear();
		source.sendSuccess( new StringTextComponent( "All Task were canceled." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showSendFeedback( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		if( ServerConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( new StringTextComponent( "Feedback is enabled." ), false );
		} else {
			source.sendSuccess( new StringTextComponent( "Feedback is disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setSendFeedback( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		ServerConfig.setSendFeedback( BoolArgumentType.getBool( context, "isFeedbackEnabled" ) );
		if( ServerConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( new StringTextComponent( "Feedback is now enabled." ), false );
		} else {
			source.sendSuccess( new StringTextComponent( "Feedback is now disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
}
