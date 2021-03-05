package de.geheimagentnr1.world_pre_generator.elements.commands.arguments.world_pos;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.geheimagentnr1.world_pre_generator.elements.queues.tasks.pregen.data.WorldPos;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.LocationPart;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;


public class WorldPosArgument implements ArgumentType<ILocationArgument> {
	
	
	public static final String registry_name = "world_pos";
	
	private static final Collection<String> EXAMPLES = Arrays.asList( "0 0", "~ ~", "1 -5", "~1 ~-2" );
	
	private static final SimpleCommandExceptionType WORLD_POS_INCOMPLETE =
		new SimpleCommandExceptionType( new StringTextComponent( "Incomplete (expected 2 coordinates)" ) );
	
	public static WorldPosArgument worldPos() {
		
		return new WorldPosArgument();
	}
	
	public static WorldPos getWorldPos( CommandContext<CommandSource> context, String name ) {
		
		Vector3d vector3d = context.getArgument( name, ILocationArgument.class ).getPosition( context.getSource() );
		return new WorldPos( (int)vector3d.x, (int)vector3d.z );
	}
	
	public ILocationArgument parse( StringReader reader ) throws CommandSyntaxException {
		
		int cursor = reader.getCursor();
		if( reader.canRead() ) {
			LocationPart x = LocationPart.parseInt( reader );
			if( reader.canRead() && reader.peek() == ' ' ) {
				reader.skip();
				LocationPart z = LocationPart.parseInt( reader );
				return new LocationInput( x, new LocationPart( true, 0.0D ), z );
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
		
		if( context.getSource() instanceof ISuggestionProvider ) {
			String remaining = builder.getRemaining();
			Collection<ISuggestionProvider.Coordinates> suggestions;
			if( !remaining.isEmpty() && remaining.charAt( 0 ) == '^' ) {
				suggestions = Collections.singleton( ISuggestionProvider.Coordinates.DEFAULT_LOCAL );
			} else {
				suggestions = ( (ISuggestionProvider)context.getSource() ).func_217293_r();
			}
			return ISuggestionProvider.func_211269_a(
				remaining,
				suggestions,
				builder,
				Commands.func_212590_a( this::parse )
			);
		} else {
			return Suggestions.empty();
		}
	}
	
	public Collection<String> getExamples() {
		
		return EXAMPLES;
	}
}
