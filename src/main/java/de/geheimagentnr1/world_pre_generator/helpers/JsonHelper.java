package de.geheimagentnr1.world_pre_generator.helpers;

import com.google.gson.JsonObject;


public class JsonHelper {
	
	
	public static boolean isJsonArray( JsonObject json, String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonArray();
	}
	
	public static boolean isJsonObject( JsonObject json, String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonObject();
	}
	
	public static boolean isInt( JsonObject json, String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonPrimitive() &&
			json.getAsJsonPrimitive( key ).isNumber();
	}
	
	public static boolean isString( JsonObject json, String key ) {
		
		return json != null && json.has( key ) && json.get( key ).isJsonPrimitive() &&
			json.getAsJsonPrimitive( key ).isString();
	}
	
	public static int getInt( JsonObject json, String key ) {
		
		return json.getAsJsonPrimitive( key ).getAsInt();
	}
	
	public static String getString( JsonObject json, String key ) {
		
		return json.getAsJsonPrimitive( key ).getAsString();
	}
}
