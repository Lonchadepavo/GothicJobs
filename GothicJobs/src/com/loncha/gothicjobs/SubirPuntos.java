package com.loncha.gothicjobs;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

public class SubirPuntos implements CommandExecutor{
	Profesiones prof;
	Main m;
	
	public SubirPuntos(Profesiones prof, Main m) {
		this.prof = prof;
		this.m = m;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {		
		if (arg0 instanceof ConsoleCommandSender) {
			if (arg1.getName().equalsIgnoreCase("subirpuntos")) {
				String nombrePlayer = arg3[0];
				
				Player p = Bukkit.getPlayer(nombrePlayer);
				
				if (p != null) {
					ItemStack pepitas = new ItemStack(Material.INK_SACK,2,(short) 4);
					ItemMeta pepitasMeta = pepitas.getItemMeta();
					pepitasMeta.setDisplayName("§fPepita de mineral magico");
					pepitas.setItemMeta(pepitasMeta);
					
					p.getInventory().addItem(pepitas);
					
					return true;
				}
			}
		}
		return false;
	}

}