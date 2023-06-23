package de.geheimagentnr1.world_pre_generator.elements.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.minecraft_forge_api.elements.commands.CommandInterface;
import de.geheimagentnr1.world_pre_generator.config.ServerConfig;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.PregenTask;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import de.geheimagentnr1.world_pre_generator.elements.workers.PregenWorker;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


@SuppressWarnings( "SameReturnValue" )
@RequiredArgsConstructor
public class PregenCommand implements CommandInterface {
	
	
	@NotNull
	private final ServerConfig serverConfig;
	
	@NotNull
	private final PregenWorker pregenWorker;
	
	@NotNull
	@Override
	public LiteralArgumentBuilder<CommandSourceStack> build() {
		
		LiteralArgumentBuilder<CommandSourceStack> pregenCommand = Commands.literal( "pregen" )
			.requires( source -> source.hasPermission( 2 ) );
		pregenCommand.then( Commands.literal( "clear" )
			.executes( this::clear ) );
		pregenCommand.then( Commands.literal( "gen" )
			.then( Commands.argument( "dimension", DimensionArgument.dimension() )
				.then( Commands.literal( "cancel" )
					.executes( this::cancel ) )
				.then( Commands.literal( "pause" )
					.executes( this::pause ) )
				.then( Commands.literal( "resume" )
					.executes( this::resume ) )
				.then( Commands.literal( "start" )
					.then( Commands.argument( "center", ColumnPosArgument.columnPos() )
						.then( Commands.argument( "radius", IntegerArgumentType.integer( 1 ) )
							.executes( this::startUnforced )
							.then( Commands.argument( "force", BoolArgumentType.bool() )
								.executes( this::start ) ) ) ) ) ) );
		pregenCommand.then( Commands.literal( "list" )
			.executes( this::printList ) );
		pregenCommand.then( Commands.literal( "sendFeedback" )
			.executes( this::showSendFeedback )
			.then( Commands.argument( "isFeedbackEnabled", BoolArgumentType.bool() )
				.executes( this::setSendFeedback ) ) );
		return pregenCommand;
	}
	
	private void printTasks( @NotNull CommandSourceStack source, @NotNull ArrayList<PregenTask> tasks ) {
		
		for( PregenTask task : tasks ) {
			source.sendSuccess(
				() -> Component.literal( String.format(
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
	
	private int printList( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		if( pregenWorker.getQueue().noTasks() ) {
			source.sendSuccess( () -> Component.literal( "Pregeneration Tasklist is empty." ), false );
		} else {
			source.sendSuccess( () -> Component.literal( "Pregeneration Tasklist:" ), false );
			ArrayList<PregenTask> activeTasks = pregenWorker.getQueue().getActiveTasks();
			ArrayList<PregenTask> pausedTasks = pregenWorker.getQueue().getPausedTasks();
			if( !activeTasks.isEmpty() ) {
				source.sendSuccess( () -> Component.literal( "Queued Tasks:" ), false );
				printTasks( source, activeTasks );
			}
			if( !pausedTasks.isEmpty() ) {
				source.sendSuccess( () -> Component.literal( "Paused Tasks:" ), false );
				printTasks( source, pausedTasks );
			}
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private int startUnforced( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		return start( context, false );
	}
	
	private int start( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		return start( context, BoolArgumentType.getBool( context, "force" ) );
	}
	
	private int start( @NotNull CommandContext<CommandSourceStack> context, boolean force )
		throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ColumnPos center = ColumnPosArgument.getColumnPos( context, "center" );
		int radius = IntegerArgumentType.getInteger( context, "radius" );
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		
		pregenWorker.getQueue().startTask( new PregenTask(
			new WorldPos( center.toChunkPos().x, center.toChunkPos().z ),
			radius,
			dimension,
			force
		) );
		source.sendSuccess(
			() -> Component.literal( String.format(
				"Task for %s got queued.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private int resume( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().resumeTask( dimension );
		source.sendSuccess(
			() -> Component.literal( String.format(
				"Task for %s was resumed.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private int pause( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().pauseTask( dimension );
		source.sendSuccess(
			() -> Component.literal( String.format(
				"Task for %s was paused.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private int cancel( @NotNull CommandContext<CommandSourceStack> context ) throws CommandSyntaxException {
		
		CommandSourceStack source = context.getSource();
		ResourceKey<Level> dimension = DimensionArgument.getDimension( context, "dimension" ).dimension();
		pregenWorker.getQueue().cancelTask( dimension );
		source.sendSuccess(
			() -> Component.literal( String.format(
				"Task for %s was canceled.",
				DimensionHelper.getNameOfDim( dimension )
			) ),
			true
		);
		return Command.SINGLE_SUCCESS;
	}
	
	private int clear( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		pregenWorker.getQueue().clear();
		source.sendSuccess( () -> Component.literal( "All Task were canceled." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private int showSendFeedback( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		if( serverConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( () -> Component.literal( "Feedback is enabled." ), false );
		} else {
			source.sendSuccess( () -> Component.literal( "Feedback is disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private int setSendFeedback( @NotNull CommandContext<CommandSourceStack> context ) {
		
		CommandSourceStack source = context.getSource();
		serverConfig.setSendFeedback( BoolArgumentType.getBool( context, "isFeedbackEnabled" ) );
		if( serverConfig.isSendFeedbackEnabled() ) {
			source.sendSuccess( () -> Component.literal( "Feedback is now enabled." ), false );
		} else {
			source.sendSuccess( () -> Component.literal( "Feedback is now disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
}
