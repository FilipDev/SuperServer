package cat.gay.spherret.plugins.superserver;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public class Events extends JavaPlugin implements Listener{

	Start st;
	
	final static HashMap<Chunk, ArrayList<Block>> chunks = new HashMap<Chunk, ArrayList<Block>>();
	final static HashMap<String, Integer> warnings = new HashMap<String, Integer>();
	final static HashMap<String, Long> timeleft = new HashMap<String, Long>();
	
	
	long time = 0;
	long timechat = 0;
	int times = 0;

	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	java.util.Date now = new Date();
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event){
		if (Start.vanilla){
			return;
		}
		Sign s = (Sign)event.getBlock().getState();
		String one = event.getLine(0);
		String two = event.getLine(1);
		String three = event.getLine(2);
		String four = event.getLine(3);
		one = one.replaceAll("&", "§");
		four = four.replaceAll("&", "§");
		three = three.replaceAll("&", "§");
		two = two.replaceAll("&", "§");
		event.setLine(0, one);
		event.setLine(1, two);
		event.setLine(2, three);
		event.setLine(3, four);
		if (event.getLine(0).toLowerCase().contains("[command]")){
			event.setLine(0, "§d[Command]");
			event.getPlayer().performCommand(event.getLine(1));
		}
		if (event.getLine(0).toLowerCase().contains("[give]")){
			event.setLine(0, "§b[Give]");
		}
		if (event.getLine(0).toLowerCase().contains("[teleport]")){
			for (char c : event.getLine(1).toCharArray()){
				event.setLine(0, "§7[Teleport]");
		        if (Character.isDigit(c) == false){
		        	event.getPlayer().sendMessage(ChatColor.RED + "Second line not number.");
		        	return;
		        }
			}
			for (char d : event.getLine(2).toCharArray()){
		        if (Character.isDigit(d) == false){
		        	event.getPlayer().sendMessage(ChatColor.RED + "Third line not number.");

		        	return;
		        }
			}
			for (char e : event.getLine(3).toCharArray()){
		        if (Character.isDigit(e) == false){
		        	event.getPlayer().sendMessage(ChatColor.RED + "Fourth line not number.");

		        	return;
		        }
			}
			double x = Integer.parseInt((String) event.getLine(1));
			double y = Integer.parseInt((String) event.getLine(2));
			double z = Integer.parseInt((String) event.getLine(3));
			Start.stp.put(event.getBlock().getLocation(), new Location(event.getBlock().getWorld(), x, y, z));
			
		}
		s.update();
		
	}
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerEnterBed(PlayerBedEnterEvent event){
		event.getPlayer().chat("/sethome");
		int sleeping = 0;
		int awake = 0;
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.isSleeping() == true){
				sleeping = sleeping + 1;
			}
			if (player.isSleeping() == false){
				awake = awake + 1;
			}
			if (sleeping > awake){
				event.getPlayer().getWorld().setTime(0);
			}
		}
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onServerPing(ServerListPingEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		event.setMotd(Start.motda);
		event.setMaxPlayers(50);
		if (Start.Configs.get("LogEvents"))
			Bukkit.broadcast("§a" + event.getAddress() + " opened their server list, with this server on it.", "superserver.*");
		if (Bukkit.getIPBans().contains(event.getAddress().getHostAddress())){
			event.setMotd("§cYou are banned from this server.");
			event.setMaxPlayers(1);
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		event.setCancelled(true);
		Player p = event.getPlayer();
		String pl = p.getDisplayName();
		String message = event.getMessage();
		String omessage = event.getMessage();
		java.util.Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");		
		if (Start.vanilla){
			return;
		}
		if (Start.Configs.get("ChatProtection")){
			try{
			if (Start.lastChat.get(pl).equals(omessage)){
				p.sendMessage(ChatColor.YELLOW + "Duplicate message blocked.");
				event.setCancelled(true);
				return;
			}
			}catch(Exception e){}
		}
		if (p.hasPermission("superserver.chatcolor")){
			message = message.replaceAll("&", "§");
		}
		System.out.println(ChatColor.RED + "(" + p.getAddress().getHostString() + ") " + ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.DARK_GRAY + pl + ": " + message);
		for (Player rec : Bukkit.getOnlinePlayers()){
			try{
				if (message.contains(rec.getName()) || message.contains(rec.getDisplayName())){
					rec.playSound(rec.getLocation(), Sound.ORB_PICKUP, 10, 5);
					String foundNameMessage = message.replaceAll(rec.getName(), "§c" + rec.getName());
					if (!rec.isOp())
						rec.sendMessage(ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.DARK_GRAY + pl + ": " + ChatColor.RESET + foundNameMessage);
					else
						rec.sendMessage(ChatColor.RED + "(" + p.getAddress().getHostString() + ") " + ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.DARK_GRAY + pl + ": " + ChatColor.RESET + foundNameMessage);
				}else{
					if (!rec.isOp())
						rec.sendMessage(ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.DARK_GRAY + pl + ": " + ChatColor.RESET + message);
					else
						rec.sendMessage(ChatColor.RED + "(" + p.getAddress().getHostString() + ") " + ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.DARK_GRAY + pl + ": " + ChatColor.RESET + message);
				}
				Start.lastChat.put(p.getName(), omessage);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		Entity e = event.getEntity();
		if (e instanceof Player){
			Player p = (Player) e;
			try{
				if (Start.Attributes.get(p.getName()).get("god")){
					event.setCancelled(true);
					return;
				}
			}catch (Exception e1){}
		}
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		Entity d = event.getEntity().getKiller();
		Entity e = event.getEntity();
		if (d instanceof Player){
			if (e.getType().equals(EntityType.ENDER_DRAGON)){
				Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ((Player) d).getName() + " killed an enderdragon!");
			}
		}
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDeath(final PlayerDeathEvent event){
		
		if (Start.vanilla){
			return;
		}
		Start.back.put(event.getEntity().getName(), event.getEntity().getEyeLocation());
		String dmessage = event.getDeathMessage();
		event.setDeathMessage(ChatColor.GREEN + "[" + format.format(now) + "] " + ChatColor.RED + dmessage);
	    Random randomNumber = new Random();
	    int rand = randomNumber.nextInt(100);
		if (rand > 50){
			event.getEntity().getWorld().spawnCreature(event.getEntity().getLocation(), EntityType.ZOMBIE);
		}
		if (rand < 50){
			event.getEntity().getWorld().spawnCreature(event.getEntity().getLocation(), EntityType.SKELETON);
		}
		return;
	}
	@EventHandler (priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		Entity e = event.getEntity();
		Entity d = event.getDamager();
		if (d.getType().equals(EntityType.PLAYER) && d.getLocation().distance(d.getWorld().getSpawnLocation()) < Bukkit.getSpawnRadius() && Start.Configs.get("SpawnPVPProtection"))
			event.setCancelled(true);
		if (e.getType().equals(EntityType.PLAYER) && e.getLocation().distance(e.getWorld().getSpawnLocation()) < Bukkit.getSpawnRadius() && Start.Configs.get("SpawnPVPProtection"))
			event.setCancelled(true);
		if (d instanceof Player){
			Player p = ((Player) d).getPlayer();
			if (p.getItemInHand().getType().equals(Material.FEATHER)){
				try{
				if (e.getPassenger().equals(d)){
					p.getVehicle().remove();
					Methods.dismount(p);
				}
				}catch (Exception er){}
			}
			if ((p.getItemInHand().getItemMeta() + "").contains("XP Theif I")){
				if (e.getType().equals(EntityType.PLAYER)){
					if (p.getItemInHand().getDurability() > 1561 && p.getItemInHand().getType().equals(Material.DIAMOND_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 250 && p.getItemInHand().getType().equals(Material.IRON_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 131 && p.getItemInHand().getType().equals(Material.STONE_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 32 && p.getItemInHand().getType().equals(Material.GOLD_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 59 && p.getItemInHand().getType().equals(Material.WOOD_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					Player ep = p;
					try{
						ep = (Player) e;
					}catch (Exception e1){
						return;
					}
					if (ep.getLevel() == 0){
						return;
					}
					ep.setLevel(ep.getLevel() - 1);
					p.setLevel(p.getLevel() + 1);
					p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 20));
				}
			}
			if ((p.getItemInHand().getItemMeta() + "").contains("XP Theif II")){
				if (e.getType().equals(EntityType.PLAYER)){
					if (p.getItemInHand().getDurability() > 1561 && p.getItemInHand().getType().equals(Material.DIAMOND_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 250 && p.getItemInHand().getType().equals(Material.IRON_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 131 && p.getItemInHand().getType().equals(Material.STONE_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 32 && p.getItemInHand().getType().equals(Material.GOLD_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					if (p.getItemInHand().getDurability() > 59 && p.getItemInHand().getType().equals(Material.WOOD_SWORD)){
						p.getItemInHand().setType(Material.AIR);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 10, 0);
						event.setCancelled(true);
						return;
					}
					Player ep = (Player) e;
					if (ep.getLevel() == 0){
						return;
					}
					ep.setLevel(ep.getLevel() - 2 + 1);
					p.setLevel(p.getLevel() + 2 - 1);
					p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 20));
				}
			}
			Start.damagetime.remove(p.getName());
			Start.damagetime.put(p.getName(), (int) p.getWorld().getFullTime());
			if (p.getItemInHand().getType().equals(Material.NETHER_STAR) && p.hasPermission("superserver.*")){
				if (e.getType().equals(EntityType.PLAYER)){
					Player t = ((Player) e).getPlayer();
					t.kickPlayer(ChatColor.YELLOW + "Nope.");
					event.setCancelled(true);
					return;
				}else{
					//e.setVelocity(new Vector(0, 100, 0));
					//e.setFireTicks(20);
					e.remove();
					event.setCancelled(true);
					return;
				}
			}
			if (Start.Configs.get("LogEvents")){
				logger.info(p.getName() + " damaged a " + e.getType() + " using a " + p.getItemInHand().getType() + " at " + e.getLocation().getBlockX() + " " + e.getLocation().getBlockY() + " " + e.getLocation().getBlockZ() + " in world " + p.getLocation().getWorld().getName());
			}
		}
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onProjectileLand(ProjectileHitEvent event){
		/*if (event.getEntityType().equals(EntityType.EGG)){
			Entity entity = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
			entity.setMetadata("Spawned", new FixedMetadataValue(this, "1"));
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 1);
			Bukkit.broadcastMessage("" + entity.getMetadata("Health"));
		}*/
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBlockBreak(final BlockBreakEvent event){
		
		if (Start.vanilla){
			return;
		}
		try{
		Player p = event.getPlayer();
		Block bb = event.getBlock();
		if (bb.getTypeId() == 56 || bb.getTypeId() == 15 || bb.getTypeId() == 14 || bb.getTypeId() == 129 || bb.getTypeId() == 73){
			System.out.println(p.getName() + " broke block " + event.getBlock().getType() + " at " + event.getBlock().getLocation().getBlockX() + " " + 
					event.getBlock().getLocation().getBlockY() + " " + event.getBlock().getLocation().getBlockZ() + " with light level " + p.getLocation().getBlock().getLightLevel());
		}
		boolean tf;
		try{
			tf = Start.Settings.get("TreeFelling");
		}catch (NullPointerException e){
			tf = false;
		}
		if (tf){
			boolean a;
			try{
				a = p.getItemInHand().getItemMeta().getLore().contains("Tree Feller I");
			}catch (Exception e){
				a = false;
			}
			if (a){
				if (bb.getType().equals(Material.LOG) && bb.getRelative(BlockFace.DOWN).getType().equals(Material.DIRT) && (bb.getRelative(BlockFace.UP).getType().equals(Material.LOG)) == false){
					byte bad = bb.getData();
					p.getWorld().dropItemNaturally(bb.getLocation(), new ItemStack(Material.LOG, 1, bad));
					event.setCancelled(true);
					bb.setType(Material.SAPLING);
				}
				if ((bb.getType().equals(Material.LOG) || bb.getType().equals(Material.HUGE_MUSHROOM_1) || bb.getType().equals(Material.HUGE_MUSHROOM_2)) && p.getGameMode().equals(GameMode.CREATIVE) == false){
					p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 1));
				}
				Block bl = bb.getRelative(BlockFace.UP);
				while (bl.getType().equals(Material.LOG) || bl.getType().equals(Material.HUGE_MUSHROOM_1) || bl.getType().equals(Material.HUGE_MUSHROOM_2)){
					p.getWorld().spawnFallingBlock(bl.getLocation(), bl.getType(), bl.getData());
					bl.setType(Material.AIR);
					bl = bl.getRelative(BlockFace.UP);
				}
			}
		}		
		String pl = p.getName();
		Material b = bb.getType();
		ItemStack i = p.getItemInHand();
		Material in = i.getType();
		World worldr = p.getWorld();
		String world = worldr.getName();
		int x = bb.getX();
		int y = bb.getY();
		int z = bb.getZ();
		if (Start.Configs.get("LogEvents")){
			logger.info(pl + " broke a " + b + " using a " + in + " at " + x + " " + y + " " + z + " in world " + world + " with a light level of " + event.getBlock().getLightLevel());
		}

		Start.distance.put(p.getName(), p.getLocation().getX() + p.getLocation().getZ());
		HashSet<Byte> transparent = new HashSet<Byte>();
		transparent.add((byte)Material.SNOW.getId());
		transparent.add((byte)Material.VINE.getId());
		transparent.add((byte)Material.STONE_BUTTON.getId());
		transparent.add((byte)Material.WOOD_BUTTON.getId());
		transparent.add((byte)Material.AIR.getId());
		transparent.add((byte)Material.LADDER.getId());
		transparent.add((byte)Material.WALL_SIGN.getId());
		transparent.add((byte)Material.FENCE.getId());
		transparent.add((byte)Material.FENCE_GATE.getId());
		transparent.add((byte)Material.COBBLE_WALL.getId());
		transparent.add((byte)Material.STATIONARY_WATER.getId());
		transparent.add((byte)Material.WATER.getId());
		transparent.add((byte)Material.STATIONARY_LAVA.getId());
		transparent.add((byte)Material.LAVA.getId());
		}catch (NullPointerException e){
			e.printStackTrace();
		}

	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		Player p = event.getPlayer();
		String pl = p.getName();
		Block bb = event.getBlock();
		Material b = bb.getType();
		
		World worldr = p.getWorld();
		String world = worldr.getName();
		Location loc = bb.getLocation();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		Start.distance.put(p.getName(), p.getLocation().getX() + p.getLocation().getZ());
		if (b == Material.LOCKED_CHEST){
			z = z + 0.5;
			x = x + 0.5;
			worldr.spawnEntity(new Location(p.getWorld(), x, y, z), EntityType.ENDER_CRYSTAL);
			bb.setType(Material.BEDROCK);
			p.getItemInHand().setType(Material.LOCKED_CHEST);
			p.updateInventory();
		}
		if (Start.Configs.get("LogEvents")){
			logger.info(pl + " placed a " + b + " at " + x + " " + y + " " + z + " in world " + world);	
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPhysics(BlockPhysicsEvent event){
		
		if (Start.vanilla)
			return;
		
		event.setCancelled(Start.Settings.get("Physics"));
	}
	@EventHandler
	public void onMoveSlot(PlayerItemHeldEvent event){
		Player p = event.getPlayer();
		int slot = event.getNewSlot();
		ItemStack helditem = p.getInventory().getContents()[slot];
		String his = helditem + "";
		if (his.equals("null")){
			return;
		}
		Methods.addLoreAndName(p, slot);
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		try{
		if (Start.vanilla){
			return;
		}
		Player p = event.getPlayer();
		String is = p.getItemInHand().getType() + "";
		String isint = p.getItemInHand().getTypeId() + "";
		Action action = event.getAction();
		String actions = action.name() + "";
		
		//LEFT CLICK AIR
		if (actions.equals("LEFT_CLICK_AIR")){
			try{
				Methods.addLoreAndName(p, p.getInventory().getHeldItemSlot());
			}catch (NullPointerException e){
			}
			if (is.equals("NETHER_STAR") && p.hasPermission("superserver.*")){
				String btbs = p.getTargetBlock(null, 256) + "";
				if (btbs.equals("null")){return;}
				if (Start.explode.containsKey(p)){p.getWorld().createExplosion(p.getTargetBlock(null, 256).getLocation(), 6);
				}
				if (Start.lightning.containsKey(p)){
					p.getWorld().strikeLightning(p.getTargetBlock(null, 256).getLocation());
				}
				p.getInventory().addItem(new ItemStack(p.getTargetBlock(null, 256).getType(), 1, p.getTargetBlock(null, 256).getData()));
				p.getTargetBlock(null, 256).setType(Material.AIR);
				if (p.getTargetBlock(null, 256).getType().equals(Material.AIR) == false){
					p.playSound(p.getLocation(), Sound.CLICK, 10, 3);
					long num = (long) (((((((((((((Math.random() * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10) * 10);
					p.sendMessage(num + "");
					p.updateInventory();
				}
			}
			return;
		}
		
		//RIGHT CLICK AIR
		if (actions.equals("RIGHT_CLICK_AIR")){
			try{
				Methods.addLoreAndName(p, p.getInventory().getHeldItemSlot());
			}catch (NullPointerException e){
			}
			if (is.equals("FEATHER") && p.hasPermission("superserver.grapple")){
				Entity chicken = p.getVehicle();
				if (!p.isInsideVehicle()){
					chicken = p.getWorld().spawnEntity(p.getLocation(), EntityType.CHICKEN);
					chicken.setPassenger(p);
				}
				chicken.setVelocity(p.getLocation().getDirection().multiply(p.getVelocity().getX() + p.getVelocity().getY() + p.getVelocity().getZ() + 2.2));
				for (Entity entity : p.getNearbyEntities(50, 50, 50)){
					if (entity instanceof Player){
						Player player = (Player) entity;
						player.hidePlayer(p);
						player.playEffect(p.getLocation(), Effect.SMOKE, 10);
					}
				}
				p.playEffect(p.getLocation(), Effect.SMOKE, 10);
				return;
			}
			if (is.equals("ARROW") && p.hasPermission("superserver.*")){
				if ((Start.type.containsKey(p.getName()) == false)){
					return;
				}
				HashSet<Byte> transparent = new HashSet<Byte>();
				transparent.add((byte) Material.AIR.getId());
				transparent.add((byte) Start.type.get(p.getName()).getId());
				Block ab = p.getTargetBlock(transparent, 270);
				if ((ab.getType().equals(Material.AIR))){
					return;
				}
				ab.setType(Start.type.get(p.getName()));
			}
			if (is.equals("NETHER_STAR") && p.hasPermission("superserver.*")){
				Block ab = p.getTargetBlock(null, 270);
				if (ab.getType().equals(Material.AIR)){
					p.sendMessage(ChatColor.RED + "Destination set too far away.");
					return;
				}else{
					float yaw = p.getLocation().getYaw();
					float pitch = p.getLocation().getPitch();
					long ya = ab.getY();
					ya = ya + 1;
					p.teleport(new Location(p.getWorld(), ab.getX(), ya, ab.getZ(), yaw, pitch));
				}
			}
			int isi = Integer.parseInt(isint);
			if (isi == 360 || isi == 357){
				if (p.getFoodLevel() > 19){
					return;
				}
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				if (p.getItemInHand().getAmount() == 1){
					return;
				}
				event.setCancelled(true);
				p.setFoodLevel(p.getFoodLevel() + 2);
			}
			if (isi == 373){
				p.sendMessage("" + p.getItemInHand().getData().getData());
				if (p.getItemInHand().getData().getData() == 7){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 11){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 13){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 15){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 23){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 27){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 29){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 31){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 39){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 0));
				}
			}
			return;
		}
		Block bb = event.getClickedBlock();
		
		//LEFT CLICK BLOCK
		if (actions.equals("LEFT_CLICK_BLOCK")){
			try{
				Methods.addLoreAndName(p, p.getInventory().getHeldItemSlot());
			}catch (NullPointerException e){
			}
			if (Start.ib.containsKey(p)){
				event.getClickedBlock().setType(Material.AIR);
				event.setCancelled(true);
			}
			if (is.equals("STICK")){
				event.setCancelled(true);
				p.sendMessage("§dPosition 2 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
				Start.pos2.put(p, event.getClickedBlock().getLocation());
			}
			if (is.equals("NETHER_STAR") && p.hasPermission("superserver.*")){
				String bms = event.getClickedBlock().getType() + "";
				
				if (bms.equals("null")){
					if (p.getTargetBlock(null, 256).getType().equals(Material.AIR) == false){
						p.getTargetBlock(null, 256).setType(Material.AIR);
					}else{
						return;
					}
				}
				
				p.getInventory().addItem(new ItemStack(event.getClickedBlock().getType(), 1, event.getClickedBlock().getData()));
				event.getClickedBlock().setType(Material.AIR);
				p.updateInventory();
				
			}
		}
		
		//RIGHT CLICK BLOCK
		if (actions.equals("RIGHT_CLICK_BLOCK")){
			try{
				Methods.addLoreAndName(p, p.getInventory().getHeldItemSlot());
			}catch (NullPointerException e){
			}
			if (event.getClickedBlock().getType().equals(Material.SIGN_POST) | event.getClickedBlock().getType().equals(Material.WALL_SIGN)){
				if (((Sign) event.getClickedBlock().getState()).getLine(0).contains("[Command]")){
					p.chat("/" + ((Sign) event.getClickedBlock().getState()).getLine(1) + " " + ((Sign) event.getClickedBlock().getState()).getLine(2) + " " + ((Sign) event.getClickedBlock().getState()).getLine(3));
				}
				if (((Sign) event.getClickedBlock().getState()).getLine(0).contains("[Give]")){
					Material item = Material.getMaterial(((Sign) event.getClickedBlock().getState()).getLine(1));
					int num = Integer.parseInt(((Sign) event.getClickedBlock().getState()).getLine(2));
					byte data = Byte.parseByte(((Sign) event.getClickedBlock().getState()).getLine(3));
					p.getInventory().addItem(new ItemStack(item, num, data));
					p.updateInventory();
					return;
				}
				if (((Sign) event.getClickedBlock().getState()).getLine(0).contains("[Teleport]")){
					if (Start.stp.containsKey(event.getClickedBlock().getLocation())){
						p.teleport(Start.stp.get(event.getClickedBlock().getLocation()));
					}
					((Sign) event.getClickedBlock().getState()).setLine(0, "§7[Teleport]");
					double x = Integer.parseInt(((Sign) event.getClickedBlock().getState()).getLine(1));
					double y = Integer.parseInt(((Sign) event.getClickedBlock().getState()).getLine(2));
					double z = Integer.parseInt(((Sign) event.getClickedBlock().getState()).getLine(3));
					Start.stp.put(event.getClickedBlock().getLocation(), new Location(event.getClickedBlock().getWorld(), x, y, z));
					if (Start.stp.containsKey(event.getClickedBlock().getLocation())){
						p.teleport(Start.stp.get(event.getClickedBlock().getLocation()));
					}
				}

			}
			if (is.equals("FEATHER")){
				Methods.dismount(p);
				return;
			}
			if (is.equals("STICK")){
				Start.pos1.put(p, event.getClickedBlock().getLocation());
				p.sendMessage("§dPosition 1 set at " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
				event.setCancelled(true);
			}
			if (is.equals("NETHER_STAR") && p.hasPermission("superserver.*")){
				bb.setData((byte) (bb.getData() + 1));
				p.sendMessage("Data changed from §c" + (bb.getData() - 1) + " to §a" + bb.getData());
			}
			
			if (event.getPlayer().getItemInHand().getType().equals(Material.WOOD_SPADE) && p.hasPermission("superserver.*")){
				p.getWorld().spawnFallingBlock(event.getClickedBlock().getLocation(), bb.getType(), bb.getData());
				bb.setType(Material.AIR);
			}
			int isi = Integer.parseInt(isint);
			if (isi == 360 || isi == 357){
				if (p.getFoodLevel() > 19){
					return;
				}
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				if (p.getItemInHand().getAmount() == 1){
					return;
				}
				event.setCancelled(true);
				p.setFoodLevel(p.getFoodLevel() + 2);
			}
			if (isi == 373){
				if (p.getItemInHand().getData().getData() == 7){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 11){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 13){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 15){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 23){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 27){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 29){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 31){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3600, 0));
				}
				if (p.getItemInHand().getData().getData() == 39){
					p.getItemInHand().setType(Material.GLASS_BOTTLE);
					p.getItemInHand().setDurability((short) 0);
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3600, 0));
				}
			}
			String pl = p.getName();
			World worldr = p.getWorld();
			String world = worldr.getName();
			int x = bb.getX();
			int y = bb.getY();
			int z = bb.getZ();
			HashSet<Byte> transparent = new HashSet<Byte>();
			transparent.add((byte)Material.VINE.getId());
			transparent.add((byte)Material.STONE_BUTTON.getId());
			transparent.add((byte)Material.WOOD_BUTTON.getId());
			transparent.add((byte)Material.AIR.getId());
			transparent.add((byte)Material.LADDER.getId());
			transparent.add((byte)Material.WALL_SIGN.getId());
			transparent.add((byte)Material.FENCE.getId());
			transparent.add((byte)Material.FENCE_GATE.getId());
			transparent.add((byte)Material.COBBLE_WALL.getId());
			transparent.add((byte)Material.STATIONARY_WATER.getId());
			transparent.add((byte)Material.WATER.getId());
			transparent.add((byte)Material.STATIONARY_LAVA.getId());
			transparent.add((byte)Material.LAVA.getId());
			if (Start.Configs.get("LogEvents")){
				if (bb.getType().equals(Material.STONE_BUTTON)){
					logger.info(pl + " activated a stone button at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.WOOD_BUTTON)){
					logger.info(pl + " activated a wooden button at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.LEVER)){
					logger.info(pl + " activated a lever at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.WOODEN_DOOR)){
					logger.info(pl + " interacted with a door at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.WOODEN_DOOR)){
					logger.info(pl + " interacted with a door at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.TRAP_DOOR)){
					logger.info(pl + " interacted with a trap door at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.CHEST)){
					logger.info(pl + " opened a chest at " + x + " " + y + " " + z + " in " + world);
				}
				if (bb.getType().equals(Material.FURNACE)){
					logger.info(pl + " opened a furnace at " + x + " " + y + " " + z + " in " + world);
				}
				
				if (is.equals("null") == true){
					return;
				}
				
				if (bb.getType().equals(Material.TNT) && p.getItemInHand().equals(Material.FLINT_AND_STEEL)){
					logger.info(pl + " activated a tnt " + x + " " + y + " " + z + " in " + world);
				}
			}
			
			if (is.equals("GHAST_TEAR") && p.hasPermission("superserver.*")){
				p.getWorld().regenerateChunk(p.getLocation().getBlock().getChunk().getX(), p.getLocation().getBlock().getChunk().getZ());
			}
		}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		Player p = event.getPlayer();
		String pl = p.getName();
		World worldr = event.getTo().getWorld();
		String world = worldr.getName();
		Location loc = event.getTo();
		int x = loc.getBlockX();
		long y = loc.getBlockY();
		int z = loc.getBlockZ();
		//Bukkit.broadcast(pl + " teleported to world " + world + " from " + event.getFrom().getBlockX() + " " + event.getFrom().getBlockY() + " " + event.getFrom().getBlockZ() + " to position " + x + " " + y + " " + z, "superserver.*");
		logger.info(pl + " teleported to world " + world + " from " + event.getFrom().getBlockX() + " " + event.getFrom().getBlockY() + " " + event.getFrom().getBlockZ() + " to position " + x + " " + y + " " + z);
	}
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event){
		if (Start.vanilla){
			return;
		}
		Player p = event.getPlayer();
		timeleft.put(p.getName(), p.getWorld().getFullTime());
	}
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPrePlayerJoin(AsyncPlayerPreLoginEvent event){
		
		if (Start.vanilla){
			return;
		}
		
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		if (Start.vanilla){
			return;
		}
		Player p = event.getPlayer();
		p.chat("/justjoined");
		try{
		for (Player player : Bukkit.getOnlinePlayers()){
			if (player.getAddress().getHostString().equals(p.getAddress().getHostString())){
				Start.ips.put(p.getName(), Start.ips.get(p.getName() + 1));
				if (Start.ips.get(p.getName()) > 1){
					p.kickPlayer("§e2 Instances of your IP already logged into server.");
				}
			}
		}
		if ((Start.gimped + "").contains(p.getName()) == false){
			Start.gimped.put(p.getName(), false);
		}
		
		if (Bukkit.getOfflinePlayer(p.getName()).hasPlayedBefore() == false){
			p.teleport(p.getWorld().getSpawnLocation());
			for (Player player : Bukkit.getOnlinePlayers()){
				if (player.equals(p) == false){
					player.setLevel(player.getLevel() + 1);
				}
				player.sendMessage("§d" + Bukkit.getOfflinePlayers().length + " unique players have joined the server thus far.");
				player.sendMessage("§dWhenever a new player joins the");
				player.sendMessage("§dserver, you get a reward of one level!");
			}
		}
		Start.distance.put(p.getName(), p.getLocation().getX() + p.getLocation().getZ());
		if (p.getWorld().getFullTime() - timeleft.get(p.getName()) < 101){
			p.kickPlayer("Joined too quickly after disconnecting.");
		}
		for (Player player : Bukkit.getOnlinePlayers()){
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 0, 10);
		}
		p.sendMessage("§eThis server runs §l§3Super§4Server.");
		}
		catch (NullPointerException e)
		{
		}
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent event){
		
		if (Start.vanilla){
			return;
		}
		HumanEntity p = event.getPlayer();
		if (((Player) p).isSneaking() == true){
			event.setCancelled(true);
			p.closeInventory();
		}
		int slot = 0;
		for (ItemStack item : p.getInventory().getContents()){
			String his = item + "";
			if (his.equals("null")){
				return;
			}
			if (item.getType().equals(Material.POTION)){
				Methods.addLoreAndName((Player) p, slot);
			}
			slot = slot + 1;
		}
		Start.distance.put(p.getName(), p.getLocation().getX() + p.getLocation().getZ());
	}
	@EventHandler (priority = EventPriority.HIGH)
	public void onInventoryClose(InventoryCloseEvent event){
		Player p = (Player) event.getPlayer();
		int slot = 0;
		for (ItemStack item : p.getInventory().getContents()){
			String his = item + "";
			if (his.equals("null")){
				return;
			}
			if (item.getType().equals(Material.POTION)){
				Methods.addLoreAndName((Player) p, slot);
			}
			slot = slot + 1;
		}
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event){
		
		if (Start.vanilla){
			return;
		}
		
		HumanEntity p = event.getWhoClicked();
		if (((Player) p).isSneaking() == true){
			event.setCancelled(true);
			p.closeInventory();
		}
		int slot = 0;
		for (ItemStack item : p.getInventory().getContents()){
			String his = item + "";
			if (his.equals("null")){
				return;
			}
			if (item.getType().equals(Material.POTION)){
				Methods.addLoreAndName((Player) p, slot);
			}
			slot = slot + 1;
		}
	}
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerMove(final PlayerMoveEvent event){	
		if (Start.vanilla){
			return;
		}
		final Player p = event.getPlayer();
		if ((Start.afk + "").contains(p.getName())){
			if (Start.afk.get(p.getName()) == true){
				Start.afk.put(p.getName(), false);
				p.setDisplayName(p.getName());	
				p.sendMessage("You are not AFK anymore.");
			}
		}
		//TRAMPOLINE CODE
		if (Start.Settings.get("Trampoline")){
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BLOCK) && p.hasPermission("superserver.*")){
				p.setVelocity(new Vector (0, 1, 0));
			}
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.GOLD_BLOCK) && p.hasPermission("superserver.*")){
				p.setVelocity(new Vector (0, 2, 0));
			}
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DIAMOND_BLOCK) && p.hasPermission("superserver.*")){
				p.setVelocity(new Vector (0, 10, 0));
			}
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.EMERALD_BLOCK) && p.hasPermission("superserver.*")){
				p.setVelocity(new Vector (0, 6, 0));
			}
		}
	}
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event){
		if (event.getTarget() instanceof Player){
			try{
				if (Start.Attributes.get(((Player) event.getTarget()).getName()).get("god")){
					event.setCancelled(true);
				}
			}catch (NullPointerException e){}
		}
	}
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if (event.getMessage().startsWith("/deop")){
			event.getPlayer().sendMessage("§cThat command can only be executed from the console.");
			event.setCancelled(true);
		}
		if (event.getMessage().startsWith("/stop")){
			event.getPlayer().sendMessage("§cThat command can only be executed from the console.");
			event.setCancelled(true);
		}
		for (Player allPlayers : Bukkit.getOnlinePlayers()){
			if (allPlayers.isOp()){
				if (!event.getMessage().equals("/justjoined"))
					allPlayers.sendMessage("§a[" + format.format(now) + "] §b" + "[" + event.getPlayer().getName() + "]" + ": §7" + event.getMessage() + "");
			}
		}
	}
}
