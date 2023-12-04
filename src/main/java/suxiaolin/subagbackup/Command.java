package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class Command implements CommandExecutor {
    private SuBagBackup plugin;
    public Command(SuBagBackup plugin) {
        this.plugin = plugin;
        plugin.getCommand("bbu").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bbu") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.config1.reLoadConfigFile();
            sender.sendMessage("[suBagBackUp]§2" + Config.config1.getLanguageConfig().getString("reload_config"));
            return true;
        }
        if (command.getName().equalsIgnoreCase("bbu") && args.length > 0 && (Bukkit.getPlayer(args[0]) != null)) {
            if (args.length > 1) {
                for (String time : Config.config1.getPlayerConfig().getStringList(args[0])) {
                    if (time.equalsIgnoreCase(args[1])) {
                        if (Config.config1.getMysqluse()) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> new Mysql().ReadSaveItems(Bukkit.getPlayer(args[0]), args[1], sender.getName()));
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> new H2().ReadSaveItems(Bukkit.getPlayer(args[0]), args[1], sender.getName()));
                        }
                    }
                }
            }else{
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> new BackUp().BackUpOne(Bukkit.getPlayer(args[0]), sender.getName()));
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("bbu") && args.length > 0 && args[0].equalsIgnoreCase("all")) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> new BackUp().BackUpAll(sender.getName()));
            return true;
        }
        return false;
    }
}
