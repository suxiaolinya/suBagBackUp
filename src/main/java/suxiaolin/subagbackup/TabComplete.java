package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    private SuBagBackup plugin;
    public TabComplete(SuBagBackup plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("bbu").setTabCompleter(this);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("bbu") && args.length == 1) {
            List<String> playerlist = new ArrayList<>();
            playerlist.add("reload");
            playerlist.add("all");
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerlist.add(player.getName());
            }
            return playerlist;
        }else if (command.getName().equalsIgnoreCase("bbu") && args.length == 2) {
            List<String> timelist;
            timelist = Config.config1.getPlayerConfig().getStringList(args[0]);
            return timelist;
        }

        return null;
    }
}
