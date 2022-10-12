package dte.spigotconfiguration.exceptions;

public class ConfigLoadException extends RuntimeException
{
	private static final long serialVersionUID = 1914688087421355295L;
	
	private final String path;
	private boolean resource;
	
	public ConfigLoadException(String path, boolean resource, Throwable cause) 
	{
		this("Cannot load", path, resource, cause);
	}
	
	public ConfigLoadException(String message, String path, boolean resource, Throwable cause) 
	{
		super(String.format(createMessageFormat(message), path, cause.getMessage()), cause);
		
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
	
	private static String createMessageFormat(String message) 
	{
		return message + " the config '%s' due to: %s";
	}
}