# CLAUDE.md - World Pre Generator

World Pre Generator generiert per Befehl `/pregen` Chunks um einen Mittelpunkt in einer Dimension vor (Warteschlange, Pause/Fortsetzen, Fortschrittsmeldungen, Fortschritt übersteht Neustarts). Mod-ID `world_pre_generator`, Package `de.geheimagentnr1.world_pre_generator`. **Serverseitig**: Clients brauchen den Mod nicht (keine Registrierungen, kein Netzwerk); es gibt keine Dist-Beschränkung (`@Mod( MODID )`), im Singleplayer läuft er im integrierten Server.

> Diese Datei ist auf allen `develop_*`-Branches identisch; Quelle: `../Docs/mod-claude/WorldPreGenerator.md` (Änderungen dort, dann `Docs/tools/sync_mod_claude_md.sh`). Versionsstand: [`../Docs/VERSIONEN.md`](../Docs/VERSIONEN.md).

## Versionen

- Branches, Ranges und Tags: Zeile „WorldPreGenerator“ in [`../Docs/VERSIONEN.md`](../Docs/VERSIONEN.md).
- NeoForge: `develop_1.21.1` (ein Jar 1.21.1 - 1.21.10), `develop_1.21.11`, `develop_26.1` (ein Jar 26.1 - 26.3). Forge: `develop_<mc>` bis 1.21.3 und `develop_1.21.1_forge`.
- Code-Unterschiede NeoForge: 1.21.11 `Identifier`, `Commands.hasPermission( Commands.LEVEL_GAMEMASTERS )` statt `hasPermission( 2 )`; 26.1 `ChunkPos.x()`/`z()` (Felder privat).

## Abhängigkeiten

- Keine Mod-Abhängigkeiten, keine gebündelten Bibliotheken. Forge ab 1.20: MinecraftForgeAPI (JarInJar, siehe [`../Docs/forge/README.md`](../Docs/forge/README.md)).

## Architektur

- `WorldPreGenerator` - Hauptklasse, erbt auf NeoForge vom eigenen `api/AbstractMod` (Config-, Event- und Befehlsregistrierung), auf Forge ab 1.20 von `AbstractMod` aus MinecraftForgeAPI; registriert `ServerConfig`, `PregenWorker`, `ModCommandsRegisterFactory` und `PregenerationWorldPersistencer`.
- `elements/workers/PregenWorker` - arbeitet pro `ServerTickEvent.Post` die aktuelle Aufgabe ab, meldet Start/Ende im Chat und steuert zwei Timer (Sekunden aus der Config): `PrinterSubTask` (Fortschritt) und `SaverSubTask` (speichert die Welt und ruft `System.gc()`). Forge-Branches: `WorldWorkerManager.IWorker` (teilt Tick-Zeit mit anderen Workern).
- `elements/queues/` - `PregenTaskQueue`/`PregenTaskList` (eine Aufgabe pro Dimension; aktuelle, wartende und pausierte Aufgaben), `PregenTask` (Quadrat um den Mittelpunkt, abgearbeitet Region für Region über `WorldPregenData`/`RegionPregenData`, generiert bis `ChunkStatus.FULL`; fertige Chunks werden übersprungen, außer mit `force`).
- `elements/commands/PregenCommand` - Brigadier-Befehl, `models/` für die Listen-Ausgabe.
- `save/PregenerationWorldPersistencer` - Warteschlange als JSON in `<welt>/world_pre_generator.dat`: laden bei `ServerStartingEvent`, schreiben nur bei `ServerStoppedEvent`.
- `config/ServerConfig` (Typ SERVER), `GenerationType` (`SERIAL`, `SEMI_PARALLEL`); `helpers/` für Dimensionen, JSON, Speichern.
- Alte Forge-Branches: bis 1.18 andere Befehlsstruktur (`/pregen start|pause|resume|cancel ..` ohne `gen <dimension>`, eigener Argument-Typ `WorldPosArgument`) und NBT-Speicherung; bis 1.19.4 statische `handlers/`-Klassen.

## Funktionen, Befehle, Konfiguration

- `/pregen` (Rechtestufe 2): `gen <dimension> start (block|chunk) <x z> <radius> [force]`, `gen <dimension> pause|resume|cancel`, `list`, `clear`, `sendFeedback [true|false]`. Bei `chunk` sind Mittelpunkt und Radius Chunk-Angaben, bei `block` Blockkoordinaten (Radius wird in Chunks umgerechnet).
- Server-Config: `send_feedback` (Fortschritt an alle Spieler), `generation.type` (`SERIAL`/`SEMI_PARALLEL`), `generation.semi_parallel_task_count` (0 = Anzahl CPU-Kerne), `delays.print` (Standard 1 s), `delays.save` (Standard 180 s).
- `SEMI_PARALLEL` erzeugt mehrere Generierungsaufträge, Minecraft generiert trotzdem auf dem Hauptthread (by design, GH#10).

## Fallstricke

- **Config speichern**: `AbstractConfig.setValue(..)` ruft nach `set(..)` `save()` auf - sonst geht `/pregen sendFeedback` beim Neustart verloren (bis 5.0.1).
- **Pregen-Zustand**: Laufende Aufgaben müssen einen Server-Neustart überstehen. Die Warteschlange wird nur beim regulären Stopp geschrieben (nach einem Absturz fehlt der Stand seit dem letzten Stopp; schon generierte Chunks werden beim Neustart der Aufgabe übersprungen). Jede Änderung an `PregenTask`/Queue-Serialisierung gegen eine vorhandene `world_pre_generator.dat` testen.
- 1.21.11+: keine GameTests mehr. Forge 1.20.1: Dedicated-Server-Absturz ab Forge 47.4.16 lag an MinecraftForgeAPI (behoben mit MFAPI 1.0.2); Abstürze großer Modpacks beim Pregen (GH#18) waren ein Vanilla-Watchdog, kein Mod-Fehler.

## Testen

- Rein serverseitig, daher vollständig per RCON testbar ([`../Docs/testing/tools/rcon.py`](../Docs/testing/tools/rcon.py), Server per `pack.sh`; Leitfaden [`../Docs/testing/README.md`](../Docs/testing/README.md)). Jar nur in den Server-Testpack.
- Ablauf: `pregen gen minecraft:overworld start chunk <x> <z> <radius>` in einem noch nicht generierten Bereich, `list`, `pause`, `resume`, `sendFeedback false/true` (Wert in der Config-Datei prüfen, übersteht Neustart), `cancel`; erneut starten, Server neu starten während die Aufgabe läuft (Fortschritt im Log muss weiterlaufen), `clear`.
- Forge 1.20.1: [`../Docs/testing/tools/forge_1.20.1_smoke.sh`](../Docs/testing/tools/forge_1.20.1_smoke.sh) prüft `/pregen` mit. CI: Build + JUnit.

## Links

- Wiki: https://github.com/GeheimagentNr1/WorldPreGenerator/wiki
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/world-pre-generator (Projekt-ID 388157)
- Modrinth: https://modrinth.com/mod/daLkM863
- [`../Docs/migrations/BREAKING_CHANGES.md`](../Docs/migrations/BREAKING_CHANGES.md), [`../Docs/releasing/release-process.md`](../Docs/releasing/release-process.md)
