package dte.spigotconfiguration;

import static dte.spigotconfiguration.utils.YamlConfigurationUtils.loadConfiguration;
import static dte.spigotconfiguration.utils.YamlConfigurationUtils.loadDataFolder;
import static dte.spigotconfiguration.utils.YamlConfigurationUtils.toResourcePath;
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
	
	protected SpigotConfig(Builder builder)
	{
		this.file = builder.file;
		this.config = builder.config;
	}

	public static SpigotConfig byPath(Plugin plugin, String path) throws ConfigLoadException
	{
		return new Builder(plugin)
				.byPath(path)
				.build();
	}
	
	public static SpigotConfig fromInternalResource(Plugin plugin, String resourceName) throws ConfigLoadException
	{
		return new Builder(plugin)
				.fromInternalResource(resourceName)
				.build();
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

	
	
	public static class Builder
	{
		private final Plugin plugin;
		private File file;
		private YamlConfiguration config;
		
		public Builder(Plugin plugin) 
		{
			this.plugin = plugin;
		}

		public Builder byPath(String path) throws ConfigLoadException
		{
			return byPath(path, false);
		}

		public Builder fromInternalResource(String resourceName) throws ConfigLoadException
		{
			return byPath(resourceName, true);
		}
		
		private Builder byPath(String path, boolean resource) throws ConfigLoadException
		{
			try 
			{
				this.file = new File(loadDataFolder(this.plugin), toResourcePath(path));
				this.config = loadConfiguration(this.plugin, this.file, resource);
				return this;
			}
			catch(IOException exception) 
			{
				throw new ConfigLoadException(path, resource, exception);
			}
		}

		public SpigotConfig build()
		{
			return new SpigotConfig(this);
		}
	}
}