package dte.spigotconfiguration.exceptions;

public class ConfigLoadException extends RuntimeException
{
	private static final long serialVersionUID = 1914688087421355295L;
	
	private final String path;
	private boolean resource;
	
	public ConfigLoadException(String path, boolean resource) 
	{
		super(String.format("Cannot load a config file at: %s", path));
		
		this.path = path;
		this.resource = resource;
	}
	
	public String getPath() 
	{
		return this.path;
	}
	
	public boolean isResource() 
	{
		return this.resource;
	}
}