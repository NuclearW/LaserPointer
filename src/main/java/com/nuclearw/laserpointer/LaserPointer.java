package com.nuclearw.laserpointer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LaserPointer extends JavaPlugin {
	static String mainDirectory = "plugins" + File.separator + "LaserPointer";
	static File versionFile = new File(mainDirectory + File.separator + "VERSION");
	static File languageFile = new File(mainDirectory + File.separator + "lang");

	private final LaserPointerPlayerListener playerListener = new LaserPointerPlayerListener(this);
	private final LaserPointerBlockListener blockListener = new LaserPointerBlockListener(this);

	public HashMap<Player, Location> pointer = new HashMap<Player, Location>();
	
	Logger log = Logger.getLogger("Minecraft");
	Properties prop = new Properties();
	
	public String[] language = new String[4];
	
	public void onEnable() {
		new File(mainDirectory).mkdir();
		
		if(!versionFile.exists()) {
			updateVersion();
		} else {
			String vnum = readVersion();
			if(vnum.equalsIgnoreCase("0.1")) updateVersion();
		}
		
		if(!languageFile.exists()) tryMakeLangFile();
		
		tryLoadLangFile();
		
		if(!prop.containsKey("no-permission") || !prop.containsKey("laser-on") || !prop.containsKey("laser-off")
				|| !prop.containsKey("not-player")) {
			this.log.severe("[LaserPointer] Lang file not complete! Restoring to default!");
			tryMakeLangFile();
			tryLoadLangFile();
		}

		this.language[0] = this.colorizeText(prop.getProperty("no-permission"));
		this.language[1] = this.colorizeText(prop.getProperty("laser-on"));
		this.language[2] = this.colorizeText(prop.getProperty("laser-off"));
		this.language[3] = this.colorizeText(prop.getProperty("not-player"));
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);
		
		log.info("[LaserPointer] Version "+this.getDescription().getVersion()+" enabled.");
	}
	
	public void onDisable() {
		log.info("[LaserPointer] Version "+this.getDescription().getVersion()+" disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("point")) {
			if(!isPlayer(sender)) {
				sender.sendMessage(this.language[3]);
				return true;
			}
			//Permission check
			if(!((Player) sender).hasPermission("laserpointer.point")) {
				sender.sendMessage(this.language[0]);
			}
			//Turning off
			if(this.pointer.containsKey((Player) sender)) {
				this.pointer.get((Player) sender).getBlock().setTypeId(0);
				this.pointer.remove((Player) sender);
				sender.sendMessage(this.language[2]);
				return true;
			}
			//Turning on
			else {
				Block tBlock = ((Player) sender).getLastTwoTargetBlocks(null, 500).get(0);
				if(tBlock.getTypeId() == 0) tBlock.setTypeIdAndData(35, (byte) 0xE, false);
				this.pointer.put((Player) sender, tBlock.getLocation());
				sender.sendMessage(this.language[1]);
				return true;
			}
		}
		return true;
	}
	
	public void tryLoadLangFile() {
		FileInputStream langin;
		try {
			langin = new FileInputStream(languageFile);
			this.prop.load(langin);
			langin.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void tryMakeLangFile() {
		try {
			languageFile.createNewFile();
			FileOutputStream out = new FileOutputStream(languageFile);
			this.prop.put("no-permission", "&cInsuffficient permissoins.");
			this.prop.put("laser-on", "&4Laser &apointer turned on.");
			this.prop.put("laser-off", "&4Laser &apointer turned off.");
			this.prop.put("not-player", "&cYou must be a player to use this command.");
			this.prop.store(out, "Loaclization.");
			out.flush();
			out.close();
			this.prop.clear();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void updateVersion() {
		try {
			versionFile.createNewFile();
			BufferedWriter vout = new BufferedWriter(new FileWriter(versionFile));
			vout.write(this.getDescription().getVersion());
			vout.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		}
	}

	public String readVersion() {
		byte[] buffer = new byte[(int) versionFile.length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(versionFile));
			f.read(buffer);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (IOException ignored) { }
		}
		
		return new String(buffer);
	}
	
    public boolean isPlayer(CommandSender sender) {
        return sender != null && sender instanceof Player;
    }
    
    public String colorizeText(String string) {
    	string = string.replaceAll("&0", ChatColor.BLACK+"");
		string = string.replaceAll("&1", ChatColor.DARK_BLUE+"");
		string = string.replaceAll("&2", ChatColor.DARK_GREEN+"");
		string = string.replaceAll("&3", ChatColor.DARK_AQUA+"");
		string = string.replaceAll("&4", ChatColor.DARK_RED+"");
		string = string.replaceAll("&5", ChatColor.DARK_PURPLE+"");
		string = string.replaceAll("&6", ChatColor.GOLD+"");
		string = string.replaceAll("&7", ChatColor.GRAY+"");
		string = string.replaceAll("&8", ChatColor.DARK_GRAY+"");
		string = string.replaceAll("&9", ChatColor.BLUE+"");
		string = string.replaceAll("&a", ChatColor.GREEN+"");
		string = string.replaceAll("&b", ChatColor.AQUA+"");
		string = string.replaceAll("&c", ChatColor.RED+"");
		string = string.replaceAll("&d", ChatColor.LIGHT_PURPLE+"");
		string = string.replaceAll("&e", ChatColor.YELLOW+"");
		string = string.replaceAll("&f", ChatColor.WHITE+"");
		return string;
    }
}
