package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuBagBackup extends JavaPlugin {
    public static SuBagBackup plugin;
    private Boolean mysqluse;
    private Config config;
    private final String pluginversion = getDescription().getVersion();
    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        config = new Config(this);
        mysqluse = config.getMysqluse();
        int backuptime = getConfig().getInt("backuptime");
        new Command(this);
        new Serialize();
        new TabComplete(this);

        new Metrics(this, 20427);

        Bukkit.getConsoleSender().sendMessage("[SuBagBackup]§2" + Config.config1.getLanguageConfig().getString("onenable"));

        if (getConfig().getBoolean("checkupdata")) {
            CheckUpdata.CheckUpdates(pluginversion);
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> new BackUp().BackUpAll(Bukkit.getConsoleSender().getName()), 0, backuptime*20*60*60);
    }

    public Boolean getmysqluse(){
        return mysqluse;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4" + Config.config1.getLanguageConfig().getString("ondisable"));
    }

}
