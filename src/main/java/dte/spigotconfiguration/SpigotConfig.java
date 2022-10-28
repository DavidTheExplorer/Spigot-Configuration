package dte.spigotconfiguration;

import static dte.spigotconfiguration.utils.YamlConfigurationUtils.getInternalPath;
import static dte.spigotconfiguration.utils.YamlConfigurationUtils.loadConfiguration;
import static dte.spigotconfiguration.utils.YamlConfigurationUtils.loadDataFolder;
import static dte.spigotconfiguration.utils.YamlConfigurationUtils.toResourcePath;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import dte.spigotconfiguration.converter.ValueConverter;
import dte.spigotconfiguration.exceptions.ConfigLoadException;

public class SpigotConfig
{
	private final File file;
	private final YamlConfiguration config;
	private final Logger logger;
	
	protected SpigotConfig(Builder builder)
	{
		this.file = builder.file;
		this.config = builder.config;
		this.logger = builder.logger;
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
	
	@SafeVarargs
	public static void register(Class<? extends ConfigurationSerializable>... serializables) 
	{
		Arrays.stream(serializables).forEach(ConfigurationSerialization::registerClass);
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
		return getList(path, typeClass::cast);
	}
	
	public <T> List<T> getList(String path, ValueConverter<T> valueConverter)
	{
		return this.config.getList(path, new ArrayList<>()).stream()
				.map(object -> 
				{
					try 
					{
						return valueConverter.convertFrom(object);
					}
					catch(ConfigLoadException exception) 
					{
						this.logger.severe(exception.getMessage());
						return null;
					}
				})
				.filter(Objects::nonNull)
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
	
	public boolean contains(String path) 
	{
		return this.config.contains(path);
	}

	
	
	public static class Builder
	{
		private final Plugin plugin;
		
		//necessary
		private File file;
		private YamlConfiguration config;
		
		//optional
		private Logger logger;
		private String namePattern;
		private List<Function<YamlConfiguration, Map<String, Object>>> defaultsSuppliers = new ArrayList<>();
		
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
		
		public Builder withNamePattern(String pattern) 
		{
			this.namePattern = pattern;
			return this;
		}
		
		public Builder logTo(Logger logger) 
		{
			this.logger = logger;
			return this;
		}
		
		public Builder withDefault(String path, Object value) 
		{
			Objects.requireNonNull(this.config, "Cannot set a default value for an unloaded config!");
			
			this.config.addDefault(path, value);
			return this;
		}

		public Builder supplyDefaults(Function<YamlConfiguration, Map<String, Object>> defaultsSupplier) 
		{
			this.defaultsSuppliers.add(defaultsSupplier);
			return this;
		}
		
		public Builder loadDefaults() throws ConfigLoadException
		{
			Objects.requireNonNull(this.config, "Cannot apply default values for an unloaded config!");
			
			try 
			{
				this.config.options().copyDefaults(true);
				this.defaultsSuppliers.forEach(supplier -> supplier.apply(this.config).forEach(this.config::set));
				this.config.save(this.file);
				return this;
			} 
			catch(Exception exception)
			{
				throw new ConfigLoadException(getInternalPath(this.plugin, this.file), "Cannot apply default values due to %cause%", exception);
			}
		}

		private Builder byPath(String path, boolean resource) throws ConfigLoadException
		{
			if(this.namePattern != null)
				path = this.namePattern.replace("%name%", path);
			
			try
			{
				this.file = new File(loadDataFolder(this.plugin), toResourcePath(path));
				this.config = loadConfiguration(this.plugin, this.file, resource);
				return this;
			}
			catch(IOException exception)
			{
				throw new ConfigLoadException(path, exception);
			}
		}

		public SpigotConfig build()
		{
			if(this.logger == null)
				this.logger = this.plugin.getLogger();
			
			return new SpigotConfig(this);
		}
	}
}