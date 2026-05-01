package de.geheimagentnr1.world_pre_generator.elements.gametests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;


@GameTestHolder( "world_pre_generator" )
public class WorldPreGeneratorGameTests {

    @GameTest( templateNamespace = "neoforge", template = "floor_3x3x3" )
    public static void modLoadsSuccessfully( GameTestHelper helper ) {

        helper.succeed();
    }
}
