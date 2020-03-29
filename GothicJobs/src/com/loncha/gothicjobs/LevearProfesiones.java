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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

public class LevearProfesiones implements CommandExecutor{
	Profesiones prof;
	
	public LevearProfesiones(Profesiones prof) {
		this.prof = prof;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player p = (Player) arg0;
		
		if (p.isOp()) {
			if (arg1.getName().equalsIgnoreCase("levearprofesiones")) {
				for (int i = 0; i < prof.profesiones.length; i++) {
					if (arg3[0].equalsIgnoreCase(prof.profesiones[i])) {
						int profesion = i;
						int nivelProfesion = prof.checkNivelesProfesiones(p)[i];
						
						try {
							prof.subirNivelProfesion(p, profesion, nivelProfesion, 100);
							return true;
							
						} catch (InvalidConfigurationException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}

}
