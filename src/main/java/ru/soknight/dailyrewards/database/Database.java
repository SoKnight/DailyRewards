package ru.soknight.dailyrewards.database;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ru.soknight.dailyrewards.DailyRewards;
import ru.soknight.dailyrewards.files.Config;
import ru.soknight.dailyrewards.utils.Logger;

public class Database {

	private String url;
	private String host;
	private String name;
	private String user;
	private String password;
	private String file;
	private boolean useMySQL;
	private int port;
	
	public Database() throws Exception {
		FileConfiguration config = Config.getConfig();
		useMySQL = config.getBoolean("database.use-mysql", false);
		if(useMySQL) {
			host = config.getString("database.host", "localhost");
			name = config.getString("database.name", "dailyrewards");
			user = config.getString("database.user", "admin");
			password = config.getString("database.password", "dailyrewards");
			port = config.getInt("database.port", 3306);
			url = "jdbc:mysql://" + host + ":" + port + "/" + name;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} else {
			file = config.getString("database.file", "dailyrewards.db");
			url = "jdbc:sqlite:" + DailyRewards.getInstance().getDataFolder() + File.separator + file;
			Class.forName("org.sqlite.JDBC").newInstance();
		}
		
		// Allowing only ORMLite errors logging
		System.setProperty("com.j256.ormlite.logger.type", "LOCAL");
		System.setProperty("com.j256.ormlite.logger.level", "ERROR");
				
		ConnectionSource source = getConnection();

		TableUtils.createTableIfNotExists(source, Profile.class);
		
		source.close();
		
		Logger.info("Database type " + (useMySQL ? "MySQL" : "SQLite") + " connected!");
	}
	
	public ConnectionSource getConnection() throws SQLException {
		return useMySQL ? new JdbcConnectionSource(url, user, password) : new JdbcConnectionSource(url);
	}
	
}
