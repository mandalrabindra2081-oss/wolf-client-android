package com.wolfclient.app

data class Module(
    val id: String,
    val name: String,
    val description: String,
    val category: Category,
    var enabled: Boolean = false
)

enum class Category(val displayName: String) {
    ALL("All"),
    VISUAL("Visual"),
    HUD("HUD"),
    UTILITY("Utility"),
    MOVEMENT("Movement")
}

object ModuleRegistry {

    val modules = mutableListOf(
        // HUD
        Module("fpsdisplay",       "FPS Display",        "Shows your frames per second on screen.",                 Category.HUD),
        Module("cpsdisplay",       "CPS Display",        "Displays your clicks per second.",                        Category.HUD),
        Module("coordinatesdisplay","Coordinates",       "View the player's coordinates.",                          Category.HUD),
        Module("pingdisplay",      "Ping Display",       "Shows your ping when connected to a server.",             Category.HUD),
        Module("speeddisplay",     "Speed Display",      "Shows your current movement speed.",                      Category.HUD),
        Module("armorhud",         "Armor HUD",          "Shows your armor and held item in the HUD.",              Category.HUD),
        Module("keystrokes",       "Keystrokes",         "Displays movement keys and states.",                      Category.HUD),
        Module("reachdisplay",     "Reach Display",      "Displays your reach when you attack an entity.",          Category.HUD),
        Module("serverdisplay",    "Server Display",     "Displays the server's name on screen.",                   Category.HUD),

        // Visual
        Module("fullbright",       "Fullbright",         "Makes the world fully bright.",                           Category.VISUAL),
        Module("hitboxes",         "Hitboxes",           "Reveals entities' bounding boxes.",                       Category.VISUAL),
        Module("chunkborders",     "Chunk Borders",      "Shows borders between chunks.",                           Category.VISUAL),
        Module("zoom",             "Zoom",               "Makes the camera zoom into the distance.",                Category.VISUAL),
        Module("blockoverlay",     "Block Overlay",      "Changes the appearance of the block selection box.",      Category.VISUAL),
        Module("motionblur",       "Motion Blur",        "Creates a motion blur effect on screen.",                 Category.VISUAL),
        Module("hurtcam",          "Hurt Cam",           "Disables camera shake when hurt.",                        Category.VISUAL),
        Module("crosshair",        "Crosshair",          "Options related to your Minecraft crosshair.",            Category.VISUAL),
        Module("cinematiccamera",  "Cinematic Camera",   "Makes your camera smooth and cinematic.",                 Category.VISUAL),
        Module("environment",      "Environment Changer","Edit visuals related to environment and weather.",         Category.VISUAL),
        Module("freelook",         "Freelook",           "Look around without moving your character.",              Category.VISUAL),
        Module("viewmodel",        "View Model",         "Customize the item-in-hand view.",                        Category.VISUAL),

        // Utility
        Module("autosprint",       "Auto Sprint",        "Automatically sprint when walking.",                      Category.UTILITY),
        Module("quickdrop",        "Quick Drop",         "Quickly drop an item by pressing a button.",              Category.UTILITY),
        Module("quickperspective", "Quick Perspective",  "Change perspective quickly on touchscreen.",              Category.UTILITY),
        Module("commandshortcuts", "Command Shortcuts",  "Shortcuts for commands like /gms, /gmc, etc.",            Category.UTILITY),
        Module("pausegame",        "Pause Game",         "Pause singleplayer like Java Edition.",                   Category.UTILITY),
        Module("waypoints",        "Waypoints",          "Set and view markers anywhere in the world.",             Category.UTILITY),
        Module("tnttimer",         "TNT Timer",          "Shows a timer above TNT when it is lit.",                 Category.UTILITY),
        Module("audiosubtitles",   "Audio Subtitles",    "Shows the names and direction of sounds being played.",   Category.UTILITY),
        Module("nametag",          "Name Tag",           "Displays your name tag in third person.",                 Category.UTILITY),

        // Movement
        Module("renderoptions",    "Render Options",     "Change render options for performance.",                  Category.MOVEMENT)
    )

    fun save(context: android.content.Context) {
        val prefs = context.getSharedPreferences("wolf_modules", android.content.Context.MODE_PRIVATE)
        val ed = prefs.edit()
        modules.forEach { ed.putBoolean(it.id, it.enabled) }
        ed.apply()
    }

    fun load(context: android.content.Context) {
        val prefs = context.getSharedPreferences("wolf_modules", android.content.Context.MODE_PRIVATE)
        modules.forEach { it.enabled = prefs.getBoolean(it.id, false) }
    }
}
