package dte.spigotconfiguration.exceptions;

import org.apache.commons.lang.exception.ExceptionUtils;

public class ConfigLoadException extends RuntimeException
{
	private final String path;
	
	private static final long serialVersionUID = 1914688087421355295L;
	
	public ConfigLoadException(String path, String message) 
	{
		super(message);
		
		this.path = path;
	}
	
	public ConfigLoadException(String path, String message, Throwable cause) 
	{
		this(path, String.format("Unable to load '%s' due to: %s", path, message.replace("%cause%", ExceptionUtils.getMessage(cause))));
		
		initCause(cause);
	}
	
	public ConfigLoadException(String path, Throwable cause) 
	{
		this(path, ExceptionUtils.getMessage(cause), cause);
	}
	
	public String getPath()
	{
		return this.path;
	}
}