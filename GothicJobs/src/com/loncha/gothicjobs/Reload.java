package com.loncha.gothicjobs;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Reload implements CommandExecutor {
	Main m;
	Profesiones prof;
	public Reload(Main m, Profesiones prof) {
		this.m = m;
		this.prof = prof;
	}
	

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player p = (Player) arg0;
		if (p.isOp()) {
			if (arg1.getName().equalsIgnoreCase("reloadjobs")) {
				m.cargarConfigInicial();
				try {
					prof.rellenarListas();
					p.sendMessage(ChatColor.GREEN+"GothicJobs recargado");
					return true;
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}

}
