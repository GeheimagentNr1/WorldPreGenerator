# AGENTS.md - World Pre Generator

## Projekt-Übersicht

**World Pre Generator** ist ein NeoForge Minecraft Mod für Minecraft 1.21.1.
- **Mod ID**: `world_pre_generator`
- **Package**: `de.geheimagentnr1.world_pre_generator`
- **Java Version**: 21
- **NeoForge Version**: 21.1.x

Bietet einen Pre-Generator für Minecraft-Welten.

## Abhängigkeiten

Keine Mod-Abhängigkeiten - eigenständiger Mod.

## Projektstruktur

```
src/main/java/de/geheimagentnr1/world_pre_generator/
├── WorldPreGenerator.java                   # Haupt-Mod-Klasse
├── api/
│   └── AbstractMod.java                     # Eigene AbstractMod Implementierung
├── config/
│   ├── GenerationType.java                  # Generierungs-Typen Enum
│   └── ServerConfig.java                    # Server-Konfiguration
├── helpers/
│   ├── DimensionHelper.java                 # Dimensions-Utilities
│   ├── JsonHelper.java                      # JSON-Utilities
│   └── SaveHelper.java                      # Speicher-Utilities
└── save/
    ├── PregenerationWorldPersistencer.java  # Welt-Persistenz
    └── Savable.java                         # Interface für speicherbare Objekte
```

## Besonderheiten

- **Server-Only**: DisplayTest ist `IGNORE_SERVER_VERSION` - primär für Server gedacht
- **Eigene AbstractMod**: Hat eine eigene `AbstractMod` Implementierung
- **Persistenz-System**: Speichert Generierungs-Fortschritt in der Welt

## Code-Stil

- **Annotations**: `@NotNull` aus `org.jetbrains.annotations`
- **Lombok**: Projekt nutzt Lombok
- **Formatierung**: Leerzeichen nach `(` und vor `)` bei Methodenaufrufen

## Build & Test

```bash
./gradlew build
./gradlew runClient
./gradlew runServer
```

## Deployment

- **CurseForge**: `./gradlew curseforge`
- **Modrinth**: `./gradlew modrinth`
