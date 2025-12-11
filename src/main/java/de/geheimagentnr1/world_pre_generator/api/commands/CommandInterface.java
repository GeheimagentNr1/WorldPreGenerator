package de.geheimagentnr1.world_pre_generator.api.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;


public interface CommandInterface {
	
	
	@NotNull
	LiteralArgumentBuilder<CommandSourceStack> build();
}
