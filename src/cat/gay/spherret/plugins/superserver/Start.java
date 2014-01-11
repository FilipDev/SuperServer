package cat.gay.spherret.plugins.superserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R1.*;

import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.server.v1_7_R1.*;

public class Start extends JavaPlugin implements Listener{

	final static HashMap<World, Long> timelock = new HashMap<World, Long>();
	final static HashMap<String, Boolean> muted = new HashMap<String, Boolean>();
	final static HashMap<Player, ArrayList<Block>> explode = new HashMap<Player, ArrayList<Block>>();
	final static HashMap<Player, ArrayList<Block>> lightning = new HashMap<Player, ArrayList<Block>>();
	final static HashMap<Player, ArrayList<Block>> flying = new HashMap<Player, ArrayList<Block>>();
	final static HashMap<Player, ArrayList<Block>> ib = new HashMap<Player, ArrayList<Block>>();
	final static HashMap<String, HashMap<String, Boolean>> Attributes = new HashMap<String, HashMap<String, Boolean>>();
	final static HashMap<String, Boolean> Settings = new HashMap<String, Boolean>();
	final static HashMap<String, Boolean> Configs = new HashMap<String, Boolean>();
	final static HashMap<String, Double> distance = new HashMap<String, Double>();
	final static HashMap<String, Integer> vanished = new HashMap<String, Integer>();
	final static HashMap<Location, Location> stp = new HashMap<Location, Location>();
	final static HashMap<String, Integer> damagetime = new HashMap<String, Integer>();
	final static HashMap<String, ArrayList<String>> ignore = new HashMap<String, ArrayList<String>>();
	final static HashMap<String, Integer> ips = new HashMap<String, Integer>();
	final static HashMap<String, Location> back = new HashMap<String, Location>();
	final static HashMap<String, Material> type = new HashMap<String, Material>();
	final static HashMap<String, Boolean> afk = new HashMap<String, Boolean>();
	final static HashMap<String, Boolean> gimped = new HashMap<String, Boolean>();
	final static HashMap<String, Entity> entityselected = new HashMap<String, Entity>();
	final static HashMap<Player, Location> pos1 = new HashMap<Player, Location>();
	final static HashMap<Player, Location> pos2 = new HashMap<Player, Location>();
	final static HashMap<String, String> lastChat = new HashMap<String, String>();
	final static HashMap<String, List<String>> blocked = new HashMap<String, List<String>>();
	public static String motda = "A Minecraft Server";
	public static boolean vanilla = true;
	public static boolean survival = true;
	boolean willwarp = true;
	boolean willhome = true;
	
