package de.geheimagentnr1.world_pre_generator.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;


@SuppressWarnings( "SameReturnValue" )
public class PregenCommand {
	
	
	private static PregenWorker pregenWorker;
	
	public static void register( CommandDispatcher<CommandSourceStack> dispatcher, PregenWorker _pregenWorker ) {
		
		LiteralArgumentBuilder<CommandSourceStack> pregenCommand = Commands.literal( "pregen" )
			.requires( source -> source.hasPermission( 2 ) );
		pregenCommand.then( Commands.literal( "clear" )
			.executes( PregenCommand::clear ) );
		pregenCommand.then( Commands.literal( "gen" )
			.then( Commands.argument( "dimension", DimensionArgument.dimension() )
				.then( Commands.literal( "cancel" )
					.executes( PregenCommand::cancel ) )
				.then( Commands.literal( "pause" )
					.executes( PregenCommand::pause ) )
				.then( Commands.literal( "resume" )
					.executes( PregenCommand::resume ) )
				.then( Commands.literal( "start" )
					.then( Commands.argument( "center", ColumnPosArgument.columnPos() )
						.then( Commands.argument( "radius", IntegerArgumentType.integer( 1 ) )
							.executes( PregenCommand::startUnforced )
							.then( Commands.argument( "force", BoolArgumentType.bool() )
								.executes( PregenCommand::start ) ) ) ) ) ) );
		pregenCommand.then( Commands.literal( "list" )
			.executes( PregenCommand::printList ) );
		pregenCommand.then( Commands.literal( "sendFeedback" )
			.executes( PregenCommand::showSendFeedback )
			.then( Commands.argument( "isFeedbackEnabled", BoolArgumentType.bool() )
				.executes( PregenCommand::setSendFeedback ) ) );
		dispatcher.register( pregenCommand );
		pregenWorker = _pregenWorker;
	}
	
	private static void printTasks( CommandSourceStack source, ArrayList<PregenTask> tasks ) {
		
		for( PregenTask task : tasks ) {
			source.sendSuccess(
				new TextComponent( String.format(
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
	
	private static int printList( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		if( pregenWorker.getQueue().noTasks() ) {
			source.sendSuccess( new TextComponent( "Pregeneration Tasklist is empty." ), false );
		} else {
			source.sendSuccess( new TextComponent( "Pregeneration Tasklist:" ), false );
			ArrayList<PregenTask> activeTasks = pregenWorker.getQueue().getActiveTasks();
			ArrayList<PregenTask> pausedTasks = pregenWorker.getQueue().getPausedTasks();
			if( !activeTasks.isEmpty() ) {
				source.sendSuccess( new TextComponent( "Queued Tasks:" ), false );
				printTasks( source, activeTasks );
			}
			if( !pausedTasks.isEmpty() ) {
				source.sendSuccess( new TextComponent( "Paused Tasks:" ), false );
				printTasks( source, pausedTasks );
			}
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int startUnforced( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		return start( context, false );
	}
	
	private static int start( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		return start( context, BoolArgumentType.getBool( context, "force" ) );
	}
	
	private static int start( CommandContext<CommandSourceStack> context, boolean force )
		throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ColumnPos center = ColumnPosArgument.getColumnPos( context, "center" );
		int radius = IntegerArgumentType.getInteger( context, "radius" );
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		
		pregenWorker.getQueue().startTask( new PregenTask(
			new WorldPos( center.x, center.z ),
			radius,
			dimension,
			force
		) );
		source.sendSuccess(
			new TextComponent( String.format(
				"Task for %s got queued.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int resume( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().resumeTask( dimension );
		source.sendSuccess(
			new TextComponent( String.format(
				"Task for %s was resumed.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int pause( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().pauseTask( dimension );
		source.sendSuccess(
			new TextComponent( String.format(
				"Task for %s was paused.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int cancel( CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().cancelTask( dimension );
		source.sendSuccess(
			new TextComponent( String.format(
				"Task for %s was canceled.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int clear( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		pregenWorker.getQueue().clear();
		source.sendSuccess( new TextComponent( "All Task were canceled." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int showSendFeedback( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		if( ServerConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( new TextComponent( "Feedback is enabled." ), false );
		} else {
			source.sendSuccess( new TextComponent( "Feedback is disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setSendFeedback( CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		ServerConfig.setSendFeedback( BoolArgumentType.getBool( context, "isFeedbackEnabled" ) );
		if( ServerConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( new TextComponent( "Feedback is now enabled." ), false );
		} else {
			source.sendSuccess( new TextComponent( "Feedback is now disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
}
