package com.loncha.gothicjobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

public class Profesiones implements Listener, Plugin{
	static Main m;
	
	FileConfiguration configFile;
	
	//Array con todos los nombres de los bloques y crafteos de cada una de las profesiones (cada array es una profesión y cada fila del array es un nivel)
	ArrayList<ArrayList<String>> accionesProfesionHerrero = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> accionesProfesionCazador = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> accionesProfesionConstructor = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> accionesProfesionGranjero = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> accionesProfesionTabernero = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> accionesProfesionAlquimista = new ArrayList<ArrayList<String>>();
	
	ArrayList<ArrayList<ArrayList<String>>> listaProfesiones = new ArrayList<ArrayList<ArrayList<String>>>();
	
	static String[] profesiones = {"herrero","cazador","constructor","granjero","tabernero","alquimista"};
	
	ArrayList<ArrayList<ArrayList<String>>> permisosProfesiones = new ArrayList<ArrayList<ArrayList<String>>>();
	
	public Profesiones(Main m) {
		this.m = m;	
		
		//RELLENAR ARRAY DE PERMISOS
		ArrayList<ArrayList<String>> tempPermisos = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < 6; i++) {
			ArrayList<String> tempPermiso = new ArrayList<String>();
			for (int k = 0; k < 6; k++) {
				String permiso = profesiones[i]+k;
				tempPermiso.add(permiso);
			}
			tempPermisos.add(tempPermiso);
		}
		
