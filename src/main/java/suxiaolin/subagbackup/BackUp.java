package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BackUp {
    private String str;

    public void BackUpOne(Player player, String sender) {
        // 获取当前系统时间
        Date now = new Date();
        // 创建日期格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        // 将日期格式化为指定格式的字符串
        String time = sdf.format(now);
        str = new Serialize().serializePlayerInventory(player);
        if (SuBagBackup.plugin.getmysqluse()) {
            new Mysql().SaveItems(player.getName(), time, str, sender);
        }else{
            new H2().SaveItems(player.getName(), time, str, sender);
        }
        Config.config1.SavePlayerConfig(player.getName(), time, sender);
        str = "";
    }

    public void BackUpAll(String sender){
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 获取当前系统时间
            Date now = new Date();
            // 创建日期格式化对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            // 将日期格式化为指定格式的字符串
            String time = sdf.format(now);
            str = Serialize.serialize.serializePlayerInventory(player);
            if (SuBagBackup.plugin.getmysqluse()) {
                new Mysql().SaveItems(player.getName(), time, str, sender);
            }else{
                new H2().SaveItems(player.getName(), time, str, sender);
            }
            Config.config1.SavePlayerConfig(player.getName(), time, sender);
            str = "";
        }
    }
}
