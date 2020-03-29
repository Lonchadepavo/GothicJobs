package com.loncha.gothicjobs;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InterfazProfesiones implements Listener {
	Main m;
	Profesiones profesiones;
	
	//Array de iconos para el menú de profesiones
	Material[] iconosProfesiones = {Material.ANVIL, Material.BOW, Material.IRON_PICKAXE, Material.IRON_HOE, Material.BEETROOT_SOUP, Material.POTION};
	String[] nombreProfesiones = {"Rama de herrero", "Rama de cazador", "Rama de constructor", "Rama de granjero", "Rama de tabernero", "Rama de alquimista"};
	String[] descripcionProfesiones = {"Crafteos de herrero", "Crafteos de cazador", "Crafteos de constructor", "Crafteos de granjero", "Crafteos de tabernero", "Crafteos de alquimista"};
	
	public InterfazProfesiones(Main m, Profesiones profesiones) {
		this.m = m;
		this.profesiones = profesiones;
	}
	
	public void mostrarProfesiones(Player p) {
		m.cargarDatos(p);
		
		int puntosRestantes = m.getPuntosProfesionRestantes(p);
		Double[] puntosProfesiones = m.getPuntosProfesiones(p);
		Boolean[] profesionesLockeadas = m.getProfesionesLockeadas(p);
		
		Inventory invProfesiones = Bukkit.createInventory(p, 54, "Profesiones");
		
		//Establecer iconos de las profesiones
		int posicionIcono = 0;
		
		nombreProfesiones = new String[]{"Rama de herrero", "Rama de cazador", "Rama de constructor", "Rama de granjero", "Rama de tabernero", "Rama de alquimista"};
		for (int i = 0; i < profesionesLockeadas.length; i++) {
			if (profesionesLockeadas[i]) {
				nombreProfesiones[i] = "§c"+nombreProfesiones[i];
				System.out.println(nombreProfesiones[i]);
			} else {
				nombreProfesiones[i] = "§f"+nombreProfesiones[i];
			}
		}
		
		for (int i = 0; i < iconosProfesiones.length; i++) {
			createDisplay(iconosProfesiones[i], invProfesiones, posicionIcono, nombreProfesiones[i], descripcionProfesiones[i]);
			posicionIcono += 9;
		}
		
		//Establecer los niveles de las profesiones
		
		//Devuelve el nivel de cada profesion de un usuario en concreto.
		int[] nivelesProfesiones = profesiones.checkNivelesProfesiones(p);
		
		posicionIcono = 0;
		
		//Profesiones
		for (int i = 0; i < nivelesProfesiones.length; i++) {
			int nivelProfesion = nivelesProfesiones[i];

			//Niveles
			for (int k = 1; k <= 5; k++) {
				posicionIcono++;
				
				if (k <= nivelProfesion) {
					createDisplay(Material.EMERALD_BLOCK, invProfesiones, posicionIcono, "Nivel " + k , "");
				} else {
					createDisplay(Material.REDSTONE_BLOCK, invProfesiones, posicionIcono, "Nivel " + k , "");
				}
				
			}
			
			posicionIcono += 4;
		}
		
		//Icono de puntos restantes
		createDisplay(Material.BOOK, invProfesiones, 8, "Puntos de profesión restantes: " + m.getPuntosProfesionRestantes(p), "Estos son los puntos que te quedan para gastar en las diferentes profesiones");
		
		//Abre el inventario de profesiones
		p.openInventory(invProfesiones);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		Inventory inv = e.getInventory();
		
		if (e.getCurrentItem() != null) {
			Material clickedItem = e.getCurrentItem().getType();
			
			if (inv.getTitle().equalsIgnoreCase("Profesiones")) {
				e.setCancelled(true);
				
				for (int i = 0; i < iconosProfesiones.length; i++) {
					if (clickedItem == iconosProfesiones[i]) {
						Boolean[] profesionesLockeadas = m.getProfesionesLockeadas(p);
						
						profesionesLockeadas[i] = !profesionesLockeadas[i];
						
						for (Boolean b : profesionesLockeadas) {
							System.out.println(b);
						}
						
						m.profesionesLockeadas.put(p, profesionesLockeadas);
						System.out.println("La profesión: " + nombreProfesiones[i] + " está en modo: " + profesionesLockeadas[i]);
	
	
						
						try {
							m.actualizarDatos(p);
							mostrarProfesiones(p);
							
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				
			}
		}
	}
	
	public static void createDisplay(Material material, Inventory inv, int Slot, String name, String lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.setLore(Lore);
		item.setItemMeta(meta);
		 
		inv.setItem(Slot, item); 
		 
	}
}
