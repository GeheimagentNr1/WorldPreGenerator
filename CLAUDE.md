# CLAUDE.md - World Pre Generator

## Projekt-Übersicht

**World Pre Generator** ist ein NeoForge Minecraft Mod.
- **Mod ID**: `world_pre_generator`
- **Package**: `de.geheimagentnr1.world_pre_generator`
- **Java Version**: 21 (`develop_26.1`: 25, `jdk-25.0.4.7-hotspot`)
- **NeoForge Version**: je Branch, siehe Tabelle

| Branch | MC | Range | NeoForge (kompiliert gegen) | Hinweis |
|---|---|---|---|---|
| `develop_1.21.1` | 1.21.1 - 1.21.10 | `[1.21.1,1.21.10]` | `21.1.216` | Release `1.21.1-5.0.2` (Config-`save()` für `/pregen sendFeedback`) |
| `develop_1.21.11` | 1.21.11 | `[1.21.11,1.21.12)` | `21.11.45` | `Identifier`/`IdentifierException`, `LEVEL_GAMEMASTERS`, GameTest entfernt |
| `develop_26.1` | 26.1 - 26.3 | `[26.1,27)` | `26.1.0.19-beta` (Java 25) | 26.x-Tooling, `ChunkPos.x()`/`z()` (Felder ab 26.1 privat) |

Alle 5.0.2, released 2026-10-02; `develop_1.21.3` ist ein alter Forge-Stand. Details: [`../Docs/migrations/1.21.10-to-1.21.11.md`](../Docs/migrations/1.21.10-to-1.21.11.md) 9, [`../Docs/migrations/1.21.11-to-26.1.md`](../Docs/migrations/1.21.11-to-26.1.md).

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

## Testing

### Java-Versionen

Verschiedene Java-Versionen sind unter `C:\Program Files\Eclipse Adoptium` installiert. Für einen Gradle-Build muss die passende Java-Version gewählt werden:

```powershell
# Java 21 für MC 1.20.5+ (NeoForge)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
./gradlew build
```

### Unit Tests (JUnit 5)

Für reine Logik-Tests ohne Minecraft-Abhängigkeiten:

```bash
./gradlew test
```

Tests liegen unter `src/test/java/`. Ergebnisse: `build/reports/tests/test/index.html`

### NeoForge GameTest Framework

Ab `develop_1.21.11` keine GameTests mehr (trivialer Smoke-Test samt Run-Config und CI-Job entfernt).

### Automatischer Test (RCON)

Rein serverseitig, daher vollständig per RCON testbar: `pregen gen minecraft:overworld start chunk <x> <z> <radius>` in einem noch nicht generierten Bereich, `list`, `pause`, `resume`, `sendFeedback false/true` (Wert muss in der Config-Datei stehen und den Neustart überstehen), `cancel`; erneut starten, Server neu starten während die Aufgabe läuft (Fortschritt im Log muss weiterlaufen), `clear`. Beim Typ `chunk` sind die Mittelpunkt-Koordinaten Chunk-Koordinaten.

### CI/CD (GitHub Actions)

Der Workflow `.github/workflows/build-and-test.yml` führt automatisch aus:
1. **Build**: Kompiliert den Mod
2. **Unit Tests**: Führt JUnit Tests aus

### Was kann automatisiert getestet werden?

| Aspekt | Automatisiert? | Methode |
|--------|----------------|---------|
| Utility-Klassen | ✅ | JUnit |
| Config-Parsing | ✅ | JUnit |
| Commands | ✅ | GameTest |
| Block/Item-Verhalten | ✅ | GameTest |
| Multi-MC-Version | ⚠️ Pro Branch | CI Matrix |

## Referenzen

- [NeoForge Migration Primer](https://docs.neoforged.net/primer/docs/) — Dokumentiert API-Aenderungen zwischen Minecraft/NeoForge-Versionen; nuetzlich fuer die Pruefung von Breaking Changes beim Upgrade auf neue Versionen

---

## Wissensdatenbank

Versionsübergreifende Migrations- und Entwicklungs-Erkenntnisse (Breaking Changes, Fixes, Testumgebungs-Patterns) werden zentral in [`../Docs/`](../Docs/) gepflegt. Bei neuen relevanten Erkenntnissen dort ergänzen, nicht nur hier.
