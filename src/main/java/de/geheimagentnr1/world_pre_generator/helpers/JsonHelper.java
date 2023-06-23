package de.geheimagentnr1.world_pre_generator.helpers;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class JsonHelper {
	
	
	public static boolean isJsonArray( @Nullable JsonObject json, @NotNull String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonArray();
	}
	
	public static boolean isJsonObject( @Nullable JsonObject json, @NotNull String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonObject();
	}
	
	public static boolean isInt( @Nullable JsonObject json, @NotNull String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonPrimitive() &&
			json.getAsJsonPrimitive( key ).isNumber();
	}
	
	public static boolean isString( @Nullable JsonObject json, @NotNull String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonPrimitive() &&
			json.getAsJsonPrimitive( key ).isString();
	}
	
	public static int getInt( @NotNull JsonObject json, @NotNull String key ) {
		
		return json.getAsJsonPrimitive( key ).getAsInt();
	}
	
	public static String getString( @NotNull JsonObject json, @NotNull String key ) {
		
		return json.getAsJsonPrimitive( key ).getAsString();
	}
}
