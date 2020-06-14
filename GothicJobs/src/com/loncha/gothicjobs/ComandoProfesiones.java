package com.loncha.gothicjobs;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

public class ComandoProfesiones implements CommandExecutor, Plugin  {
	InterfazProfesiones ifaceProfesiones;
	
	public ComandoProfesiones(InterfazProfesiones ifaceProfesiones) {
		this.ifaceProfesiones = ifaceProfesiones;
	}
	
	//Comando para mostrar la interfaz de profesiones
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player p = (Player) arg0;
		
		if (arg1.getName().equalsIgnoreCase("profesiones")) {
			ifaceProfesiones.mostrarProfesiones(p);
			return true;
		}
		
		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public FileConfiguration getConfig() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public File getDataFolder() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ChunkGenerator getDefaultWorldGenerator(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PluginDescriptionFile getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public PluginLoader getPluginLoader() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public InputStream getResource(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Server getServer() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isNaggable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void reloadConfig() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void saveConfig() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void saveDefaultConfig() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void saveResource(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setNaggable(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
