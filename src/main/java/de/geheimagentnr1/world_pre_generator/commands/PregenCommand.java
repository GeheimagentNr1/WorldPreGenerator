package de.geheimagentnr1.world_pre_generator.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.geheimagentnr1.world_pre_generator.generator.queue.TaskQueue;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PreGeneratorTask;
import de.geheimagentnr1.world_pre_generator.generator.tasks.PrintTask;
import de.geheimagentnr1.world_pre_generator.helpers.DimensionHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;


@SuppressWarnings( "SameReturnValue" )
public class PregenCommand {
	
	
	public static void register( CommandDispatcher<CommandSource> dispatcher ) {
		
		LiteralArgumentBuilder<CommandSource> pregenCommand = Commands.literal( "pregen" ).requires(
			source -> source.hasPermissionLevel( 2 ) );
		pregenCommand.then( Commands.literal( "list" ).executes( PregenCommand::printList ) );
		pregenCommand.then( Commands.literal( "start" ).then( Commands.argument( "center", Vec2Argument.vec2() )
			.then( Commands.argument( "radius", IntegerArgumentType.integer( 1 ) )
				.then( Commands.argument( "dimension", DimensionArgument.getDimension() )
					.executes( PregenCommand::start ) ) ) ) );
		pregenCommand.then( Commands.literal( "cancel" ).then( Commands.argument( "dimension",
			DimensionArgument.getDimension() ).executes( PregenCommand::cancel ) ) );
		pregenCommand.then( Commands.literal( "clear" ).executes( PregenCommand::clear ) );
		pregenCommand.then( Commands.literal( "sendFeedback" ).executes( PregenCommand::sendFeedback )
			.then( Commands.argument( "isFeedbackEnabled", BoolArgumentType.bool() )
				.executes( PregenCommand::changeSendFeedback ) ) );
		dispatcher.register( pregenCommand );
	}
	
	private static int printList( CommandContext<CommandSource> context ) {
		
		if( TaskQueue.isNotEmpty() ) {
			context.getSource().sendFeedback( new StringTextComponent( "World PreGen Tasklist:" ), false );
		} else {
			context.getSource().sendFeedback( new StringTextComponent( "World PreGen Tasklist is empty." ), false );
		}
		for( PreGeneratorTask task : TaskQueue.getTasks() ) {
			context.getSource().sendFeedback(
				new StringTextComponent( DimensionHelper.getNameOfDim( task.getDimension() ) )
					.appendString( " " ).appendString( String.valueOf( task.getCenterX() ) ).appendString( " " )
					.appendString( String.valueOf( task.getCenterZ() ) ).appendString( " " )
					.appendString( String.valueOf( task.getRadius() ) )
				, false );
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int start( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		Vector2f center = Vec2Argument.getVec2f( context, "center" );
		int radius = IntegerArgumentType.getInteger( context, "radius" );
		RegistryKey<World> dimension = DimensionArgument.getDimensionArgument( context, "dimension" ).getDimensionKey();
		
		TaskQueue.add( new PreGeneratorTask( context.getSource().getServer(), (int)center.x, (int)center.y, radius,
			dimension ) );
		context.getSource().sendFeedback( new StringTextComponent( "Task for " )
			.appendString( DimensionHelper.getNameOfDim( dimension ) ).appendString( " got queued." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int cancel( CommandContext<CommandSource> context ) throws CommandSyntaxException {
		
		RegistryKey<World> dimension = DimensionArgument.getDimensionArgument( context, "dimension" ).getDimensionKey();
		TaskQueue.cancelTask( dimension );
		context.getSource().sendFeedback( new StringTextComponent( "Task for " )
			.appendString( DimensionHelper.getNameOfDim( dimension ) ).appendString( " was canceled." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int clear( CommandContext<CommandSource> context ) {
		
		TaskQueue.clear();
		context.getSource().sendFeedback( new StringTextComponent( "All Task were canceled." ), true );
		return Command.SINGLE_SUCCESS;
	}
	
	private static int sendFeedback( CommandContext<CommandSource> context ) {
		
		CommandSource source = context.getSource();
		if( PrintTask.isFeedbackEnabled() ) {
			source.sendFeedback( new StringTextComponent( "Feedback is enabled." ), false );
		} else {
			source.sendFeedback( new StringTextComponent( "Feedback is disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
	
	private static int changeSendFeedback( CommandContext<CommandSource> context ) {
		
		boolean sendFeedback = BoolArgumentType.getBool( context, "isFeedbackEnabled" );
		CommandSource source = context.getSource();
		PrintTask.setIsFeedbackEnabled( sendFeedback );
		if( PrintTask.isFeedbackEnabled() ) {
			source.sendFeedback( new StringTextComponent( "Feedback is now enabled." ), false );
		} else {
			source.sendFeedback( new StringTextComponent( "Feedback is now disabled." ), false );
		}
		return Command.SINGLE_SUCCESS;
	}
}