		permisosProfesiones.add(tempPermisos);
	}
	
	//Método para comprobar a partir de los puntos invertidos en cada profesión en que nivel te encuentras.
	public static int[] checkNivelesProfesiones(Player p) {
		int[] niveles = new int[6];
		
		//Coge los puntos repartidos entre las profesiones del usuario que se le pasa como parámetro
		Double[] puntosProfesiones = m.getPuntosProfesiones(p);
		
		for (int i = 0; i < puntosProfesiones.length; i++) {
			double puntos = puntosProfesiones[i];
			
			for (int k = 0; k < m.puntosDeCorte.length; k++) {
				if (puntos >= m.puntosDeCorte[k]) {
					niveles[i] = k+1;
					if (!p.hasPermission("gjobs."+profesiones[i]+niveles[i])) {
						m.getServer().dispatchCommand(m.getServer().getConsoleSender(), "upc addGroup " + p.getName() + " gjobs." + profesiones[i] + niveles[i]);
					}
				}
			}
		}
		
		return niveles;
	}
	
	public void rellenarListas() throws FileNotFoundException, IOException, InvalidConfigurationException {
		accionesProfesionHerrero = new ArrayList<ArrayList<String>>();
		accionesProfesionCazador = new ArrayList<ArrayList<String>>();
		accionesProfesionConstructor = new ArrayList<ArrayList<String>>();
		accionesProfesionGranjero = new ArrayList<ArrayList<String>>();
		accionesProfesionTabernero = new ArrayList<ArrayList<String>>();
		accionesProfesionAlquimista = new ArrayList<ArrayList<String>>();
		
		listaProfesiones = new ArrayList<ArrayList<ArrayList<String>>>();
		
		File config = new File("plugins/GothicJobs/config.yml");
		
		configFile = new YamlConfiguration();
		configFile.load(config);
		
		//Profesiones
		for (int i = 0; i < 6; i++) {
			ArrayList<ArrayList<String>> tempProfesiones = new ArrayList<ArrayList<String>>();
			
			//Rellenar el array de profesiones con placeholders
			for (int j = 0; j < 6; j++) {
				ArrayList<String> temp = new ArrayList<String>();
				tempProfesiones.add(temp);
			}
 			
			//Niveles
			for (int k = 0; k < 6; k++) {
				ArrayList<String> tempCrafteos = new ArrayList<String>();
				
				tempCrafteos = (ArrayList<String>) getCustomConfig().getStringList(profesiones[i]+".nivel"+k);
				tempProfesiones.set(k, tempCrafteos);
			}
			
			listaProfesiones.add(tempProfesiones);
		}
		
	}
	
	public void vaciarListas() {
		listaProfesiones = new ArrayList<ArrayList<ArrayList<String>>>();
	}
	
	public void subirNivelProfesion(Player p, int profesion, int nivelAccion, double puntosAccion) throws InvalidConfigurationException {
		int[] nivelProfesiones = checkNivelesProfesiones(p);
		
		int puntosRestantes = m.getPuntosProfesionRestantes(p);
		Double[] puntosProfesiones = m.getPuntosProfesiones(p);
		Boolean[] profesionesLockeadas = m.getProfesionesLockeadas(p);
		
		double puntosCrafteo = 0;
		
		if (puntosAccion > 0) {
			puntosCrafteo = puntosAccion;
		} else {
			puntosCrafteo = m.puntosPorCrafteo;
		}

		//Comprueba si el nivel del crafteo es igual a tu nivel de profesión
		if (nivelProfesiones[profesion] == nivelAccion) {
			//Comprueba si la profesión no esta bloqueada
			if (!profesionesLockeadas[profesion]) {
				//Comprueba si tienes suficientes puntos para gastarlos
				if (puntosRestantes >= puntosCrafteo) {
					//Comprueba si no tienes la profesión al máximo
					if (puntosProfesiones[profesion] < m.puntosDeCorte[nivelAccion]) {
						puntosProfesiones[profesion] += puntosCrafteo;
						puntosRestantes -= puntosCrafteo;
						
						m.puntosProfesiones.put(p, puntosProfesiones);
						m.puntosProfesionRestantes.put(p, puntosRestantes);
						
						checkNivelesProfesiones(p);
						
						m.actualizarDatos(p); //Actualiza el YML
						
						p.sendMessage("Has subido puntos en la rama de " + profesiones[profesion]);
						
					}
				}
			}
		}
	}
	
	//Código para levear las diferentes profesiones según las acciones
	
	//Crafteos normales (crafting table o inventario)
	@EventHandler
	public void onItemCraft(CraftItemEvent e) throws InvalidConfigurationException {
		Player p = (Player) e.getWhoClicked();

		ItemStack item = e.getInventory().getResult();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		
		String nombreItem = "";
		String nombreItemInHand = "";
		
		//Guarda el nombre del item que has crafteado
		if (item.hasItemMeta()) {
			nombreItem = item.getItemMeta().getDisplayName();
		} else {
			nombreItem = item.getType().toString();
		}
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}

		for (int i = 0 ; i < listaProfesiones.size(); i++) {
			ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
			
			for (int k = 0; k < tempProfesion.size(); k++) {
				for (String s : tempProfesion.get(k)) {
					if (e.getCursor() != null && e.getCursor().getType() != Material.AIR && e.getCursor().hasItemMeta()) {
						if (e.getCursor().getItemMeta().getDisplayName().equals(e.getCurrentItem().getItemMeta().getDisplayName())) {
							if (e.getCursor().getAmount() < e.getCurrentItem().getMaxStackSize()) {
								if (s.contains(",")) {
									String[] palabra = s.split(",");
			
									if (palabra.length < 3) {
										if (palabra[0].equalsIgnoreCase(nombreItem)) {
											if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
												subirNivelProfesion(p, i, k, 0);
												break;
												
											} else if (isNumeric(palabra[1])){
												double puntosCrafteo = Double.parseDouble(palabra[1]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
												
											} else {
												e.setCancelled(true);
											}
										}
									} else {
										if (palabra[0].equalsIgnoreCase(nombreItem)) {
											if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
												if (isNumeric(palabra[2])) {
													double puntosCrafteo = Double.parseDouble(palabra[2]);
													subirNivelProfesion(p, i, k, puntosCrafteo);
													break;
												}
											}
										}
									}
								} else {
									if (s.equals(nombreItem)) {
										subirNivelProfesion(p, i, k, 0);
										break;
									}
								}
							}
						}
					} else {
						if (s.contains(",")) {
							String[] palabra = s.split(",");
	
							if (palabra.length < 3) {
								if (palabra[0].equalsIgnoreCase(nombreItem)) {
									if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
										subirNivelProfesion(p, i, k, 0);
										break;
										
									} else if (isNumeric(palabra[1])){
										double puntosCrafteo = Double.parseDouble(palabra[1]);
										subirNivelProfesion(p, i, k, puntosCrafteo);
										break;
										
									} else {
										e.setCancelled(true);
									}
								}
							} else {
								if (palabra[0].equalsIgnoreCase(nombreItem)) {
									if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
										if (isNumeric(palabra[2])) {
											double puntosCrafteo = Double.parseDouble(palabra[2]);
											subirNivelProfesion(p, i, k, puntosCrafteo);
											break;
										}
									}
								}
							}
						} else {
							if (s.equals(nombreItem)) {
								subirNivelProfesion(p, i, k, 0);
								break;
							}
						}
					}
				}
			}
		}
		
	}
	
	//Fundir cosas en un horno
	@EventHandler
	public void onItemSmelt(FurnaceExtractEvent e) throws InvalidConfigurationException {
		Player p = e.getPlayer();
		ItemStack item = e.getPlayer().getItemOnCursor();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		
		String nombreItem = "";
		String nombreItemInHand = "";
		
		//Guarda el nombre del item que has crafteado
		if (item.hasItemMeta()) {
			nombreItem = item.getItemMeta().getDisplayName();
		} else {
			nombreItem = item.getType().toString();
		}
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		for (int i = 0 ; i < listaProfesiones.size(); i++) {
			ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
			
			for (int k = 0; k < tempProfesion.size(); k++) {
				for (String s : tempProfesion.get(i)) {
					if (s.contains(",")) {
						String[] palabra = s.split(",");
						
						if (palabra.length < 3) {
							if (palabra[0].equalsIgnoreCase(nombreItem)) {
								if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
									subirNivelProfesion(p, i, k, 0);
									break;
								} else if (isNumeric(palabra[1])){
									double puntosCrafteo = Double.parseDouble(palabra[1]);
									subirNivelProfesion(p, i, k, puntosCrafteo);
									break;
								}
							}
						} else {
							if (palabra[0].equalsIgnoreCase(nombreItem)) {
								if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
									if (isNumeric(palabra[2])) {
										double puntosCrafteo = Double.parseDouble(palabra[2]);
										subirNivelProfesion(p, i, k, puntosCrafteo);
										break;
									}
								}
							}
						}
					} else {
						if (s.equals(nombreItem)) {
							subirNivelProfesion(p, i, k, 0);
							break;
						}
					}
				}
			}
		}
	}
	
	//Cuando rompes un bloque
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) throws InvalidConfigurationException {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		
		String nombreItemInHand = "";

		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (b.hasMetadata("left") || b.hasMetadata("right")) {
			e.setCancelled(true);
		}
		
		for (int i = 0 ; i < listaProfesiones.size(); i++) {
			ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
			
			for (int k = 0; k < tempProfesion.size(); k++) {
				for (String s : tempProfesion.get(k)) {
					if (s.contains(",")) {
						String[] palabra = s.split(",");
						
						//Si el bloque tiene el metadato guardado en la lista
						if (b.hasMetadata(palabra[0])) {
							b.removeMetadata(palabra[0], m);
							
							if (palabra.length < 3) {
								if (isNumeric(palabra[1])) {
									Double puntosCrafteo = Double.parseDouble(palabra[1]);
									subirNivelProfesion(p, i, k, puntosCrafteo);
									break;
									
								} else {
									if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
										subirNivelProfesion(p, i, k, 0);
										break;
									}
								}
							} else {
								if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
									if (isNumeric(palabra[2])) {
										double puntosCrafteo = Double.parseDouble(palabra[2]);
										subirNivelProfesion(p, i, k, puntosCrafteo);
										break;
									}
								}
							}
						//Si el bloque es del tipo guardado en la lista	
						} else if (b.getType().toString().equalsIgnoreCase(palabra[0])){
							if (palabra.length < 3) {
								if (isNumeric(palabra[1])) {
									Double puntosCrafteo = Double.parseDouble(palabra[1]);
									subirNivelProfesion(p, i, k, puntosCrafteo);
									break;
									
								} else {
									if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
										subirNivelProfesion(p, i, k, 0);
										break;
									}
								}
							} else {
								if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
									if (isNumeric(palabra[2])) {
										double puntosCrafteo = Double.parseDouble(palabra[2]);
										subirNivelProfesion(p, i, k, puntosCrafteo);
										break;
									}
								}
							}
						}
					} else {
						if (s.equals(b.getType().toString())) {
							subirNivelProfesion(p, i, k, 0);
							break;
						}
					}
				}
			}
		}
		
	}
	
	//Cuando interactúas con un bloque (click derecho o izquierdo)
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) throws InvalidConfigurationException {
		Player p = e.getPlayer();
		ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();
		Block b = e.getClickedBlock();
		
		String nombreItemInHand = "";

		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (b.hasMetadata("right")) {
				e.setCancelled(true);
				for (int i = 0 ; i < listaProfesiones.size(); i++) {
					ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
					
					for (int k = 0; k < tempProfesion.size(); k++) {
						for (String s : tempProfesion.get(k)) {
							if (s.contains(",")) {
								String[] palabra = s.split(",");
								
								//Si el bloque tiene el metadato guardado en la lista
								if (b.hasMetadata(palabra[0])) {
									b.removeMetadata(palabra[0], m);
									
									if (palabra.length < 3) {
										if (isNumeric(palabra[1])) {
											Double puntosCrafteo = Double.parseDouble(palabra[1]);
											subirNivelProfesion(p, i, k, puntosCrafteo);
											break;
											
										} else {
											if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
												subirNivelProfesion(p, i, k, 0);
												break;
											} else {
												break;
											}
										}
									} else {
										if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
											if (isNumeric(palabra[2])) {
												double puntosCrafteo = Double.parseDouble(palabra[2]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
											}
										} else {
											break;
										}
									}
								//Si el bloque es del tipo guardado en la lista	
								} else if (b.getType().toString().equalsIgnoreCase(palabra[0])){
									if (palabra.length < 3) {
										if (isNumeric(palabra[1])) {
											Double puntosCrafteo = Double.parseDouble(palabra[1]);
											subirNivelProfesion(p, i, k, puntosCrafteo);
											break;
											
										} else {
											if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
												subirNivelProfesion(p, i, k, 0);
												break;
											}
										}
									} else {
										if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
											if (isNumeric(palabra[2])) {
												double puntosCrafteo = Double.parseDouble(palabra[2]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
											}
										}
									}
								}
							} else {
								if (s.equals(b.getType().toString())) {
									subirNivelProfesion(p, i, k, 0);
									break;
								}
							}
						}
					}
				}
			}
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (b.hasMetadata("left")) {
				e.setCancelled(true);
				for (int i = 0 ; i < listaProfesiones.size(); i++) {
					ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
					
					for (int k = 0; k < tempProfesion.size(); k++) {
						for (String s : tempProfesion.get(k)) {
							if (s.contains(",")) {
								String[] palabra = s.split(",");
								//Si el bloque tiene el metadato guardado en la lista
								if (b.hasMetadata(palabra[0])) {
									System.out.println("profesion1");
									b.removeMetadata(palabra[0], m);
									
									if (palabra.length < 3) {
										System.out.println("2");
										if (isNumeric(palabra[1])) {
											Double puntosCrafteo = Double.parseDouble(palabra[1]);
											subirNivelProfesion(p, i, k, puntosCrafteo);
											break;
											
										} else {
											if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
												subirNivelProfesion(p, i, k, 0);
												break;
											} else {
												break;
											}
										}
									} else {
										System.out.println("3");
										if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
											System.out.println("herramienta");
											if (isNumeric(palabra[2])) {
												System.out.println("puntos");
												double puntosCrafteo = Double.parseDouble(palabra[2]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
											}
										} else {
											break;
										}
									}
								//Si el bloque es del tipo guardado en la lista	
								} else if (b.getType().toString().equalsIgnoreCase(palabra[0])){
									if (palabra.length < 3) {
										if (isNumeric(palabra[1])) {
											Double puntosCrafteo = Double.parseDouble(palabra[1]);
											subirNivelProfesion(p, i, k, puntosCrafteo);
											break;
											
										} else {
											if (nombreItemInHand.equalsIgnoreCase(palabra[1])) {
												subirNivelProfesion(p, i, k, 0);
												break;
											}
										}
									} else {
										if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
											if (isNumeric(palabra[2])) {
												double puntosCrafteo = Double.parseDouble(palabra[2]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
											}
										}
									}
								}
							} else {
								if (s.equals(b.getType().toString())) {
									subirNivelProfesion(p, i, k, 0);
									break;
								}
							}
						}
					}
				}
			} else if (b.hasMetadata("right")) {
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) throws InvalidConfigurationException {
		
		if (e.getEntity().getKiller() instanceof Player) {
			Player p = e.getEntity().getKiller();
			ItemStack itemInHand = p.getInventory().getItemInMainHand();
			String entityName = "";
			Location l = e.getEntity().getLocation();
			
			String nombreItemInHand = "";
			
			if (itemInHand.hasItemMeta()) {
				nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
			} else {
				nombreItemInHand = itemInHand.getType().toString();
			}
			
			if (e.getEntity().getCustomName() != null) {
				entityName = e.getEntity().getCustomName();
			} else {
				entityName = e.getEntity().getName();
			}

			//Si has matado a un mob
			if (e.getEntity() instanceof LivingEntity) {
				if (!(e.getEntity() instanceof Player)) {
					for (int i = 0 ; i < listaProfesiones.size(); i++) {
						ArrayList<ArrayList<String>> tempProfesion = listaProfesiones.get(i);
						
						for (int k = 0; k < tempProfesion.size(); k++) {
							for (String s : tempProfesion.get(k)) {
								if (s.contains(",")) {
									String[] palabra = s.split(",");
									
									if (palabra.length < 3) {
										if (palabra[0].equalsIgnoreCase(entityName)) {
											if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
												subirNivelProfesion(p, i, k, 0);
												break;
											} else if (isNumeric(palabra[1])){
												double puntosCrafteo = Double.parseDouble(palabra[1]);
												subirNivelProfesion(p, i, k, puntosCrafteo);
												break;
											}
										}
									} else {
										if (palabra[0].equalsIgnoreCase(entityName)) {
											if (palabra[1].equalsIgnoreCase(nombreItemInHand)) {
												if (isNumeric(palabra[2])) {
													double puntosCrafteo = Double.parseDouble(palabra[2]);
													subirNivelProfesion(p, i, k, puntosCrafteo);
													break;
												}
											}
										}
									}
								} else {
									if (s.equals(entityName)) {
										subirNivelProfesion(p, i, k, 0);
										break;
									}
								}
							}
						}
					}	
				}
			}
		}
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return false;
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