	List<Entity> highlighted = new ArrayList<Entity>();
	
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(new Events(), this);
		vanilla = true;
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "----------------------");
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "|SuperServer Enabled!|");
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "|Author: swampshark19|");
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "|      Enjoy! :D     |");
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "-`---------------------");
		this.saveDefaultConfig();
		registerConfig();
		for (Player player : Bukkit.getOnlinePlayers()) {
			distance.put(player.getName(), player.getLocation().getX() + player.getLocation().getZ());
		}
		Settings.put("Trampoline", false);
		Settings.put("Physics", false);
		Settings.put("TreeFelling", true);
		for (Player player : Bukkit.getOnlinePlayers()){
			gimped.put(player.getName(), false);
		}
		Bukkit.dispatchCommand(getServer().getConsoleSender(), "unlock");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				Bukkit.broadcastMessage("§7[§8SS§7] §cClearing drops to prevent lag.");
				List<Entity> dropList = Methods.filterEntity(EntityType.DROPPED_ITEM);
				for (Entity drop : dropList){
					drop.remove();
				}
				Bukkit.broadcastMessage("§7[§8SS§7] §aDrops cleared.");
			}
		}
				, 15 * 20,  15 * 60 * 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				Bukkit.getConsoleSender().sendMessage("§7[§8SS§7] §eSaving worlds.");
				Chunk[] chunks = {};
				for (World w : Bukkit.getWorlds()){
					w.save();
					chunks = (Chunk[]) ArrayUtils.add(chunks, w.getLoadedChunks());
				}
				Bukkit.getConsoleSender().sendMessage("§7[§8SS§7] §aWorlds saved.");
			}
		}
				, 20 * 20,  10 * 60 * 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				for (World world : timelock.keySet()){
					world.setTime(timelock.get(world));
				}
			}
		}
				, 20 * 20,  20 * 20);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				Bukkit.broadcastMessage("§7[§8SS§7] §bClearing drops in 15 seconds.");
			}
		}
				, 0,  7 * 60 * 20);
	}
	
	public void onDisable(){
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Saving worlds...");
		for (World world : Bukkit.getWorlds()){
			world.save();
		}
		System.out.println("" + highlighted.size());
		for (Entity e : highlighted){
			Methods.addEntityNBTString(e, "CustomName", " ");
		}
		saveConfig();
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SuperServer Disabled.");
		Bukkit.getScheduler().cancelAllTasks();
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args){
		if (label.equalsIgnoreCase("vanilla") && (!(sender instanceof Player))){
			if (!vanilla){
				vanilla = true;
				Bukkit.broadcastMessage("§cvanilla = " + vanilla);
				return true;
			}
			vanilla = false;
			Bukkit.broadcastMessage("§cvanilla = " + vanilla);
			return true;
		}

		if (label.equalsIgnoreCase("unlock")){
		if (!(sender instanceof Player)){
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "[SuperServer] Unlocked.");
			vanilla = false;
			return true;
		}
		if (sender.isOp()){
			sender.sendMessage("Unlocked.");
			vanilla = false;
			return true;
		}
		}
		
		if (vanilla ){
			if (sender instanceof Player){
				((Player) sender).getPlayer().sendMessage("Unknown command. Type \"help\" for help.");
			}
			return true;
		}
				
		if (label.equalsIgnoreCase("deleteworld")){
			for (Player player : Bukkit.getOnlinePlayers()){
				if (player.getWorld().getName().equals(args[0])){
					player.teleport(new Location(Bukkit.getWorld("world"), Bukkit.getWorld("world").getSpawnLocation().getX(), Bukkit.getWorld("world").getSpawnLocation().getY(), Bukkit.getWorld("world").getSpawnLocation().getZ()));
				}
			}
			Bukkit.unloadWorld(args[0], false);
			File  file = new File(Bukkit.getWorldContainer() + args[0]);
			file.delete();
			return true;
		}
				

		if (label.equalsIgnoreCase("worlds")){
			if (sender instanceof Player){
				for (World world : Bukkit.getWorlds()){
					sender.sendMessage(world.getName());
				}
				return true;
			}
			System.out.println("" + Bukkit.getWorlds());
			return true;
		}
		if (!(sender instanceof Player)){
			if (label.equalsIgnoreCase("getConfigs")){
				this.saveConfig();

			}
			return true;
		}

		if (label.equalsIgnoreCase("op")){
			if (sender instanceof Player)
				return true;
			try{
				Bukkit.getPlayer(args[0]).setOp(true);
				Bukkit.getConsoleSender().sendMessage("Player " + args[0] + " opped.");
				Bukkit.getPlayer(args[0]).sendMessage("§eYou are now op.");
			}catch (Exception e){
				System.out.println("Argument must be a valid player.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("deop")){
			if (sender instanceof Player)
				return true;
			try{
				Bukkit.getPlayer(args[0]).setOp(false);
				Bukkit.getConsoleSender().sendMessage("Player " + args[0] + " deopped.");
				Bukkit.getPlayer(args[0]).sendMessage("§eYou are no longer op.");
			}catch (Exception e){
				System.out.println("Argument must be a valid player.");
			}
			return true;
		}
		if (!(sender instanceof Player)){
			if (label.equalsIgnoreCase("enabled")){
				try{
				getConfig().set(args[0], args[1]);
				registerConfig();
				}catch(Exception e){
					System.out.println("/enabled [item] [boolean]");
				}
				return true;
				
			}
		}
		if (!(sender instanceof Player)){
			if (label.equalsIgnoreCase("createworld")){
				if (args.length < 1){
					System.out.println("§cFirst argument must be the world name, no spaces.");
					System.out.println("§cSecond argument must be the world generator (FLAT, LARGE_BIOMES, NORMAL, VERSION_1_1?).");
					System.out.println("§cThird argument must be the environment type (THE_END, NETHER, NORMAL)");
					System.out.println("§cFourth argument can be the world seed, no spaces.");
					return true;
				}
		        WorldCreator wc = new WorldCreator(args[0]);
		        wc.type(WorldType.valueOf(args[1].toUpperCase()));
		        wc.environment(Environment.valueOf(args[2].toUpperCase()));
		        if (args.length < 4){
					long num = (long) (Math.random() * Math.pow(10, 13));
					wc.seed(num);
			        wc.createWorld();
			        Bukkit.broadcastMessage("world " + wc.name() + " created.");
			        return true;
		        }
		        wc.seed(Long.parseLong(args[3]));
		        wc.createWorld();
		        Bukkit.broadcastMessage("world " + wc.name() + " created.");
		        return true;
			}
		} 

		if (!(sender instanceof Player)){
			if (label.equalsIgnoreCase("stop")){
				Bukkit.shutdown();
			}
		}
		
		if (!(sender instanceof Player)){
			System.out.println("This command cannot be run from console.");
		}
		
		//PLAYER BASED COMMANDS
		final Player p = (Player) sender;
		org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer nmsPlayerType = (org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer) p;
		final PlayerConnection pc = nmsPlayerType.getHandle().playerConnection;
		EntityPlayer nmsp = nmsPlayerType.getHandle();
		if (label.equalsIgnoreCase("killall") && p.hasPermission("superserver.*")){
			int amt = 0;
			for (Entity e : p.getNearbyEntities(250, 250, 250)){
				amt = amt + 1;
				if (!e.getType().equals(EntityType.PLAYER)){
					e.remove();
				}
			}
			p.sendMessage(amt + " entities killed.");
			return true;
		}
		if (label.equalsIgnoreCase("c") && p.hasPermission("superserver.gamemode")){
			p.setGameMode(GameMode.CREATIVE);
			return true;
		}
		if (label.equalsIgnoreCase("a") && p.hasPermission("superserver.gamemode")){
			p.setGameMode(GameMode.ADVENTURE);
			return true;
		}
		if (label.equalsIgnoreCase("s") && p.hasPermission("superserver.gamemode")){
			p.setGameMode(GameMode.SURVIVAL);
			return true;
		}
		if (label.equalsIgnoreCase("sethome") && p.hasPermission("superserver.sethome")){
			setHome(p);
			return true;
		}
		if (label.equalsIgnoreCase("drop") && p.hasPermission("superserver.*")){
			try{
				Player t = Bukkit.getPlayer(args[0]);
				ItemStack[] inv = t.getInventory().getContents();
				for (ItemStack item : inv){
					t.getWorld().dropItemNaturally(t.getLocation(), item);
				}
				t.getInventory().clear();
			}catch (Exception e){
				p.sendMessage("§cIncorrect Usage: /drop <player>");
			}
		}
		if (label.equalsIgnoreCase("ptime") && p.hasPermission("superserver.ptime")){
			try{
				p.setPlayerTime(Long.getLong(args[0]), true);
			}catch (Exception e){
				if (args[0].equalsIgnoreCase("reset")){
					p.setPlayerTime(p.getWorld().getTime(), true);
					return true;
				}
				p.sendMessage("§cIncorrect Usage: /ptime <long time> or /ptime reset");
			}
		}
		if (label.equalsIgnoreCase("pweather") && p.hasPermission("superserver.pweather")){
			try{
				p.setPlayerWeather(WeatherType.valueOf(args[0]));
			}catch (Exception e){
				if (args[0].equalsIgnoreCase("reset")){
					if (p.getWorld().hasStorm())
						p.setPlayerWeather(WeatherType.DOWNFALL);
					else
						p.setPlayerWeather(WeatherType.CLEAR);
				}else{
					p.sendMessage("§cIncorrect Usage: /pweather CLEAR or /pweather DOWNFALL - CAPTIAL LETTERS REQUIRED");
				}
			}
		}
		if (label.equalsIgnoreCase("timelock") && p.hasPermission("superserver.timelock")){
			timelock.put(p.getWorld(), p.getWorld().getTime());
			p.sendMessage("§aWorld time locked to " + p.getWorld().getTime());
			return true;
		}
		if (label.equalsIgnoreCase("edestroy")){
			try{
				Player t = Bukkit.getPlayer(args[0]);
				t.remove();
			}catch (Exception e){
				p.sendMessage("§cIncorrect Usage: /edestroy <player>");
			}
		}
		if (label.equalsIgnoreCase("justjoined")){
			String ip = "";
			for (Byte ipsection : p.getAddress().getAddress().getAddress()){
				ip = ip + ipsection + ".";
			}
			int amountOfIps = 0;
			try{
				amountOfIps = getConfig().getList("iplist." + p.getName()).size();
			} catch (NullPointerException e){
				getConfig().set("iplist." + p.getName() + "." + 1, ip);
			}
			getConfig().set("iplist." + p.getName() + "." + amountOfIps + 1, ip);
			return true;
			
		}
		if (label.equalsIgnoreCase("tree") && p.hasPermission("superserver.*")){
			Block bb = p.getTargetBlock(null, 256);
			args[0] = args[0].toUpperCase();
			p.getWorld().generateTree(bb.getLocation(), TreeType.valueOf(args[0]));
			p.getWorld().refreshChunk(bb.getLocation().getChunk().getX(), bb.getLocation().getChunk().getZ());
			return true;
		}
		if (label.equalsIgnoreCase("sudo") && p.hasPermission("superserver.*")){
			Player t = Bukkit.getPlayer(args[0]);
			String message = "";
			int x = 1;
			while (x < args.length){
				args[0] = args[x].replaceAll("&", "§");
				message = message + args[x] + " ";
				x = x + 1;
			}
			t.chat(message);
			return true;
		}
		if (label.equalsIgnoreCase("openinv") && p.hasPermission("superserver.openinv")){
			Player t = Bukkit.getPlayerExact(args[0]);
			Inventory inventory = t.getInventory();
			p.openInventory(inventory);
			return true;
		}
		if (label.equalsIgnoreCase("block")){
			String target = "";
			try{
				target = args[0];
			}catch(Exception e){
				p.sendMessage("§cCorrect Usage: /block <player>");
			}
			try{
			List<String> blockedList = Start.blocked.get(p.getName());
			blockedList.add(target);
			Start.blocked.put(p.getName(), blockedList);
			}catch(Exception e){
				List<String> blockedList = new ArrayList<String>();
				blockedList.add(target);
				Start.blocked.put(p.getName(), blockedList);
			}
			return true;
		}
		if (label.equalsIgnoreCase("unblock")){
			String target = "";
			try{
				target = args[0];
			}catch(Exception e){
				p.sendMessage("§cCorrect Usage: /unblock <player>");
			}
			try{
				List<String> blockedList = Start.blocked.get(p.getName());
				blockedList.remove(target);
				Start.blocked.put(p.getName(), blockedList);
			}catch(Exception e){
				p.sendMessage("§cPlayer was not blocked.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("blocked")){
			try{
				p.sendMessage("§3Players which are " + "§6blocked:");
				for (String player : Start.blocked.get(p.getName())){
					p.sendMessage(player);
				}
			}catch(Exception e){
				p.sendMessage("§cYou have no players blocked.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("home")){
			final int x = p.getLocation().getBlockX();
			final int y = p.getLocation().getBlockY();
			final int z = p.getLocation().getBlockZ();
			willhome = true;
			p.sendMessage("Don't move for 5 seconds.");
			Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable(){
				public void run(){
					int nx = p.getLocation().getBlockX();
					int ny = p.getLocation().getBlockY();
					int nz = p.getLocation().getBlockZ();
					Integer move = (nx - x) + (ny - y) + (nz - z);
					if (move != 0){
						willhome = false;
					}
					if (!willhome){
						p.sendMessage("§cYou moved! Teleport cancelled.");
						return;
					}
					try
					{
					gotoHome(p);
					}
					catch(Exception e)
					{
						p.sendMessage("You haven't set a home yet!");
					}
				}
			}, 100L);
			return true;
		}
		
		if (label.equalsIgnoreCase("setwarp") && p.hasPermission("superserver.setwarp")){
			if (args.length < 1){
				p.sendMessage("§cFirst argument must be warp name.");
				return true;
			}
			setWarp(p, args[0]);
			return true;
		}
		if (label.equalsIgnoreCase("delwarp") && p.hasPermission("superserver.setwarp")){
			if (args.length < 1){
				return true;
			}
			boolean worked = delWarp(args[0]);
			if (!worked){
				p.sendMessage("§cWarp " + args[0] + " not found. Warp is CaSe SeNsItIvE.");
			}
			p.sendMessage("§aDeletion sucessful.");
			return true;
		}
		if (label.equalsIgnoreCase("warp") && p.hasPermission("superserver.warp")){
			if (args.length < 1){
				p.sendMessage("chicken.");
				for (String warpName : getConfig().getStringList("warps")){
					p.sendMessage(warpName + "");
				}
				return true;
			}
			final int x = p.getLocation().getBlockX();
			final int y = p.getLocation().getBlockY();
			final int z = p.getLocation().getBlockZ();
			willwarp = true;
			if (!doesWarpExist(args[0])){
				p.sendMessage("§cWarp nonexistant.");
				return true;
			}
			p.sendMessage("" + doesWarpExist(args[0]));
			p.sendMessage("§eDon't move for 5 seconds.");
			Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable(){
				public void run(){
					int nx = p.getLocation().getBlockX();
					int ny = p.getLocation().getBlockY();
					int nz = p.getLocation().getBlockZ();
					Integer move = (nx - x) + (ny - y) + (nz - z);
					if (move != 0){
						willwarp = false;
					}
					if (!willwarp){
						p.sendMessage("§cYou moved! Teleport cancelled.");
						return;
					}
					gotoWarp(p, args[0]);
				}
			}, 100L);
			return true;
		}
		
		if (label.equalsIgnoreCase("back") && p.hasPermission("superserver.back")){
			if (!("" + back).contains(p.getName())){
				p.sendMessage("§cYou haven't died yet...");
				return true;
			}
			p.teleport(back.get(p.getName()));
			back.remove(p.getName());
			return true;
		}
		if (label.equalsIgnoreCase("stop")){
			if (!(sender instanceof Player)){
				Bukkit.shutdown();
				return true;
			}
			p.sendMessage("§cCommand must be executed from console.");
			return true;
		}
		
		if (label.equalsIgnoreCase("createworld") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage("§cFirst argument must be the world name, no spaces.");
				p.sendMessage("§cSecond argument must be the world generator (FLAT, LARGE_BIOMES, NORMAL, VERSION_1_1?).");
				p.sendMessage("§cThird argument must be the environment type (THE_END, NETHER, NORMAL)");
				p.sendMessage("§cFourth argument can be the world seed, no spaces.");
				return true;
			}
	        WorldCreator wc = new WorldCreator(args[0]);
	        wc.type(WorldType.valueOf(args[1].toUpperCase()));
	        wc.environment(Environment.valueOf(args[2].toUpperCase()));
	        if (args.length < 4){
				long num = (long) (((((((((((((Math.random() * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10);
				wc.seed(num);
		        wc.createWorld();
		        Bukkit.broadcastMessage("world " + wc.name() + " created.");
		        return true;
	        }
	        wc.seed(Long.parseLong(args[3]));
	        wc.createWorld();
	        Bukkit.broadcastMessage("world " + wc.name() + " created.");
	        return true;
		}
		if (label.equalsIgnoreCase("up")){
			if (args.length < 1){
				return true;
			}
			p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + Long.parseLong(args[0]), p.getLocation().getZ()));
			p.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.GLASS);
			return true;
		}
		if (label.equalsIgnoreCase("down")){
			if (args.length < 1){
				return true;
			}
			p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - Integer.parseInt(args[0]), p.getLocation().getZ()));
			return true;
		}

		if (label.equalsIgnoreCase("rtd") && (p.hasPermission("superserver.rtd") || p.hasPermission("superserver.*"))){
			try{
			if (getConfig().getLong("rtd." + p.getName()) - Bukkit.getWorld("world").getFullTime() > 72000){
				p.sendMessage("§cCannot use rtd for another " + (((p.getWorld().getFullTime() - getConfig().getLong("rtd." + p.getName()) - 72000) / 20 * -1) / 60) + " minutes");
				return true;
			}
			}
			catch (NullPointerException e){}
			Random randGen = new Random();
			int randNum = randGen.nextInt(13);
			p.sendMessage("§eYou rolled a " + randNum);
			if (randNum == 12){
				int randNum2 = randGen.nextInt(2);
				if (randNum2 == 12){
					p.sendMessage("§1One time use SUPERSWORD!");
					ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
					item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 127);
					item.setDurability((short) 1561);
					item.getItemMeta().setDisplayName("§1Super Sword");
					p.getInventory().addItem(item);
					return true;
				}
				p.sendMessage("No supersword this time... But you get something else!");
				Random randGen3 = new Random();
				randNum = randGen3.nextInt(12);
				p.sendMessage("§eYou rolled a " + randNum);
			}
			if (randNum == 11){
				p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 3600, 1));
				p.sendMessage("§9FREE ULTRA AIR SUPREME!");
			}
			if (randNum == 10){
				p.giveExpLevels(5);
				p.sendMessage("§dEXPERIENCE LEVELS!!!");
			}
			if (randNum == 9){
				Methods.addTreeFellEnchant(p);
				p.sendMessage("§5You get a tree felling axe, §nuse it well!");
			}
			if (randNum == 8){
				p.setHealth(20);
				p.setSaturation(45);
				p.setFoodLevel(20);
				p.sendMessage("§aYou have been 1-upped!");
			}
			if (randNum == 7){
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3600, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 5));
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3600, 3));
				p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 3600, 1));
				p.sendMessage("§7Crystal Meth!");
			}
			if (randNum == 1){
				p.sendMessage("§8Invincibility!");
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 19));
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 0));
			}
			if (randNum == 6){
				p.removePotionEffect(PotionEffectType.BLINDNESS);
				p.removePotionEffect(PotionEffectType.CONFUSION);
				p.removePotionEffect(PotionEffectType.HUNGER);
				p.removePotionEffect(PotionEffectType.POISON);
				p.removePotionEffect(PotionEffectType.SLOW);
				p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				p.removePotionEffect(PotionEffectType.WEAKNESS);
				p.sendMessage("§b§oI'm feeling better already! Thanks doc!");
			}
			if (randNum == 5){
				p.sendMessage("§n§3You get a random, normally unobtainable potion!");
				Random randGen5 = new Random();
				int randNum5 = randGen5.nextInt(9);
				ItemStack item = new ItemStack(Material.POTION, 1);
				if (randNum5 == 0){
					item.setDurability((byte) 7);
				}
				if (randNum5 == 1){
					item.setDurability((byte) 11);
				}
				if (randNum5 == 2){
					item.setDurability((byte) 13);
				}
				if (randNum5 == 3){
					item.setDurability((byte) 15);
				}
				if (randNum5 == 4){
					item.setDurability((byte) 23);
				}
				if (randNum5 == 5){
					item.setDurability((byte) 27);
				}
				if (randNum5 == 6){
					item.setDurability((byte) 29);
				}
				if (randNum5 == 7){
					item.setDurability((byte) 31);
				}
				if (randNum5 == 8){
					item.setDurability((byte) 39);
				}
				p.getInventory().addItem(item);
			}
			if (randNum == 4){
				p.setFireTicks(100);
				p.getWorld().strikeLightningEffect(p.getLocation());
				p.sendMessage("§eYOU HAVE BEEN SMITED!");
			}
			if (randNum == 2){
				int players = Bukkit.getOnlinePlayers().length;
				Random randGen4 = new Random();
				int randNum4 = randGen4.nextInt(players);
				p.teleport(Bukkit.getOnlinePlayers()[randNum4].getLocation());
				p.sendMessage("§7You have been teleported to a random player...");
			}
			if (randNum == 3){
				p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 600, 2));
				p.sendMessage("§2I don't feel so good...");
			}
			if (randNum == 0){
				Random randGen2 = new Random();
				int randNum2 = randGen2.nextInt(3);
				if (randNum2 == 0 || randNum2 == 2){
					p.setHealth(0);
					p.sendMessage("§4The power of the dice compells you.");
					p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 10, 0);
				}
				if (randNum2 == 1){
					p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 10, -1);
					p.playSound(p.getLocation(), Sound.ENDERMAN_SCREAM, 10, 0);
					p.playSound(p.getLocation(), Sound.ENDERMAN_STARE, 10, 4);
					p.sendMessage("§4Death has been prevented, next time you won't be so lucky...");
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 260, 4));
					p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 260, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 4));
					p.setHealth(1);
				}
			}
			getConfig().set("rtd." + p.getName(), Bukkit.getWorld("world").getFullTime());
	        return true;
		}
		if (label.equalsIgnoreCase("settype") && p.hasPermission("superserver.*")){
			args[0] = args[0].toUpperCase();
			try
			{
			Material material = Material.valueOf(args[0]);
			if (!material.isBlock()){
				p.sendMessage("§cMaterial " + material + " is not type block.");
				return true;
			}
			type.put(p.getName(), material);
			p.sendMessage("Material set to " + args[0]);
			return true;
			}
			catch (IllegalArgumentException e)
			{
				p.sendMessage("§cMaterial " +args[0] + " not found.");
				return true;
			}
		}
		if (label.equalsIgnoreCase("dispose") && p.hasPermission("superserver.*")){
			pc.sendPacket(new PacketPlayOutNamedEntitySpawn(nmsp));
			return true;
		}
		if (label.equalsIgnoreCase("oplist")){
			p.sendMessage("§3Operators (" + Bukkit.getOperators().size() + "):");
			for (OfflinePlayer ops : Bukkit.getOperators()){
				p.sendMessage("" + ops.getName());
			}
			return true;
		}
		if (label.equalsIgnoreCase("repair") && p.hasPermission("superserver.*")){
			p.getItemInHand().setDurability((short) 0);
	        return true;
		}
		if (label.equalsIgnoreCase("block")){
			List<String> ignored;
			try{
				ignored = ignore.get(p.getName());
			} catch (NullPointerException e){
				ignored = new ArrayList<String>();
			}
			ignored.add(args[0]);
			ignore.put(p.getName(), (ArrayList<String>) ignored);
			p.sendMessage("§8[§7SS&8] §6&lYou cannot see " + args[0] + "'s messages anymore.");
	        return true;
		}
		if (label.equalsIgnoreCase("unblock")){
			ArrayList<String> ignored = ignore.get(p.getName());
			int names = -1;
			for (String name : ignored){
				names++;
				if (name.equals(args[0]))
					ignored.remove(names);
			}
			ignore.put(p.getName(), ignored);
			p.sendMessage("§8[§7SS&8] §3&lYou are no longer blocking " + args[0] + "'s messages anymore.");
	        return true;
		}
		
		
		if (label.equalsIgnoreCase("enabled") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				Bukkit.broadcastMessage("§cIncorrect usage. /enabled <feature>");
				return true;
			}
			try{
				getConfig().set(args[0], args[1]);
				registerConfig();
			}catch (Exception e){
				p.sendMessage("§c/enabled [setting] [boolean]");
			}
	        return true;
			
		}
		if (label.equalsIgnoreCase("getConfigs") && p.hasPermission("superserver.*")){
			this.saveConfig();
			String message = "";
			for (String keys : Configs.keySet()){
				message = message + (keys + " is set to " + Configs.get("keys") + "\n");
			}
			message = message.replaceAll("true", "§atrue");
			message = message.replaceAll("false", "§cfalse");
			p.sendMessage(message);
			return true;
		}
		if (label.equalsIgnoreCase("getid")){
			p.sendMessage("" + p.getItemInHand().getTypeId());
			return true;
		}
		if (label.equalsIgnoreCase("setspawn") && p.hasPermission("superserver.*")){
			Location loc = p.getLocation();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			World world = p.getWorld();
			world.setSpawnLocation(x, y, z);
			p.sendMessage("Spawn set to " + x + " " + y + " " + z);
	        return true;
		}
		if (label.equals("entity") && p.hasPermission("superserver.entitymanip")){
			if (args.length < 1){
				p.sendMessage("Incorrect usage, /entity [action] [entity] [amount]");
				return true;
			}
			if (args[0].equalsIgnoreCase("highlight")){
				final Entity e = entityselected.get(p.getName());
				Methods.addEntityNBTString(e, "CustomName", "HIGHLIGHTED");
				highlighted.add(e);
			}
			if (args[0].equalsIgnoreCase("clone")){
				final Entity e = entityselected.get(p.getName());
				p.getWorld().spawnEntity(e.getLocation(), e.getType());
				return true;
			}
			if (args[0].equalsIgnoreCase("eject")){
				try{
				p.sendMessage("Ejected from " + p.getVehicle().getType());
				}
				catch (NullPointerException error){
					p.sendMessage("You aren't in a vehicle!");
				}
				p.setPassenger(p);
				p.eject();
				return true;
			}
			if (args[0].equalsIgnoreCase("teleport")){
				try{
					entityselected.get(p.getName()).teleport(p);
				}
				catch (NullPointerException error){
					p.sendMessage("§cYou must select an entity first.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("ride")){
				try{
					entityselected.get(p.getName()).setPassenger(p);
				}
				catch (NullPointerException error){
					p.sendMessage("§cYou must select an entity first.");
				}
			}
			if (args[0].equalsIgnoreCase("select")){
				HashSet<Byte> transparent = new HashSet<Byte>();
				transparent.add((byte)Material.SNOW.getId());
				transparent.add((byte)Material.VINE.getId());
				transparent.add((byte)Material.AIR.getId());
				Entity ed = p.getWorld().spawnEntity(p.getTargetBlock(transparent, 256).getLocation(), EntityType.ARROW);
				Entity e = ed.getNearbyEntities(15, 15, 15).get(0);
				ed.remove();
				p.sendMessage(e.getType() + " selected at " + e.getLocation().getBlockX() + " " + e.getLocation().getBlockY() + " " + e.getLocation().getBlockZ());
				entityselected.put(p.getName(), e);
			}
			if (args[0].equalsIgnoreCase("nbtsetbyte")){
				p.sendMessage("" + entityselected.get(p.getName()));
				Methods.addEntityNBTByte(entityselected.get(p.getName()), args, p);
			}
			if (args[0].equalsIgnoreCase("nbtsetint")){
				p.sendMessage("" + entityselected.get(p.getName()));
				Methods.addEntityNBTInt(entityselected.get(p.getName()), args, p);
			}
			if (args[0].equalsIgnoreCase("nbtsetshort")){
				p.sendMessage("" + entityselected.get(p.getName()));
				Methods.addEntityNBTShort(entityselected.get(p.getName()), args, p);
			}
			if (args[0].equalsIgnoreCase("nbtsetstring")){
				p.sendMessage("" + entityselected.get(p.getName()));
				Methods.addEntityNBTString(entityselected.get(p.getName()), args[1], args[2]);
			}
			if (args[0].equalsIgnoreCase("nbtget")){
				try{
				Methods.getEntityNBT(entityselected.get(p.getName()), args, p);
				}
				catch (NullPointerException error){
					p.sendMessage("§cYou must select an entity first.");
				}
			}
			if (args[0].equalsIgnoreCase("destroy")){
				try{
					entityselected.get(p.getName()).remove();
				}
				catch (NullPointerException error){
					args[1] = args[1].toUpperCase();
					EntityType et = EntityType.PIG;
					int x = 1;
					try{
					et = EntityType.valueOf(args[1]);
					x = Integer.parseInt(args[2]);
					}
					catch (IllegalArgumentException e)
					{
						p.sendMessage("Entity type " + args[1] + " not found.");
					}
					Block tb = p.getTargetBlock(null, 256);
					Entity se = p.getWorld().spawnEntity(tb.getLocation(), EntityType.ARROW);
					int counter = 0;
					for (Entity e : se.getNearbyEntities(3, 3, 3)){
						se.remove();
						if (e.getType() == et){
							e.remove();
							if (counter == x){
								break;
							}
							counter++;
						}
					}
				}
			}
			if (args[0].equalsIgnoreCase("create")){
				if (args.length < 3){
					int amt = 1;
					while (amt > 0){
						amt = amt - 1;
						p.getWorld().spawnEntity(p.getTargetBlock(null, 256).getLocation().getBlock().getRelative(BlockFace.UP).getLocation(), EntityType.fromName(args[1]));
					}
					return true;
				}
				int amount = Integer.parseInt(args[2]);
				while (amount > 0){
					amount = amount - 1;
					p.getWorld().spawnEntity(p.getTargetBlock(null, 256).getLocation().getBlock().getRelative(BlockFace.UP).getLocation(), EntityType.fromName(args[1]));
				}
				p.sendMessage(args[2] + " " + args[1] + " spawned.");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("chunkme")){
			p.sendMessage("You have been chunked.");
			p.getWorld().regenerateChunk(p.getLocation().getBlockX(), p.getLocation().getBlockZ());
	        return true;
		}
		if (label.equalsIgnoreCase("timer")){
			Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
				public void run(){
					
				}
			}, 50, 50);
		}
		if (label.equalsIgnoreCase("gimp") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage("Argument must be players name.");
				return true;
			}
			Player t = Bukkit.getPlayer(args[0]);
			if ((gimped + "").contains(t.getName())){
				if (gimped.get(t.getName())){
					gimped.put(t.getName(), false);
					p.sendMessage("§e" + t.getName() + " has been ungimped.");
					return true;
				}
			}
			gimped.put(t.getName(), true);
			p.sendMessage("§e" + t.getName() + " has been gimped.");
			return true;
		}
		if (label.equalsIgnoreCase("biomeset") && p.hasPermission("superserver.*")){
			World world = p.getWorld();
			Location loc = p.getLocation();
			world.setBiome(loc.getBlockX(), loc.getBlockZ(), Biome.valueOf(args[0]));
	        return true;
		}
		if (label.equalsIgnoreCase("world") && p.hasPermission("superserver.*")){
			World world = Bukkit.getWorld(args[0]);
			p.teleport(new Location(world, world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ()));
	        return true;
		}
		if (label.equalsIgnoreCase("se") | label.equalsIgnoreCase("signedit") && p.hasPermission("superserver.*")){
			String message = "";
			if (p.getTargetBlock(null, 256).getType().equals(Material.WALL_SIGN) | p.getTargetBlock(null, 256).getType().equals(Material.SIGN_POST)){
				Sign sign = (Sign) p.getTargetBlock(null, 256).getState();
				if (args.length < 1){
					p.sendMessage(ChatColor.RED + "Incorrect usage: /signedit <line> <message>");
					return true;
				}
				try {  
					Double.parseDouble(args[0]);  
				} catch (NumberFormatException nfe) {  
					p.sendMessage(ChatColor.RED + "Incorrect usage: Second argument must be an integer.");
					return true;  
				}  
				if (Integer.parseInt(args[0]) > 4){
					p.sendMessage(ChatColor.RED + "Incorrect usage: There cannot be more than 4 lines.");
					return true;
				}
		        for (int x = 1;x < args.length; x++) {
			           message = message + args[x] + " ";
			           message = message.replaceAll("&", "§");
			           if (x + 1 == args.length){
				           sign.setLine(Integer.parseInt(args[0]) - 1, message);
				           sign.update();
			           }
		        }
			}
	        return true;
		}
		if (label.equalsIgnoreCase("test")){
			if (sender instanceof Player){
				p.sendMessage("Your ping is " + Methods.getPing(p) + " ms");
				return true;
			}
			System.out.println("TEST");
	        return true;

		}
		if (label.equalsIgnoreCase("enchant") && p.hasPermission("superserver.enchant")){
			try{
			p.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(args[0]), Integer.parseInt(args[1]));
			}
			catch (Exception e){
				p.sendMessage("§cIncorrect usage.");
			}
		}
		if (label.equalsIgnoreCase("tf") && p.hasPermission("superserver.*")){
			if (!Start.Settings.get("TreeFelling")){
				p.sendMessage("Treefelling is disabled in the sphmoneymobs.");
			}
			Methods.addTreeFellEnchant(p);
			return true;
		}
		if (label.equalsIgnoreCase("ls2") && p.hasPermission("superserver.*")){
			ItemStack item = new ItemStack(p.getItemInHand().getType(), 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§b" + "Level Stealing Sword");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§7Level Theif II");
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.getInventory().setItemInHand(item);
	        return true;
		}
		if (label.equalsIgnoreCase("ls") && p.hasPermission("superserver.*")){
			ItemStack item = new ItemStack(p.getItemInHand().getType(), 1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§b" + "Level Stealing Sword");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("§7Level Theif I");
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.getInventory().setItemInHand(item);
	        return true;
		}
		if (label.equalsIgnoreCase("hp") && p.hasPermission("superserver.*") || label.equalsIgnoreCase("honeypot") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /honeypot [args]");
				return true;
			}
			return true;
		}
		if (label.equalsIgnoreCase("sban") && p.hasPermission("superserver.*")){
			String t = args[0];
			Player tplayer = getServer().getPlayer(t);
			Bukkit.banIP(tplayer.getAddress().getHostString());
			tplayer.setBanned(true);
			tplayer.kickPlayer("You have been sbanned.");
	        return true;
		}
		if (label.equalsIgnoreCase("afk") && p.hasPermission("superserver.afk")){
			try
			{
				if (afk.get(p.getName())){
					afk.put(p.getName(), false);
					p.setDisplayName(p.getName());
					p.sendMessage("You are not AFK anymore.");
					return true;
				}
				afk.put(p.getName(), true);
				p.setDisplayName("§7[AFK] " + p.getName());
				p.sendMessage("§7[§8SS§7] You are now AFK.");
				return true;
			}
			catch (NullPointerException e)
			{
				afk.put(p.getName(), true);
				return true;
			}
		}
		if (label.equalsIgnoreCase("vanish") && p.hasPermission("superserver.*")){
			String vanish = "" + p.getName();
			if (!vanished.containsKey(vanish)){
				for (Player player : Bukkit.getOnlinePlayers()){
					player.hidePlayer(p);
				}
				p.setCanPickupItems(false);
				p.sendMessage("§7[§8SS§7] §7You have vanished.");
				vanished.put(p.getName(), 1);
			}else{
				for (Player player : Bukkit.getOnlinePlayers()){
					player.showPlayer(p);
				}
				p.setCanPickupItems(true);
				vanished.remove(p.getName());
				p.sendMessage("§7[§8SS§7] §fYou have unvanished.");
			}
	        return true;

		}
		if (label.equalsIgnoreCase("setname") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /setname <string>");
				return true;
			}
			String t = args[0];
			if (t.equals("return")){
				p.sendMessage("§7[§8SS§7] §aNickname cleared.");
				p.setDisplayName(p.getName());
			}else{
				t = t.replaceAll("&", "§");
				p.setDisplayName(t + "§r");
				p.sendMessage("§7[§8SS§7] §bYou have changed your name to: §8" + p.getDisplayName());
			}
	        return true;
		}
		if (label.equalsIgnoreCase("superserver")){
			p.sendMessage("SUPERSERVER v1.1");
			p.sendMessage("ABOUT: A sphmoneymobs that adds tons of new commands and such.");
			p.sendMessage("AUTHOR: swampshark19");
	        return true;
		}
		if (label.equalsIgnoreCase("explode") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /explode <name>");
				return true;
			}
			final Player t = p.getServer().getPlayer(args[0]);
			if (!explode.containsKey(t)){
				explode.put(t, null);
				t.sendMessage("Explosions will now be created when you use wand");
			}else if (explode.containsKey(t)){
					explode.remove(t);
					t.sendMessage("Explosions will not be created anymore");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("lightning") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /lightning <name>");
				return true;
			}
			final Player t = p.getServer().getPlayer(args[0]);
			if (!lightning.containsKey(t)){
				lightning.put(t, null);
				t.sendMessage("Lightning will now be created when you use wand");
			}else if (lightning.containsKey(t)){
					lightning.remove(t);
					t.sendMessage("Lightning will not be created anymore");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("fly") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /fly <name>");
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			if (!flying.containsKey(t)){
				flying.put(t, null);
				t.setAllowFlight(true);
				t.sendMessage("§7[§8SS§7] You can now fly");
			}else if (flying.containsKey(t)){
					flying.remove(t);
					t.setAllowFlight(false);
					t.sendMessage("§7[§8SS§7] You cannot fly anymore");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("ib") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /ib <name>");
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			if (!ib.containsKey(t)){
				ib.put(t, null);
				t.setAllowFlight(true);
				t.sendMessage("§7[§8SS§7] You can now instantly break blocks");
			}else if (ib.containsKey(t)){
					ib.remove(t);
					t.sendMessage("§7[§8SS§7] You cannot instantly break blocks anymore");
			}
	        return true;
			
		}
		if (label.equalsIgnoreCase("god") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				try{
				if (Attributes.get(p.getName()).get("god")){
					Attributes.get(p.getName()).put("god", false);
					p.sendMessage("God mode disabled");
				}else{
					Attributes.get(p.getName()).put("god", true);
					p.sendMessage("God mode enabled");
				}
				}catch (NullPointerException e){
					try{
					Attributes.get(p.getName()).put("god", true);
					}
					catch (NullPointerException er){
						HashMap<String, Boolean> attribute = new HashMap<String, Boolean>();
						attribute.put("god", true);
						Attributes.put(p.getName(), attribute);
					}
				}
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			Attributes.get(t.getName()).put("god", !Attributes.get(t.getName()).get("god"));
			if (Attributes.get(t.getName()).get("god"))
				t.sendMessage("God mode enabled");
			else
				t.sendMessage("God mode disabled");
	        return true;
		}
		if (label.equalsIgnoreCase("kill") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.setHealth(0);
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			t.setHealth(0);
	        return true;
		}
		if (label.equalsIgnoreCase("feed") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.setFoodLevel(20);
				p.setSaturation(30);
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			if (args.length < 2){
				t.setFoodLevel(20);
				t.setSaturation(30);
			}else{
				int amt = Integer.parseInt(args[1]);
				t.setFoodLevel(amt);
				t.setSaturation(30);
			}
	        return true;
		}
		if (label.equalsIgnoreCase("saturation") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				double sat = p.getSaturation();
				p.sendMessage(sat + "");
				return true;
			}else{
				Player t = p.getServer().getPlayer(args[0]);
				double sat = t.getSaturation();
				p.sendMessage(sat + "");
			}
	        return true;

		}
		if (label.equalsIgnoreCase("heal") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.setHealth(20);
				return true;
			}
			Player t = p.getServer().getPlayer(args[0]);
			if (args.length < 2){
				t.setHealth(20);
			}else{
				int amt = Integer.parseInt(args[1]);
				t.setHealth(amt);
			}
	        return true;
		}
		if (label.equalsIgnoreCase("cls") && p.hasPermission("superserver.*")){
			if (!(args.length < 1)){
				int counter = 0;
				try{
					while (counter < 25 * Integer.parseInt(args[0])){
						p.sendMessage("                                                ");
						counter++;
					}	
				}catch (Exception e){
					p.sendMessage("§cIncorrect usage: Argument must be a integer");
				}

		        return true;
			}
			int counter = 0;
			while (counter < 25){
				p.sendMessage("                                                ");
				counter++;
			}
	        return true;
		}
		if (label.equalsIgnoreCase("entity") && p.hasPermission("superserver.*")){
			HashSet<Byte> transparent = new HashSet<Byte>();
			transparent.add((byte) Material.AIR.getId());
			transparent.add((byte) Material.SNOW.getId());
			transparent.add((byte) Material.LONG_GRASS.getId());
			CraftWorld cw = (CraftWorld) p.getWorld();
			Block tg = p.getTargetBlock(transparent, 256);
			TileEntity te = cw.getHandle().getTileEntity(tg.getX(), tg.getY(), tg.getZ());
			if (args[0].equalsIgnoreCase("nbtsetstring")){
				Methods.addTileEntityNBTString(te, args, p);
				tg.getState().update();
			}
			if (args[0].equalsIgnoreCase("nbtsetshort")){
				Methods.addTileEntityNBTShort(te, args, p);
				tg.getState().update();
			}
			if (args[0].equalsIgnoreCase("nbtsetint")){
				Methods.addTileEntityNBTInt(te, args, p);
				tg.getState().update();
			}
			return true;
		}
		if (label.equalsIgnoreCase("helpme")){
			String message = "";
			for (int x = 0;x < args.length; x++){
				message = message + args[x] + " ";
				if (x + 1 == args.length){
					Bukkit.broadcast(ChatColor.RED + p.getName() + " needs help with \"" + message + "\"", getServer().BROADCAST_CHANNEL_ADMINISTRATIVE);
				}
			}
	        return true;
		}
		if (label.equalsIgnoreCase("physics") && p.hasPermission("superserver.*")){
			Settings.put("Physics", !Settings.get("Physics"));
	        return true;
		}
		if (label.equalsIgnoreCase("setmotd") && p.hasPermission("superserver.*")){
			String motd = "";
			for (int x = 0;x < args.length; x++){
				motd = motd + args[x] + " ";
				if (x + 1 == args.length){
					motda = motd;
				}
			}
	        return true;
		}
		if (label.equalsIgnoreCase("motd") && p.hasPermission("superserver.*")){
			p.sendMessage(motda);
	        return true;
		}
		if (label.equalsIgnoreCase("fill") && p.hasPermission("superserver.*")){
			try{
			args[0] = args[0].toUpperCase();
			if (!Material.valueOf(args[0]).isBlock()){
				return true;
			}
			List<Block> Blocks = Methods.getBlocksInSelection(pos1.get(p), pos2.get(p), p.getWorld());
			Methods.fillSelectedBlocks(Blocks, Material.valueOf(args[0]), p.getWorld());
			p.sendMessage("§d" + Blocks.size() + " blocks changed.");
			}
			catch (Exception e){
				p.sendMessage("§cIncorrect usage.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("size") && p.hasPermission("superserver.*")){
			try{
			List<Block> Blocks = Methods.getBlocksInSelection(pos1.get(p), pos2.get(p), p.getWorld());
			p.sendMessage("§d" + Blocks.size() + " blocks selected.");
			}
			catch (NullPointerException e){
				p.sendMessage("§cSelection must be made first.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("replace") && p.hasPermission("superserver.*")){
			try{
			args[0] = args[0].toUpperCase();
			args[1] = args[1].toUpperCase();
			if (!Material.valueOf(args[0]).isBlock() && !Material.valueOf(args[1]).isBlock()){
				return true;
			}
			List<Block> Blocks = Methods.getBlocksInSelection(pos1.get(p), pos2.get(p), p.getWorld());
			int amount = Methods.replaceSelectedBlocks(Blocks, Material.valueOf(args[0]), Material.valueOf(args[1]), p.getWorld());
			p.sendMessage("§d" + amount + " blocks changed.");
			}
			catch (Exception e){
				p.sendMessage("§cIncorrect usage.");
			}
			return true;
		}
		if (label.equalsIgnoreCase("clearinventory") || label.equalsIgnoreCase("ci") && p.hasPermission("superserver.*")){
			try{
				Bukkit.getPlayer(args[0]).getInventory().clear();
			}catch (Exception e){
				p.getInventory().clear();
			}
	        return true;
		}
		if (label.equalsIgnoreCase("extinguish") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.RED + "Incorrect usage, /" + "extinguish <player>");
			}else{
				try{
					Player t = Bukkit.getPlayer(args[0]);
					t.setFireTicks(0);
				}catch (Exception e){
					p.setFireTicks(0);
				}
			}
	        return true;
		}
		if (label.equalsIgnoreCase("setpos1")){
			pos1.put(p, p.getLocation());
			p.sendMessage("§dPosition 1 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
		}
		if (label.equalsIgnoreCase("settpos1")){
			pos1.put(p, p.getTargetBlock(null, 256).getLocation());
			p.sendMessage("§dPosition 1 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
		}
		if (label.equalsIgnoreCase("setpos2")){
			pos2.put(p, p.getLocation());
			p.sendMessage("§dPosition 2 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
		}
		if (label.equalsIgnoreCase("settpos2")){
			pos2.put(p, p.getTargetBlock(null, 256).getLocation());
			p.sendMessage("§dPosition 2 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
		}
		if (label.equalsIgnoreCase("fire") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.RED + "Incorrect usage, /" + "fire <player> <duration>");
				return true;
			}
			try{
				Player t = Bukkit.getPlayer(args[0]);
				if (args.length < 2){
					t.setFireTicks(200);
				}else{
					int time = Integer.parseInt(args[1]);
					time = time * 20;
					t.setFireTicks(time);
				}
			}catch (Exception e){
				try{
					p.setFireTicks(Integer.parseInt(args[0]));
				}catch (Exception e2){
					p.sendMessage("§cSecond number must be either a valid player or a number.");
				}
			}

	        return true;
		}
		if (label.equalsIgnoreCase("mute") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.DARK_RED + "Usage: /mute <player>");
				return true;
			}
			String tn = Bukkit.getOfflinePlayer(args[0]).getName();
			if (muted.get(tn))
				muted.put(tn, false);
			if (!muted.get(tn))
				muted.put(tn, true);
	        return true;
		}
		if (label.equalsIgnoreCase("spawn") && p.hasPermission("superserver.*")){
			World world = p.getWorld();
			Location spawnloc = world.getSpawnLocation();
			p.teleport(spawnloc);
	        return true;
		}
		if (label.equalsIgnoreCase("tell") || label.equalsIgnoreCase("msg")){
			Player t = p.getServer().getPlayer(args[0]);
			String pl = p.getName();
	        String message = "";
	        for (int x = 1;x < args.length; x++) {
		        args[x] = args[x].replaceAll("&", "§");
		        message = message + args[x] + " ";
		        if (x + 1 == args.length){
		            t.sendMessage(ChatColor.GRAY + pl + " whispers " + message);
		        }
			}
	        return true;
		}
		if (label.equalsIgnoreCase("crash") && p.hasPermission("superserver.*") && p.isOp()){
			if (args.length < 1){
				p.sendMessage(ChatColor.RED + "Incorrect usage, /" + "crash <player>");
				return true;
			}else{
				final Player t = p.getServer().getPlayer(args[0]);
				String onlinecheck = "" + t;
				if (onlinecheck.equals("null")){
					p.sendMessage(ChatColor.RED + "Player " + args[0] + " not found");
					return true;
				}else{
				final String tn = t.getName();
				t.sendMessage(ChatColor.YELLOW + "Nope.");
				t.sendMessage(ChatColor.YELLOW + "Nope.");
				t.sendMessage(ChatColor.YELLOW + "Nope.");
				t.sendMessage(ChatColor.YELLOW + "Nope.");
				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {	
					  public void run() {
						  org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer nmsPlayerType = (org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer) t;
						  PlayerConnection pc = nmsPlayerType.getHandle().playerConnection;
						  EntityPlayer nmsp = nmsPlayerType.getHandle();
						  pc.sendPacket(new PacketPlayOutNamedEntitySpawn(nmsp));
						  Bukkit.broadcastMessage(ChatColor.RED + tn + "'s client has been crashed.");
						  t.kickPlayer("Nope.");
					  }
					}, 10L);
				}
			}
	        return true;
		}
		if (label.equalsIgnoreCase("timeget") && p.hasPermission("superserver.*")){
			World world = p.getWorld();
			long ticks = world.getTime();
			long seconds = ticks / 20;
			int minutes = (int) (seconds / 60);
			int timeleft = (10 - minutes);
			p.sendMessage("Ticks " + ticks);
			p.sendMessage("Seconds " + seconds);
			p.sendMessage("Minutes " + minutes);
			p.sendMessage("Minutes till new day " + timeleft);
	        return true;
		}
		if (label.equalsIgnoreCase("leave") || label.equalsIgnoreCase("l") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.RED + "Incorrect usage, /" + "leave <string>");
			}else{
			String t = args[0];
			Bukkit.broadcastMessage("§e" + t + " has left the game");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("join") || label.equalsIgnoreCase("j") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				p.sendMessage(ChatColor.RED + "Incorrect usage, /" + "join <string>");
			}else{
				String t = args[0];
				Bukkit.broadcastMessage("§e" + t + " has joined the game");
			}
	        return true;
		}
		if (label.equalsIgnoreCase("sb") || label.equalsIgnoreCase("superbroadcast") && p.hasPermission("superserver.*")){
	         String message = "";
	         for (int x = 0;x < args.length; x++) {
		        args[x] = args[x].replaceAll("&", "§");
	            message = message + args[x] + " ";
	            if (x + 1 == args.length){
	            Bukkit.broadcastMessage(ChatColor.RESET + message);
	            }
	         }
	         return true;
		}
		if (label.equalsIgnoreCase("sm") || label.equalsIgnoreCase("supermessage") && p.hasPermission("superserver.*")){
			if (args.length < 1){
				return true;
			}
			for (Player player : Bukkit.getOnlinePlayers()){
				if (!player.getName().equalsIgnoreCase(args[0]))
					continue;
					return true;
			}
			Player t = Bukkit.getPlayer(args[0]);
			String message = "";
			int x = 1;
			while (x < args.length){
				args[0] = args[x].replaceAll("&", "§");
				message = message + args[x] + " ";
				x = x + 1;
			}
			t.sendMessage(ChatColor.RESET + message);
			return true;
		}
		p.sendMessage("Command not implemented yet!");
		return true;
	}
	//Methods that require main class
	public void setWarp(Player p, String warp){
		p.sendMessage("§dWarp set.");
		getConfig().set("warps." + warp + ".location.world" , p.getLocation().getWorld().getName());
		getConfig().set("warps." + warp + ".location.x" , p.getLocation().getBlockX());
		getConfig().set("warps." + warp + ".location.y" , p.getLocation().getBlockY());
		getConfig().set("warps." + warp + ".location.z" , p.getLocation().getBlockZ());
		saveConfig();
	}
	public void setHome(Player p){
		p.sendMessage("§dHome set.");
		getConfig().set("homes." + p.getName() + ".location.world" , p.getLocation().getWorld().getName());
		getConfig().set("homes." + p.getName() + ".location.x" , p.getLocation().getBlockX());
		getConfig().set("homes." + p.getName() + ".location.y" , p.getLocation().getBlockY());
		getConfig().set("homes." + p.getName() + ".location.z" , p.getLocation().getBlockZ());
		saveConfig();
	}
	public void gotoWarp(Player p, String warp){
		String worldString = (String) getConfig().get("warps." + warp + ".location.world");
		World world = Bukkit.getWorld(worldString);
		int x = getConfig().getInt("warps." + warp + ".location.x");
		int y = getConfig().getInt("warps." + warp + ".location.y");
		int z = getConfig().getInt("warps." + warp + ".location.z");
		Location location = new Location(world, x, y, z);
		p.teleport(location);
		p.sendMessage("§bTeleporting to warp");
	}
	public void gotoHome(Player p){
		String worldString = (String) getConfig().get("homes." + p.getName() + ".location.world");
		World world = Bukkit.getWorld(worldString);
		int x = getConfig().getInt("homes." + p.getName() + ".location.x");
		int y = getConfig().getInt("homes." + p.getName() + ".location.y");
		int z = getConfig().getInt("homes." + p.getName() + ".location.z");
		Location location = new Location(world, x, y, z);
		p.teleport(location);
		p.sendMessage("§bTeleporting home.");
	}
	public boolean delWarp(String warp){
		try{
		getConfig().getKeys(false).remove(warp);
		return true;
		}
		catch (NullPointerException e){
			return false;
		}
	}
	public boolean doesWarpExist(String warp){
		try{
			getConfig().getInt("warps." + warp + ".location.x");
		}
		catch (NullPointerException e){
			return false;
		}
		return true;
	}
    public void registerConfig(){
		Start.Configs.put("BlockSwearing", Boolean.parseBoolean(getConfig().getString("ChatProtection").replaceAll("\'", "")));
		Start.Configs.put("SpawnPVPProtection", Boolean.parseBoolean(getConfig().getString("SpawnPVPProtection").replaceAll("\'", "")));
		Start.Configs.put("LogEvents", Boolean.parseBoolean(getConfig().getString("LogEvents").replaceAll("\'", "")));
		Start.Configs.put("ChatProtection", Boolean.parseBoolean(getConfig().getString("ChatProtection").replaceAll("\'", "")));
		saveConfig();
    }

}