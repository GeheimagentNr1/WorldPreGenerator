- Support for NeoForge 26.3.0.37 and newer: the server config is registered with the new config type (the jar for Minecraft 26.1 - 26.2 crashes there)
- An existing world config in `<world>/serverconfig` is copied once to `<world>/syncedconfig`, because NeoForge 26.3 only reads world configs from there (dedicated servers; in singleplayer move the file by hand)
Add compatibility for minecraft version 26.1 - 26.3
Fix sendFeedback set with /pregen not being saved to the config file (it was lost on server restart)
