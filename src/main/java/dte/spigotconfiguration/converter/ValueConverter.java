package dte.spigotconfiguration.converter;

import dte.spigotconfiguration.exceptions.ConfigLoadException;

@FunctionalInterface
public interface ValueConverter<T>
{
	T convertFrom(Object object) throws ConfigLoadException;
}
