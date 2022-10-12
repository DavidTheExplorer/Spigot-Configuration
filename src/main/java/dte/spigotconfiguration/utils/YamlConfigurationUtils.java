package dte.spigotconfiguration.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class YamlConfigurationUtils
{
	public static File loadDataFolder(Plugin plugin) 
	{
		File pluginFolder = plugin.getDataFolder();

		if(!pluginFolder.exists())
			pluginFolder.mkdirs();

		return pluginFolder;
	}
	
	public static String toResourcePath(String path) 
	{
		path = path.replace("/", File.separator);

		if(!path.endsWith(".yml"))
			path += ".yml";

		return path;
	}
	
	public static YamlConfiguration loadConfiguration(Plugin plugin, File file, boolean resource) throws IOException 
	{
		if(resource)
		{
			if(file.exists())
				return YamlConfigurationUtils.loadResourceConfig(plugin, file);
			
			plugin.saveResource(getInternalPath(plugin, file), false);

			return YamlConfiguration.loadConfiguration(file);
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		return YamlConfiguration.loadConfiguration(file);
	}
	
	public static YamlConfiguration loadResourceConfig(Plugin plugin, File file) throws IOException
	{
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		//regenerate missing fields from the internal config
		YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(getInternalPath(plugin, file))));
		config.setDefaults(internalConfig);
		config.options().copyDefaults(true);

		//save the config to reflect the changes
		config.save(file);
		
		return config;
	}
	
	public static String getInternalPath(Plugin plugin, File file) 
	{
		String pluginName = plugin.getName();
		String path = file.getPath();
		
		return !path.contains(pluginName) ? path : path.substring(path.indexOf(pluginName) + pluginName.length() +1).replace(File.separator, "/");
	}
}
