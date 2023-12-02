package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

public class Mysql {
    private Connection connection;
    private int Port;
    private String Host;
    private String Username;
    private String Password;
    private String Database;
    public void MysqlConnect(int port, String host, String database, String username, String password) {
        Host = host;
        Port = port;
        Database = database;
        Username = username;
        Password = password;
        connection = null;
    }
    public void LoadMysql() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + Host + ":" + Port + "/" + Database;
            DriverManager.getConnection(url, Username, Password);
            createTableIfNotExists();
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§a连接数据库成功!");
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4连接数据库失败:" + e.getMessage());
        }
    }
    public void connect() {
        try {
            String url = "jdbc:mysql://" + Host + ":" + Port + "/" + Database;
            this.connection = DriverManager.getConnection(url, Username, Password);
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4连接数据库失败:" + e.getMessage());
        }
    }

    public void createTableIfNotExists() {
        if (this.connection == null) {
            try {
                Statement statement = this.connection.createStatement();
                String createTableQuery = "CREATE TABLE IF NOT EXISTS bagbackup (player_name VARCHAR(255) NOT NULL,time VARCHAR(255) NOT NULL,item_data TEXT NOT NULL)";
                statement.executeUpdate(createTableQuery);
                statement.close();
                connection.close();
                connection = null;
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4创建数据表失败:" + e.getMessage());
            }
        }
    }

    public void SaveItems(String playerName, String time, String itemData, String sender) {
        connect();
        if (this.connection == null) {
            try {
                String data = "INSERT INTO bagbackup (player_name,time,item_data) VALUES (?,?,?)";
                PreparedStatement statement = this.connection.prepareStatement(data);
                statement.setString(1, playerName);
                statement.setString(2, time);
                statement.setString(3, itemData);
                statement.executeUpdate();
                statement.close();
                connection.close();
                connection = null;
                Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§2保存数据成功!");
            } catch (SQLException e) {
                Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4保存数据失败:" + e.getMessage());
            }
        }
    }

    public void ReadSaveItems(Player playerName, String time, String sender) {
        connect();
        PreparedStatement selectStatement = null;
        if (this.connection == null) {
            try {
                selectStatement = connection.prepareStatement("SELECT item_data FROM backups WHERE player_name = ? AND time = ?");
                selectStatement.setString(1, playerName.getName());
                selectStatement.setString(2, time);
                ResultSet resultSet = selectStatement.executeQuery();
                if (resultSet.next()) {
                    try{
                        String itemData = resultSet.getString("item_data");
                        Serialize.serialize.deserializePlayerInventory(playerName,itemData);
                        Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§2恢复备份成功!");
                    }catch (Exception e){
                        Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4恢复备份失败:" + e.getMessage());
                    }
                }
            } catch (SQLException e) {
                Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4恢复备份失败:" + e.getMessage());
            }finally {
                try {
                    if (selectStatement != null) {
                        selectStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                } catch (SQLException e) {
                    Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + e.getMessage());
                }
            }
        }
    }

    public void DeleteData(String playerName, String time, String sender) {
        connect();
        PreparedStatement selectStatement = null;
        if (this.connection == null) {
            try {
                selectStatement = connection.prepareStatement("DELETE FROM bagbackup WHERE player_name = ? AND time = ?");
                selectStatement.setString(1, playerName);
                selectStatement.setString(2, time);
                selectStatement.executeUpdate();
            }catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4删除数据失败:" + e.getMessage());
            }finally {
                try {
                    if (selectStatement != null) {
                        selectStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                        connection = null;
                    }
                } catch (SQLException e) {
                    Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + e.getMessage());
                }
            }
        }
    }

}
