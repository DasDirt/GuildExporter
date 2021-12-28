package dev.dirt.guildexporter;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        Option token = new Option("t", "token", true, "Bot Token");
        options.addOption(token);
        token.setRequired(true);
        Option userIds = new Option("id", true, "Ids of the users whitelisted (seperated by ,) to use the commands");
        userIds.setRequired(true);
        options.addOption(userIds);

        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("GuildExporter", options);

            System.exit(0);
        }

        if (commandLine != null) {
            String ids = commandLine.getOptionValue("id");
            try {
                long[] longIds;
                if (ids.contains(",")) { // if he have multiple ids we try to parse each one and create a long[] from it
                    String[] stringIds = ids.split(",");
                    longIds = new long[stringIds.length];
                    for (int i = 0; i < stringIds.length; i++) {
                        longIds[i] = Long.parseLong(stringIds[i]);
                    }
                } else {
                    longIds = new long[]{Long.parseLong(ids)}; // if we only got one id we just try to parse it directly
                }
                new GuildExporter(commandLine.getOptionValue("token"), longIds).startBot();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
