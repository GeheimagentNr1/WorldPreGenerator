package de.geheimagentnr1.world_pre_generator.elements.commands.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public record TaskRow(@NotNull String dimension,
                      @NotNull String type,
                      @Nullable String center,
                      @Nullable String centerX,
                      @Nullable String centerZ,
                      @NotNull String radius,
                      @NotNull String forced) {
	
	
}
