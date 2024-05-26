package suxiaolin.subagbackup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

public class H2 {

    public void LoadSQLite(){
        Connection connection = null;
        Statement statement = null;

        try {
            // 注册 SQLite 数据库驱动
            Class.forName("org.sqlite.JDBC");

            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");

            // 创建 Statement 对象
            statement = connection.createStatement();

            try {
                // 如果数据表不存在，则创建数据表
                String createTableSQL = "CREATE TABLE IF NOT EXISTS bagbackup (player_name VARCHAR(255) NOT NULL,time VERCHAR(255) NOT NULL,item_data TEXT NOT NULL)";
                statement.executeUpdate(createTableSQL);
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("datatablealready_exists"));
            }

        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("datatablealready_exists"));
        } finally {
            // 关闭连接和 Statement
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void SaveItems(String playerName, String time, String itemData, String sender){
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");

            String data = "INSERT INTO bagbackup (player_name,time,item_data) VALUES (?,?,?)";
            statement = connection.prepareStatement(data);
            statement.setString(1, playerName);
            statement.setString(2, time);
            statement.setString(3, itemData);

            // 执行 SQL 语句
            statement.executeUpdate();

            Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("backup_success"));
        }catch (SQLException e) {
            Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("backup_failed") + e.getMessage());
        }finally {
            // 关闭连接和 Statement
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("disconnect_database_error") + e.getMessage());
            }
        }
    }

    public void SaveItems(String playerName, String time, String itemData){
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");

            String data = "INSERT INTO bagbackup (player_name,time,item_data) VALUES (?,?,?)";
            statement = connection.prepareStatement(data);
            statement.setString(1, playerName);
            statement.setString(2, time);
            statement.setString(3, itemData);

            // 执行 SQL 语句
            statement.executeUpdate();

            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("backup_success"));
        }catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("backup_failed") + e.getMessage());
        }finally {
            // 关闭连接和 Statement
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("disconnect_database_error") + e.getMessage());
            }
        }
    }

    public void ReadSaveItems(Player playerName, String time, String sender) {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        try {
            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");
            selectStatement = connection.prepareStatement("SELECT item_data FROM bagbackup WHERE player_name = ? AND time = ?");
            selectStatement.setString(1, playerName.getName());
            selectStatement.setString(2, time);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                try{
                    String itemData = resultSet.getString("item_data");
                    Serialize.serialize.deserializePlayerInventory(playerName,itemData);
                    Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("restore_backup_success"));
                }catch (Exception e){
                    Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4:" + Config.config1.getLanguageConfig().getString("restore_backup_error") + e.getMessage());
                }finally {
                    try {
                        if (selectStatement != null) {
                            selectStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("restore_backup_error") + e.getMessage());
                    }
                }
            }
        }catch (SQLException e) {
            Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4" + e.getMessage());
        }
    }

    public void DeleteData(String playerName, String time, String sender) {
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        try {
            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");
            deleteStatement = connection.prepareStatement("DELETE FROM bagbackup WHERE player_name = ? AND time = ?");
            deleteStatement.setString(1, playerName);
            deleteStatement.setString(2, time);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getPlayer(sender).sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("delete_extra_backup_error") + e.getMessage());
        } finally {
            try {
                if (deleteStatement != null) {
                    deleteStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + e.getMessage());
            }
        }
    }

    public void DeleteData(String playerName, String time) {
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        try {
            // 连接到数据库（如果不存在则会自动创建）
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/suBagBackup/bagbackup.db");
            deleteStatement = connection.prepareStatement("DELETE FROM bagbackup WHERE player_name = ? AND time = ?");
            deleteStatement.setString(1, playerName);
            deleteStatement.setString(2, time);
            deleteStatement.executeUpdate();
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§2" + Config.config1.getLanguageConfig().getString("delete_backup_success"));
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + Config.config1.getLanguageConfig().getString("delete_extra_backup_error") + e.getMessage());
        } finally {
            try {
                if (deleteStatement != null) {
                    deleteStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage("[suBagBackup]§4" + e.getMessage());
            }
        }
    }

}

