package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {
    private SuBagBackup plugin;
    public static Config config1;
    private FileConfiguration config;
    private File PlayerConfigFile;
    private int maxbackup;
    private YamlConfiguration PlayerConfig;
    private Boolean mysqluse;
    public Config(SuBagBackup plugin) {

        this.plugin = plugin;
        config = this.plugin.getConfig();
        config1 = this;
        LoadConfigFile();
        LoadPlayerConfig();
    }

    public void LoadConfigFile() {
        int port = config.getInt("MYSQl.port");
        String host = config.getString("MYSQL.host");
        String username = config.getString("MYSQL.username");
        String password = config.getString("MYSQL.password");
        String database = config.getString("MYSQL.database");
        maxbackup = config.getInt("maxbackup");
        mysqluse = config.getBoolean("MYSQL.use");

        if (mysqluse) {
            new Mysql().MysqlConnect(port, host, database, username, password);
            new Mysql().LoadMysql();
        }else{
            new H2().LoadSQLite();
        }
    }

    public void reLoadConfigFile() {
        this.plugin.reloadConfig();
        config = this.plugin.getConfig();
        LoadConfigFile();
    }

    public void LoadPlayerConfig(){

        // 指定配置文件路径
        PlayerConfigFile = new File(plugin.getDataFolder(), "player.yml");
        if (!PlayerConfigFile.exists()) {
            try {
                PlayerConfigFile.createNewFile();
            }catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4创建配置文件player.yml失败");
            }
        }
        // 加载配置文件
        PlayerConfig = YamlConfiguration.loadConfiguration(PlayerConfigFile);

    }

    public void SavePlayerConfig(String PlayerName,String time, String sender){

        if (PlayerConfig.getString(PlayerName) == null) {
            PlayerConfig.createSection(PlayerName);
            try {
                PlayerConfig.save(PlayerConfigFile);
            }catch (IOException e) {
                Bukkit.getPlayer(sender).sendMessage("[suBagBackUp]§4创建玩家数据失败" + e.getMessage());
            }
            LoadPlayerConfig();
        }
        List<String> time1 = PlayerConfig.getStringList(PlayerName);

        time1.add(time);
        if(time1.size() > maxbackup){
            String t = time1.get(0);
            time1.remove(0);
            if (mysqluse) {
                new Mysql().DeleteData(PlayerName, t, sender);
            }else{
                new H2().DeleteData(PlayerName, t, sender);
            }
        }
        PlayerConfig.set(PlayerName, time1);
        try {
            PlayerConfig.save(PlayerConfigFile);
        }catch (IOException e) {
            Bukkit.getPlayer(sender).sendMessage("[suBagBackUp]§4玩家备份时间列表保存失败");
        }
        LoadPlayerConfig();
    }


    public Boolean getMysqluse() {
        return mysqluse;
    }
    public FileConfiguration getConfig() {
        return config;
    }

    public YamlConfiguration getPlayerConfig() {
        return PlayerConfig;
    }
}
