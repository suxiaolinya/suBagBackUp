package suxiaolin.subagbackup;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;

public class CheckUpdata {
    public static void CheckUpdates(String version) {
        try {
            URL url = new URL("https://api.github.com/repos/suxiaolinya/checkupdatasuBagBackUp/releases/latest");
            Scanner scanner = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A");
            String json = scanner.hasNext()? scanner.next() : "";
            scanner.close();

            String latestVersion = json.contains("\"tag_name\":") ? json.split("\"tag_name\":")[1].split(",")[0].replace("\"", "").trim() : null;

            if (latestVersion != null && !latestVersion.equalsIgnoreCase(version)) {
                // 有新版本可用，发送提示消息
                Bukkit.getConsoleSender().sendMessage("[suBlockMonster]§4插件有新版本可用!v" + latestVersion);
            }else{
                Bukkit.getConsoleSender().sendMessage("[suBlockMonster]§2插件已是最新版本!");
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("[suBlockMonster]§4检查更新时发生错误!！" + e.getMessage());
        }
    }
}
