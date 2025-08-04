package org.bruno.sonus.commands;

import org.bruno.sonus.data.SoundRegion;
import org.bruno.sonus.handlers.MessagesHandler;
import org.bruno.sonus.handlers.RegionHandler;
import org.bruno.sonus.handlers.SoundProfileHandler;
import org.bruno.sonus.helpers.MessagesHelper;
import org.bruno.sonus.helpers.PermissionsHelper;
import org.bruno.sonus.utils.SetSoundResult;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class SetCommand implements SubCommand {

    private final RegionHandler regionHandler;
    private final SoundProfileHandler soundProfileHandler;
    private final MessagesHelper messagesHelper;
    private final MessagesHandler messagesHandler;

    public SetCommand(RegionHandler regionHandler, SoundProfileHandler soundProfileHandler, MessagesHelper messagesHelper, MessagesHandler messagesHandler) {
        this.regionHandler = regionHandler;
        this.soundProfileHandler = soundProfileHandler;
        this.messagesHelper = messagesHelper;
        this.messagesHandler = messagesHandler;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!PermissionsHelper.hasSetPermission(sender)) { // Assumes a new sonus.command.set permission
            messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getNoPermission());
            return true;
        }

        if (args.length < 2) {
            messagesHelper.sendCommandSenderMessage(sender, "&cUsage: /sonus set <region_name> <sound_profile>");
            return true;
        }

        String regionName = args[0];
        String profileName = args[1];

        // Delegate the logic to the handler and process the result
        SetSoundResult result = soundProfileHandler.setRegionSound(regionName, profileName);

        switch (result) {
            case SUCCESS -> messagesHelper.sendCommandSenderMessage(sender, "&aSuccessfully bound sound profile '" + profileName + "' to region '" + regionName + "'.");
            case REGION_NOT_FOUND -> messagesHelper.sendCommandSenderMessage(sender, messagesHandler.getRegionNotFound().replace("{0}", regionName));
            case PROFILE_NOT_FOUND -> messagesHelper.sendCommandSenderMessage(sender, "&cError: Sound profile '" + profileName + "' not found in sounds.yml.");
        }

        return true;
    }

    @Override
    public List<String> getSubcommandCompletions(CommandSender sender, String[] args) {
        // Tab completion for the region name
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            return regionHandler.getAllRegions().stream()
                    .map(SoundRegion::name)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        // Tab completion for the sound profile name
        if (args.length == 3) {
            String input = args[2].toLowerCase();
            return soundProfileHandler.getSoundProfileKeys().stream() // Assumes a getSoundProfileKeys() method
                    .map(String::toLowerCase)
                    .filter(name -> name.startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}