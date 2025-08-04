package org.bruno.sonus.utils;

public final class Constants {

    private Constants() {}

    public static final class Integrations {
        public static final int BSTATS_ID = 26769;
        public static final int SPIGOT_RESOURCE_ID = 126002; // TODO: Update value once the resource is created in SpigotMC
    }

    public static final class Files {
        public static final String REGIONS_FILE = "regions.yml";
        public static final String SOUNDS_FILE = "sounds.yml";
        public static final String MESSAGES_FILE = "messages.yml";
    }

    public static final class Permissions {
        private static final String BASE = "sonus.";

        // Wildcard Permissions
        public static final String ALL = BASE + "*";
        public static final String ALL_COMMANDS = BASE + "command.*";

        // Command Permissions
        public static final String CMD_HELP = BASE + "command.help";
        public static final String CMD_RELOAD = BASE + "command.reload";
        public static final String CMD_DEFINE = BASE + "command.define";
        public static final String CMD_DELETE = BASE + "command.delete";
        public static final String CMD_LIST = BASE + "command.list";
        public static final String CMD_WAND = BASE + "command.wand";
        public static final String CMD_SET = BASE + "command.set";

        public static final String NOTIFY_UPDATE = BASE + "update.notify";
    }
}