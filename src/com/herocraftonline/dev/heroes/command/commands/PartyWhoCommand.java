package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;

public class PartyWhoCommand extends BaseCommand {

    public PartyWhoCommand(Heroes plugin) {
        super(plugin);
        name = "PartyWho";
        description = "Check your party members";
        usage = "/party who";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("party who");
        identifiers.add("party");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Hero hero = plugin.getHeroManager().getHero(player);
            if(hero.getParty() == null) {
                Messaging.send(player, "Sorry, you aren't in a party", (String[]) null);
                return;
            }
            HeroParty heroParty = hero.getParty();
            Messaging.send(player, "$1", heroParty.getMembers().toString());
        }
    }

}
