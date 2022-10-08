package dte.spigotconfiguration;

import static dte.spigotconfiguration.utils.Utils.loadDataFolder;
import static dte.spigotconfiguration.utils.Utils.loadResourceConfig;
import static dte.spigotconfiguration.utils.Utils.toInternalPath;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class SpigotConfig
{
	private final File file;
	private final YamlConfiguration config;
	
	private SpigotConfig(File file, YamlConfiguration config)
	{
		this.file = file;
		this.config = config;
	}

	public static SpigotConfig byPath(Plugin plugin, String path) throws ConfigLoadException
	{
		return byPath(plugin, path, false);
	}

	public static SpigotConfig fromInternalResource(Plugin plugin, String resourceName) throws ConfigLoadException 
	{
		return byPath(plugin, resourceName, true);
	}

	private static SpigotConfig byPath(Plugin plugin, String path, boolean resource) throws ConfigLoadException
	{
		try 
		{
			File file = new File(loadDataFolder(plugin), toInternalPath(path));
			YamlConfiguration config = getConfiguration(plugin, file, resource);

			return new SpigotConfig(file, config);
		}
		catch(Exception exception) 
		{
			throw new ConfigLoadException(path, resource);
		}
	}

	public YamlConfiguration getStorage() 
	{
		return this.config;
	}

	public File getFile() 
	{
		return this.file;
	}

	public void set(String path, Object object) 
	{
		this.config.set(path, object);
	}

	public void delete(String path) 
	{
		this.config.set(path, null);
	}

	public void clear()
	{
		this.config.getKeys(false).forEach(this::delete);
	}

	public void save() throws IOException
	{
		this.config.save(this.file);
	}

	public <T> List<T> getList(String path, Class<T> typeClass)
	{
		return this.config.getList(path, new ArrayList<>()).stream()
				.map(typeClass::cast)
				.collect(toList());
	}

	public ConfigurationSection getSection(String path) 
	{
		ConfigurationSection section = this.config.getConfigurationSection(path);

		return section != null ? section : this.config.createSection(path);
	}



	private static YamlConfiguration getConfiguration(Plugin plugin, File file, boolean resource) throws IOException 
	{
		if(resource) 
		{
			if(file.exists())
			{
				return loadResourceConfig(plugin, file);
			}
			else
			{
				plugin.saveResource(file.getName(), false);
				
				return YamlConfiguration.loadConfiguration(file);
			}
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		return YamlConfiguration.loadConfiguration(file);
	}



	/*
	 * Delegation of common methods to YamlConfiguration
	 */
	public Object get(String path) 
	{
		return this.config.get(path);
	}
	
	public String getString(String path)
	{
		return this.config.getString(path);
	}

	public int getInt(String path)
	{
		return this.config.getInt(path);
	}

	public double getDouble(String path) 
	{
		return this.config.getDouble(path);
	}

	public boolean getBoolean(String path)
	{
		return this.config.getBoolean(path);
	}

	public ItemStack getItemStack(String path) 
	{
		return this.config.getItemStack(path);
	}

	public Set<String> getKeys(boolean deep) 
	{
		return this.config.getKeys(deep);
	}

	public Map<String, Object> getValues(boolean deep) 
	{
		return this.config.getValues(deep);
	}
}