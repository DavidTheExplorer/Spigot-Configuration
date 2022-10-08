package dte.spigotconfiguration;

import static dte.spigotconfiguration.utils.Utils.toInternalPath;
import static dte.spigotconfiguration.utils.Utils.loadDataFolder;
import static dte.spigotconfiguration.utils.Utils.loadResourceConfig;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class SpigotConfig
{
	private final File file;
	private final YamlConfiguration config;
	
	protected SpigotConfig(File file, YamlConfiguration config) 
	{
		this.file = file;
		this.config = config;
	}

	public static SpigotConfig byPath(Plugin plugin, String path) throws IOException 
	{
		return byPath(plugin, path, false);
	}

	public static SpigotConfig from(Plugin plugin, String resourceName) throws IOException 
	{
		return byPath(plugin, resourceName, true);
	}

	private static SpigotConfig byPath(Plugin plugin, String path, boolean resource) throws IOException
	{
		File file = new File(loadDataFolder(plugin), toInternalPath(path));
		file.getParentFile().mkdirs();
		file.createNewFile();
		
		YamlConfiguration config = getConfiguration(plugin, file, resource);
		
		return new SpigotConfig(file, config);
	}

	public YamlConfiguration getConfig() 
	{
		return this.config;
	}

	public File getFile() 
	{
		return this.file;
	}

	public <T> List<T> getList(String path, Class<T> type)
	{
		return this.config.getList(path, new ArrayList<>()).stream()
				.map(type::cast)
				.collect(toList());
	}

	public ConfigurationSection getSection(String path) 
	{
		ConfigurationSection section = this.config.getConfigurationSection(path);

		return section != null ? section : this.config.createSection(path);
	}

	public boolean exists() 
	{
		return this.file.exists();
	}

	public void save() throws IOException
	{
		this.config.save(this.file);
	}

	public void delete(String path) 
	{
		this.config.set(path, null);
	}

	public void clear() throws IOException
	{
		this.config.getKeys(false).forEach(this::delete);
		save();
	}

	private static YamlConfiguration getConfiguration(Plugin plugin, File file, boolean resource) 
	{
		if(!resource)
			return YamlConfiguration.loadConfiguration(file);
		
		if(file.exists())
			return loadResourceConfig(plugin, file);

		plugin.saveResource(file.getPath(), false);
		
		return YamlConfiguration.loadConfiguration(file);
	}
}