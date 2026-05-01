package de.geheimagentnr1.world_pre_generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldPreGeneratorTest {

    @Test
    void modIdIsValid() {

        String modId = "world_pre_generator";
        assertTrue( modId.matches( "[a-z][a-z0-9_]{1,63}" ) );
    }
}
