package com.loncha.gothicjobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener{
	Profesiones profesiones = new Profesiones(this);
	InterfazProfesiones ifaceProfesiones = new InterfazProfesiones(this, profesiones);
	
	HashMap<Player,Integer> puntosProfesionRestantes = new HashMap<Player,Integer>();
	HashMap<Player,Boolean[]> profesionesLockeadas = new HashMap<Player,Boolean[]>();
	HashMap<Player,Double[]> puntosProfesiones = new HashMap<Player,Double[]>();
	
	static List<ObjetoProfesion> listaInterfacesProfesiones = new ArrayList<ObjetoProfesion>();
	
	FileConfiguration configFile;
	FileConfiguration playerYaml;
	
	//Variables para el sistema de profesiones
	public static int professionCap = 0;
	public static int[] puntosDeCorte = {100, 200, 300, 400, 500};
	public static int puntosPorCrafteo = 10;
	
	//EL ORDEN DE LAS PROFESIONES EN LOS ARRAYS DE LOCKEADOS Y PUNTOSPROFESION DEBE SER EL SIGUIENTE:
	//Herrero - Cazador - Constructor - Granjero - Tabernero - Alquimista
	//El orden debe estar presente en el archivo del que se lea la información (yml)
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.ifaceProfesiones, this);
		getServer().getPluginManager().registerEvents(this.profesiones, this);
		
		getCommand("profesiones").setExecutor(new ComandoProfesiones(ifaceProfesiones));
		getCommand("levearprofesiones").setExecutor(new LevearProfesiones(profesiones));
		getCommand("subirpuntos").setExecutor(new SubirPuntos(profesiones, this));
		getCommand("reloadjobs").setExecutor(new Reload(this, profesiones));
		
		File dataFolder = new File("plugins/GothicJobs/playerdata");
		File dataParent = new File(dataFolder.getParent());
		
		File configFilePath = new File("plugins/GothicJobs/configfile.yml");
		
		if (!dataParent.exists()) dataParent.mkdir();
		if (!dataFolder.exists()) dataFolder.mkdir();
		if (!configFilePath.exists()) {
			
			try {
				configFilePath.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		cargarConfigInicial();
		cargarDatosInterfaces();
		
		try {
			profesiones.rellenarListas();
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		Block b = e.getBlock();
		Location lDown = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
		if (lDown.getBlock().getType() == Material.COAL_BLOCK) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                	if (b.getType() == Material.FIRE) {
                		b.setType(Material.AIR);
                		lDown.getBlock().setType(Material.AIR);
                	}
                }

            }, 2400);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) throws IOException, InvalidConfigurationException {
		Player p = e.getPlayer();
		
		File originalDataFile = new File("plugins/GothicJobs/playerdata/profesionesplayercopy.yml");
		File dataProfesionesFile = new File("plugins/GothicJobs/playerdata/Profesiones"+p.getName()+".yml");		
		
		//Comprueba si el archivo de datos del player existe, si no existe lo crea y rellena el hashmap con datos default.
		if (!dataProfesionesFile.exists()) {
			Files.copy(originalDataFile.toPath(), dataProfesionesFile.toPath());
			
			Boolean[] tempLock = new Boolean[6];
			Double[] tempPuntosProfesion = new Double[6];
			Integer pProfesionRestantes = professionCap;
			
			for (int i = 0;i < 6; i++) {
				tempLock[i] = false;
				tempPuntosProfesion[i] = (double) 0;
			}
			
			puntosProfesiones.put(p, tempPuntosProfesion);
			profesionesLockeadas.put(p, tempLock);
			puntosProfesionRestantes.put(p, pProfesionRestantes);
			
			actualizarDatos(p);
			
		}
		
		for(String s : profesiones.profesiones) {
			if (!p.hasPermission("gjobs."+s+"0")) {
				getServer().dispatchCommand(getServer().getConsoleSender(), "upc addGroup " + p.getName() + " gjobs." + s + 0);
			}
		}
		
		cargarDatos(p);
	}
	
	//Método que actualiza los archivos de configuración del usuario que se le pase y recarga los valores de los hashmapyo 
	public void actualizarDatos(Player p) throws InvalidConfigurationException {
		guardarDatos(p);
		cargarDatos(p);
	}
	
	public boolean cargarConfigInicial() {
		try {
			File config = new File("plugins/GothicJobs/config.yml");
			
			configFile = new YamlConfiguration();
			configFile.load(config);
			
			List<Integer> tempArrayPuntos = new ArrayList<Integer>();
			
			professionCap = getCustomConfig().getInt("puntos-totales");
			tempArrayPuntos = getCustomConfig().getIntegerList("puntos-de-corte");
			
			for (int i = 0; i < tempArrayPuntos.size(); i++) {
				puntosDeCorte[i] = tempArrayPuntos.get(i);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void cargarDatosInterfaces() {
		try {
			File interfacesProfesiones = new File("plugins/GothicJobs/configinterfaz.yml");
			configFile = new YamlConfiguration();
			configFile.load(interfacesProfesiones);

			String[] profesiones = Profesiones.profesiones;
			
			for (int i = 0; i < profesiones.length; i++) {
				for (int k = 0; k < 6; k++) {
					List<String> tempProfLine = getCustomConfig().getStringList(profesiones[i]+".nivel "+k);
					List<ItemStack> tempObjects = new ArrayList<ItemStack>();
					List<ItemStack> tempMecanicas = new ArrayList<ItemStack>();
					
					ObjetoProfesion objProf = new ObjetoProfesion();
					
					objProf.setNombreProfesion("§f"+profesiones[i]);
					objProf.setNivelProfesion(k);
					
					for (String s : tempProfLine) {
						String[] splitter = s.split("/");
						ItemStack itemProf = new ItemStack(Material.valueOf(splitter[2]), 1, (short) Integer.parseInt(splitter[3]));
						
						ItemMeta metaProf = itemProf.getItemMeta();		
						metaProf.setDisplayName("§f"+splitter[0]);
						metaProf.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
						metaProf.setLore(new ArrayList<String>(Arrays.asList(splitter[1])));
						itemProf.setItemMeta(metaProf);
						
						if (splitter[4].equalsIgnoreCase("M")) {
							tempMecanicas.add(itemProf);
						} else if (splitter[4].equalsIgnoreCase("O")) {
							tempObjects.add(itemProf);
						}
						
					}

					objProf.setMecanicasProfesion(tempMecanicas);
					objProf.setObjetosProfesion(tempObjects);
					
					listaInterfacesProfesiones.add(objProf);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Método para cargar los datos de un .yml en el hashmap correspondiente
	public boolean cargarDatos(Player p) {
		try {
			List<Double> tempPuntosProfesion = new ArrayList<Double>();
			List<Boolean> tempLock = new ArrayList<Boolean>();
			Integer tempRestantes = 0;
			
			File profesionesPlayer = new File("plugins/GothicJobs/playerdata/Profesiones"+p.getName()+".yml");
			
			playerYaml = new YamlConfiguration();
			playerYaml.load(profesionesPlayer);
			
			tempLock = getPlayerConfig().getBooleanList("profesiones-lockeadas");
			tempPuntosProfesion = getPlayerConfig().getDoubleList("puntos-profesiones");
			tempRestantes = getPlayerConfig().getInt("puntos-restantes");
			
			Boolean[] tempArrayLock = new Boolean[tempLock.size()];
			Double[] tempArrayPuntos = new Double[tempPuntosProfesion.size()];
			
			for (int i = 0; i < tempArrayLock.length; i++) {
				tempArrayLock[i] = tempLock.get(i);
				tempArrayPuntos[i] = tempPuntosProfesion.get(i);
			}

			puntosProfesionRestantes.put(p,tempRestantes);
			profesionesLockeadas.put(p, tempArrayLock);
			puntosProfesiones.put(p, tempArrayPuntos);
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean guardarDatos(Player p) throws InvalidConfigurationException {
		
		try {
			Double[] tempPuntosProfesion = puntosProfesiones.get(p);
			Boolean[] tempLock = profesionesLockeadas.get(p);
			Integer tempRestantes = puntosProfesionRestantes.get(p);
			
			//Crea un writer y coge la ruta del archivo para guardarlo después
			File profesionesPlayer = new File("plugins/GothicJobs/playerdata/Profesiones"+p.getName()+".yml");
			
			playerYaml = new YamlConfiguration();
			playerYaml.load(profesionesPlayer);
			
			Yaml dataYaml = new Yaml();
			FileWriter writer = new FileWriter(profesionesPlayer);
			getPlayerConfig().set("nombre", p.getName());
			getPlayerConfig().set("puntos-restantes", tempRestantes);
			getPlayerConfig().set("profesiones-lockeadas", tempLock);
			getPlayerConfig().set("puntos-profesiones", tempPuntosProfesion);
			getPlayerConfig().save(profesionesPlayer);
			
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Método que devuelve cuántos puntos de profesión disponible tiene un jugador
	public Integer getPuntosProfesionRestantes(Player p) {
		return puntosProfesionRestantes.get(p);
	}
	
	//Método que devuelve cuántos puntos tiene cada una de las profesiones
	public Double[] getPuntosProfesiones(Player p) {
		return puntosProfesiones.get(p);
	}
	
	//Método que devuelve la lista de profesiones lockeadas y no lockeadas
	public Boolean[] getProfesionesLockeadas(Player p) {
		return profesionesLockeadas.get(p);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	//Método para establecer un .yml como archivo de configuración para acceder a el
	public FileConfiguration getPlayerConfig() {
		return this.playerYaml;
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}
}
