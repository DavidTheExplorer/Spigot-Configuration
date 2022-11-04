package dte.spigotconfiguration.converter;

import org.bukkit.Material;

import dte.spigotconfiguration.exceptions.ConfigLoadException;

@FunctionalInterface
public interface ValueConverter<T>
{
	T convertFrom(Object object) throws ConfigLoadException;
	
	ValueConverter<Material> MATERIAL = (materialName) -> Material.matchMaterial(materialName.toString().toUpperCase());
}
