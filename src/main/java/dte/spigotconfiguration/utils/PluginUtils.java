package dte.spigotconfiguration.utils;

import java.io.File;

import org.bukkit.plugin.Plugin;

public class PluginUtils
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
}
