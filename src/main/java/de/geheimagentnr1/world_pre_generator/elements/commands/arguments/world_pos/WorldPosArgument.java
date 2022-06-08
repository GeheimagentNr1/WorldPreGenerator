package de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;


public class WorldPosArgument implements ArgumentType<Coordinates> {
	
	
	public static final String registry_name = "world_pos";
	
	private static final Collection<String> EXAMPLES = Arrays.asList( "0 0", "~ ~", "1 -5", "~1 ~-2" );
	
	private static final SimpleCommandExceptionType WORLD_POS_INCOMPLETE =
		new SimpleCommandExceptionType( Component.literal( "Incomplete (expected 2 coordinates)" ) );
	
	public static WorldPosArgument worldPos() {
		
		return new WorldPosArgument();
	}
	
	public static WorldPos getWorldPos( CommandContext<CommandSourceStack> context, String name ) {
		
		Vec3 vec3 = context.getArgument( name, Coordinates.class ).getPosition( context.getSource() );
		return new WorldPos( (int)vec3.x, (int)vec3.z );
	}
	
	public Coordinates parse( StringReader reader ) throws CommandSyntaxException {
		
		int cursor = reader.getCursor();
		if( reader.canRead() ) {
			WorldCoordinate x = WorldCoordinate.parseInt( reader );
			if( reader.canRead() && reader.peek() == ' ' ) {
				reader.skip();
				WorldCoordinate z = WorldCoordinate.parseInt( reader );
				return new WorldCoordinates( x, new WorldCoordinate( true, 0.0D ), z );
			} else {
				reader.setCursor( cursor );
				throw WORLD_POS_INCOMPLETE.createWithContext( reader );
			}
		} else {
			throw WORLD_POS_INCOMPLETE.createWithContext( reader );
		}
	}
	
	public <S> CompletableFuture<Suggestions> listSuggestions(
		CommandContext<S> context,
		SuggestionsBuilder builder ) {
		
		if( context.getSource() instanceof SharedSuggestionProvider ) {
			String remaining = builder.getRemaining();
			Collection<SharedSuggestionProvider.TextCoordinates> suggestions;
			if( !remaining.isEmpty() && remaining.charAt( 0 ) == '^' ) {
				suggestions = Collections.singleton( SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL );
			} else {
				suggestions = ( (SharedSuggestionProvider)context.getSource() ).getAbsoluteCoordinates();
			}
			return SharedSuggestionProvider.suggest2DCoordinates(
				remaining,
				suggestions,
				builder,
				Commands.createValidator( this::parse )
			);
		} else {
			return Suggestions.empty();
		}
	}
	
	public Collection<String> getExamples() {
		
		return EXAMPLES;
	}
}
