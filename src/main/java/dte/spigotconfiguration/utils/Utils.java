package dte.spigotconfiguration.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Utils
{
	public static File loadDataFolder(Plugin plugin) 
	{
		File pluginFolder = plugin.getDataFolder();

		if(!pluginFolder.exists())
			pluginFolder.mkdirs();

		return pluginFolder;
	}
	
	public static String toInternalPath(String path) 
	{
		path = path.replace("/", File.separator);

		if(!path.endsWith(".yml"))
			path += ".yml";

		return path;
	}
	
	public static YamlConfiguration loadResourceConfig(Plugin plugin, File file) 
	{
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(file.getName())));

		//regenerate missing fields from the internal config
		config.setDefaults(internalConfig);
		config.options().copyDefaults(true);

		//save the config to reflect the changes 
		try
		{
			config.save(file);
		} 
		catch(IOException exception) 
		{
			exception.printStackTrace();
		}

		return config;
	}
}
