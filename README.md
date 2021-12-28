# GuildExporter
GuildExporter can be used to export a Discord guild as .json file and to import recreate a Discord guild from a previous generated .json file

At the moment channels, roles, categories, permissions, system channel, afk channel & afk timeout are saved.

[The bot in action](https://youtu.be/MMdTRbKCkCk)

## Usage
GuildExporter is written in Java 8
1. Create an Application on [Discords Developer Portal](https://discord.com/developers/applications)
2. Go to the Bot tab and click on Add Bot.
3. Click on the Copy button under the token section to copy your bot token
4. Start terminal in the same folder of the downloaded Jar file and type `java -jar GuildExporter -t TOKEN -id ID,ID` also don't forget replace TOKEN with your bot token and ID,ID with the discord ids that should be whitelisted for the commands

The Bot will now start and give you the domain through which the bot can be added to your Discord guild
After the bot appears on your server you can use the bots slash commands `/export filename` and `/restore filename` to start the desired action

## Note: This bot does not work asynchronously and is not suitable for simultaneous use on multiple guilds!