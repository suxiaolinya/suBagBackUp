package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Config {
    private SuBagBackup plugin;
    public static Config config1;
    private FileConfiguration config;
    private int maxbackup;
    private File PlayerConfigFile;
    private YamlConfiguration PlayerConfig;
    private YamlConfiguration LanguageConfig;
    private Boolean mysqluse;
    private String language;
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
        language = config.getString("language");

        SaveLanguageConfig();
        LoadLanguageConfig();

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
                Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4" + LanguageConfig.getString("create_playerconfig_error"));
            }
        }
        // 加载配置文件
        PlayerConfig = YamlConfiguration.loadConfiguration(PlayerConfigFile);

    }

    public void SaveLanguageConfig(){
        String[] filelanguage = {"Language/en.yml", "Language/zh_CN.yml"};
        String folderPath = "plugins/suBagBackup/Language";
        File folder = new File(folderPath);
        if (!folder.exists()){
            folder.mkdir();
        }
        for (String s : filelanguage) {
            InputStream inputStream = plugin.getResource(s);
            File file = new File(plugin.getDataFolder(), s);
            if (!file.exists()) {
                try {
                    Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }catch (IOException e) {
                    Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4Create languag econfig error. | 创建语言文件失败." + e.getMessage());
                }
            }
        }
    }

    public void LoadLanguageConfig(){
        File LanguageConfigFile = new File(plugin.getDataFolder(), "Language/" + language + ".yml");
        LanguageConfig = YamlConfiguration.loadConfiguration(LanguageConfigFile);
    }

    public void SavePlayerConfig(String PlayerName,String time, String sender){

        if (PlayerConfig.getString(PlayerName) == null) {
            PlayerConfig.createSection(PlayerName);
            try {
                PlayerConfig.save(PlayerConfigFile);
            }catch (IOException e) {
                Bukkit.getPlayer(sender).sendMessage("[suBagBackUp]§4" + LanguageConfig.getString("create_playerdata_error:") + e.getMessage());
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
            Bukkit.getPlayer(sender).sendMessage("[suBagBackUp]§4" + LanguageConfig.getString("save_playerbackuptimelist_error"));
        }
        LoadPlayerConfig();
    }

    public void SavePlayerConfig(String PlayerName,String time){

        if (PlayerConfig.getString(PlayerName) == null) {
            PlayerConfig.createSection(PlayerName);
            try {
                PlayerConfig.save(PlayerConfigFile);
            }catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4" + LanguageConfig.getString("create_playerdata_error:") + e.getMessage());
            }
            LoadPlayerConfig();
        }
        List<String> time1 = PlayerConfig.getStringList(PlayerName);

        time1.add(time);
        if(time1.size() > maxbackup){
            String t = time1.get(0);
            time1.remove(0);
            if (mysqluse) {
                new Mysql().DeleteData(PlayerName, t);
            }else{
                new H2().DeleteData(PlayerName, t);
            }
        }
        PlayerConfig.set(PlayerName, time1);
        try {
            PlayerConfig.save(PlayerConfigFile);
        }catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackUp]§4" + LanguageConfig.getString("save_playerbackuptimelist_error"));
        }
        LoadPlayerConfig();
    }

    public void DeletePlayerConfig(String PlayerName, String time, String sender){
        List<String> time1 = PlayerConfig.getStringList(PlayerName);
        int index = 0;
        for (String a : time1){
            if (a.equals(time)){
                time1.remove(index);
                break;
            }
            index++;
        }
        PlayerConfig.set(PlayerName, time1);
        try {
            PlayerConfig.save(PlayerConfigFile);
        }catch (IOException e) {
            Bukkit.getPlayer(sender).sendMessage("[suBagBackUp]§4删除玩家数据列表失败." + LanguageConfig.getString("save_playerbackuptimelist_error"));
        }
        LoadPlayerConfig();
    }

    public Boolean getMysqluse() {
        return mysqluse;
    }
    public FileConfiguration getConfig() {
        return config;
    }
    public YamlConfiguration getLanguageConfig() {
        return LanguageConfig;
    }
    public YamlConfiguration getPlayerConfig() {
        return PlayerConfig;
    }
}
