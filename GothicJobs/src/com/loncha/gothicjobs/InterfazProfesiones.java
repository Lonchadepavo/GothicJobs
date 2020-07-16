package com.loncha.gothicjobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InterfazProfesiones implements Listener {
	Main m;
	Profesiones profesiones;
	
	//Array de iconos para el menú de profesiones
	Material[] iconosProfesiones = {Material.ANVIL, Material.BOW, Material.IRON_PICKAXE, Material.IRON_HOE, Material.BEETROOT_SOUP, Material.POTION};
	
	//Array con el nombre y descripción de las profesiones en la interfaz.
	String[] nombreProfesiones = {"Rama de herrero", "Rama de cazador", "Rama de constructor", "Rama de granjero", "Rama de tabernero", "Rama de alquimista"};
	String[] descripcionProfesiones = {"Crafteos de herrero", "Crafteos de cazador", "Crafteos de constructor", "Crafteos de granjero", "Crafteos de tabernero", "Crafteos de alquimista"};
	String[] puntosDeCorte = {"0 puntos necesarios","200 puntos necesarios","600 puntos necesarios","1400 puntos necesarios","3000 puntos necesarios","6300 puntos necesarios"};
	
	//Constructor
 	public InterfazProfesiones(Main m, Profesiones profesiones) {
		this.m = m;
		this.profesiones = profesiones;
	}
	
 	//Método encargado de listar las profesiones en la interfaz correspondiente (Las lista con las que tienes desbloqueadas, los puntos, etc.)
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
			} else {
				nombreProfesiones[i] = "§f"+nombreProfesiones[i];
			}
		}
		
		for (int i = 0; i < iconosProfesiones.length; i++) {
			createDisplay(iconosProfesiones[i], invProfesiones, posicionIcono, nombreProfesiones[i], new String[] {descripcionProfesiones[i]});
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
			for (int k = 0; k <= 5; k++) {
				posicionIcono++;
				
				if (k <= nivelProfesion) {
					createDisplay(Material.EMERALD_BLOCK, invProfesiones, posicionIcono, "Nivel " + k , new String[] {puntosDeCorte[k], ChatColor.GOLD+"Click para más información..."});
				} else {
					createDisplay(Material.REDSTONE_BLOCK, invProfesiones, posicionIcono, "Nivel " + k , new String[] {puntosDeCorte[k], ChatColor.GOLD+"Click para más información..."});
				}
				
			}
			
			posicionIcono += 3;
		}
		
		//Icono de puntos restantes
		createDisplay(Material.BOOK, invProfesiones, 8, "Puntos de profesión restantes: " + m.getPuntosProfesionRestantes(p), new String[] {"Estos son los puntos que te quedan para gastar en las diferentes profesiones"});
		
		//Abre el inventario de profesiones
		p.openInventory(invProfesiones);
		
	}
	
	public void mostrarCrafteosProfesiones(Player p, String profesion, String nivel) {
		Inventory invProfesiones = Bukkit.createInventory(p, 9, "Información profesiones");
		
		for (int i = 0; i < 9; i++) {
			createDisplay(Material.STAINED_GLASS_PANE, invProfesiones, i," ", new String[] {});
		}
		
		Material mProf = Material.BOOK;
		for (int i = 0; i < nombreProfesiones.length; i++) {
			if (nombreProfesiones[i].contains(profesion.toLowerCase())) {
				mProf = iconosProfesiones[i];
			}
		}
		
		createDisplay(mProf, invProfesiones, 0,"§f"+profesion + " " + nivel, new String[] {});
		createDisplay(Material.BOOK, invProfesiones, 3, "§fObjetos desbloqueados", new String[] {"Lista de objetos que desbloqueas al " + nivel.toLowerCase() + " de " + profesion.toLowerCase()});
		createDisplay(Material.ANVIL, invProfesiones, 5, "§fMecánicas desbloqueadas", new String[] {"Lista de mecánicas que desbloqueas al " + nivel.toLowerCase() + " de " + profesion.toLowerCase()});
		createDisplay(Material.REDSTONE, invProfesiones, 8, "§fVolver atras", new String[] {});
		
		p.openInventory(invProfesiones);
	}
	
	public void mostrarObjProfesiones(Player p, String profesion, int nivel, String tipo) {
		ObjetoProfesion objProf = new ObjetoProfesion();

		for (ObjetoProfesion temp : Main.listaInterfacesProfesiones) {
			if (temp.getNombreProfesion().equalsIgnoreCase(profesion)) {
				if (temp.getNivelProfesion() == nivel) {
					objProf = temp;
					break;
				}
			}
		}
		
		List<ItemStack> itemsProf = new ArrayList<ItemStack>();
		
		if (tipo.equalsIgnoreCase("objetos")) {
			itemsProf = objProf.getObjetosProfesion();
			
			
		} else if (tipo.equalsIgnoreCase("mecanicas")) {
			itemsProf = objProf.getMecanicasProfesion();
			
		}
		
		int sizeInterface = 9;
		
		if (itemsProf.size() >= 10 && itemsProf.size() < 18) {
			sizeInterface = 18;
		} else if (itemsProf.size() >= 18 && itemsProf.size() < 27) {
			sizeInterface = 27;
		} else if (itemsProf.size() >= 27 && itemsProf.size() < 36) {
			sizeInterface = 36;
		} else if (itemsProf.size() >= 36 && itemsProf.size() < 45) {
			sizeInterface = 45;
		} else if (itemsProf.size() >= 45 && itemsProf.size() < 54) {
			sizeInterface = 54;
		}
		
		Inventory invProfesiones = Bukkit.createInventory(p, sizeInterface, "Desbloqueables");

		for (int i = 0; i < itemsProf.size(); i++) {
			invProfesiones.setItem(i, itemsProf.get(i));
		}
		
		createDisplay(Material.REDSTONE, invProfesiones, invProfesiones.getSize()-1, "§fVolver atras", new String[] {});
		
		p.openInventory(invProfesiones);
	}
	
	//Evento de click en un inventario
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		Inventory inv = e.getClickedInventory();
		if (e.getCurrentItem() != null) {
			if (inv.getType() != InventoryType.PLAYER) {
				if (inv.getTitle().equalsIgnoreCase("Profesiones")) {
					e.setCancelled(true);
					Material clickedItem = e.getCurrentItem().getType();
					
					//DETECTAR CLICK EN LAS PROFESIONES PARA BLOQUEARLAS
					for (int i = 0; i < iconosProfesiones.length; i++) {
						if (clickedItem == iconosProfesiones[i]) {
							Boolean[] profesionesLockeadas = m.getProfesionesLockeadas(p);
							
							profesionesLockeadas[i] = !profesionesLockeadas[i];
							
							m.profesionesLockeadas.put(p, profesionesLockeadas);
		
							try {
								m.actualizarDatos(p);
								mostrarProfesiones(p);
								
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					
					//DETECTAR CLICK EN LOS NIVELES
					ItemStack fullClickedItem = e.getCurrentItem();
					
					if (fullClickedItem.hasItemMeta()) {
						//HERRERO
						if (e.getSlot() > 0 && e.getSlot() < 8) {
							mostrarCrafteosProfesiones(p, "Herrero", fullClickedItem.getItemMeta().getDisplayName());
						} 
						//CAZADOR
						else if (e.getSlot() > 9 && e.getSlot() < 17) {
							mostrarCrafteosProfesiones(p, "Cazador", fullClickedItem.getItemMeta().getDisplayName());
						}
						//CONSTRUCTOR
						else if (e.getSlot() > 18 && e.getSlot() < 26) {
							mostrarCrafteosProfesiones(p, "Constructor", fullClickedItem.getItemMeta().getDisplayName());
						}
						//GRANJERO
						else if (e.getSlot() > 27 && e.getSlot() < 35) {
							mostrarCrafteosProfesiones(p, "Granjero", fullClickedItem.getItemMeta().getDisplayName());
						}
						//TABERNERO
						else if (e.getSlot() > 36 && e.getSlot() < 44) {
							mostrarCrafteosProfesiones(p, "Tabernero", fullClickedItem.getItemMeta().getDisplayName());
						}
						//ALQUIMISTA
						else if (e.getSlot() > 45 && e.getSlot() < 53) {
							mostrarCrafteosProfesiones(p, "Alquimista", fullClickedItem.getItemMeta().getDisplayName());
						}
					}
					
				} else if (inv.getTitle().equalsIgnoreCase("Información profesiones")) {
					ItemStack fullClickedItem = e.getCurrentItem();
					
					e.setCancelled(true);
					
					String[] splitter = e.getInventory().getItem(0).getItemMeta().getDisplayName().split(" ");
					
					String profesion = splitter[0];
					int nivelProfesion = Integer.valueOf(splitter[2]);
					
					if (fullClickedItem.hasItemMeta()) {
						if (fullClickedItem.getItemMeta().getDisplayName().equals("§fObjetos desbloqueados")) {
							mostrarObjProfesiones(p, profesion, nivelProfesion, "Objetos");
							
						} else if (fullClickedItem.getItemMeta().getDisplayName().equals("§fMecánicas desbloqueadas")) {
							mostrarObjProfesiones(p, profesion, nivelProfesion, "Mecanicas");
							
						} else if (fullClickedItem.getItemMeta().getDisplayName().contains("Volver atras")) {
							mostrarProfesiones(p);
							
						}
					}
				} else if (inv.getTitle().equalsIgnoreCase("Desbloqueables")) {
					e.setCancelled(true);
					if (e.getCurrentItem() != null) {
						ItemStack fullClickedItem = e.getCurrentItem();
						
						if (fullClickedItem.hasItemMeta()) {
							if (fullClickedItem.getItemMeta().getDisplayName().contains("Volver atras")) {
								mostrarProfesiones(p);		
							}
						}
					}
				}
			}
		}
	}
	
	//Método para crear facilmente items en una interfaz.
	public static void createDisplay(Material material, Inventory inv, int Slot, String name, String[] lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> Lore = new ArrayList<String>();
		for (String s : lore) {
			Lore.add(s);
		}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.setLore(Lore);
		item.setItemMeta(meta);
		 
		inv.setItem(Slot, item); 
		 
	}
}
