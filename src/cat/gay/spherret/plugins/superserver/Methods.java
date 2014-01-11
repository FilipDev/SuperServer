package cat.gay.spherret.plugins.superserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.PlayerConnection;
import net.minecraft.server.v1_7_R1.TileEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Methods{
	
	java.util.Date now = new Date();
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	public static void addTreeFellEnchant(Player p){
		ItemStack item = new ItemStack(Material.IRON_AXE, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b" + "Tree Felling Axe");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Tree Feller I");
		meta.setLore(lore);
		item.setItemMeta(meta);
		p.getWorld().dropItemNaturally(p.getEyeLocation(), item);
        return;
	}
	@SuppressWarnings("deprecation")
	public static void addLoreAndName(Player p, int slot){
		if (p.getInventory().getContents()[slot].getType().equals(Material.POTION)){
			ItemStack item = p.getInventory().getContents()[slot];
			byte data = item.getData().getData();
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			if (data == 7){
				meta.setDisplayName("§rPotion of Disease");
				lore.add("§cHunger (3:00)");
			}
			if (data == 11){
				meta.setDisplayName("§rPotion of Resistance");
				lore.add("§7Resistance (3:00)");
			}
			if (data == 13){
				meta.setDisplayName("§rPotion of Blinding");
				lore.add("§cBlindness (3:00)");
			}
			if (data == 15){
				meta.setDisplayName("§rPotion of Water Breathing");
				lore.add("§7Water Breathing (3:00)");
			}
			if (data == 23){
				meta.setDisplayName("§rPotion of Withering");
				lore.add("§cWither (3:00)");
			}
			if (data == 27){
				meta.setDisplayName("§rPotion of Leaping");
				lore.add("§7Jump Boost (3:00)");
			}
			if (data == 29){
				meta.setDisplayName("§rPotion of Haste");
				lore.add("§7Haste (3:00)");
			}
			if (data == 31){
				meta.setDisplayName("§rPotion of Mining Fatigue");
				lore.add("§cMining Fatigue (3:00)");
			}
			if (data == 39){
				meta.setDisplayName("§rPotion of Confusion");
				lore.add("§cNausea (3:00)");
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			p.getInventory().setItem(slot, item);
		}
	}
	public static void makeCircle(Location loc, int r, Material m) {
        int x;
        int y = loc.getBlockY();
        int z;
        int temprad = 0;
        for (double i = 0.0; i < 360.0; i += 0.1) {
        if (!(temprad > r)){
            temprad++;
        }
        double angle = i * Math.PI / 180;
            x = (int)(loc.getX() + temprad * Math.cos(angle));
            z = (int)(loc.getZ() + temprad * Math.sin(angle));
            Bukkit.getWorld("world").getBlockAt(x, y, z).setType(m);
        }
    }
    public static boolean addEntityNBTShort(Entity e, String[] args, Player p){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		System.out.println(tag.getShort(args[1]));
		tag.setShort(args[1], Short.parseShort(args[2]));
		((EntityLiving)nmsEntity).a(tag);
		p.sendMessage("§aChange successful.");
    	return true;
    }
    public static boolean addEntityNBTByte(Entity e, String[] args, Player p){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		System.out.println(tag.getByte(args[1]));
		tag.setByte(args[1], Byte.parseByte(args[2]));
		((EntityLiving)nmsEntity).a(tag);
		p.sendMessage("§aChange successful.");
    	return true;
    }
    public static boolean addEntityNBTInt(Entity e, String[] args, Player p){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		System.out.println(tag.getInt(args[1]));
		tag.setInt(args[1], Integer.parseInt(args[2]));
		((EntityLiving)nmsEntity).a(tag);
		p.sendMessage("§aChange successful.");
    	return true;
    }
    public static boolean addEntityNBTString(Entity e, String tagname, String value){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		System.out.println(tag.getString(tagname));
		tag.setString(tagname, value);
		((EntityLiving)nmsEntity).a(tag);
    	return true;
    }
    public static boolean removeEntityNBTString(Entity e, String tagname){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		System.out.println(tag.getString(tagname));
		((EntityLiving)nmsEntity).a(tag);
    	return true;
    }
    @SuppressWarnings("static-access")
	public static void addTileEntityNBTString(TileEntity te, String[] args, Player p){
		NBTTagCompound tag = new NBTTagCompound();
		te.c(tag);
		tag.setString(args[1], args[2]);
		te.a(tag);
		p.sendMessage("§aChange successful.");
		te.update();
		return;
    }
    @SuppressWarnings("static-access")
	public static void addTileEntityNBTShort(TileEntity te, String[] args, Player p){
		NBTTagCompound tag = new NBTTagCompound();
		te.c(tag);
		tag.setShort(args[1], Short.parseShort(args[2]));
		te.a(tag);
		p.sendMessage("§aChange successful.");
		te.update();
		return;
    }
    @SuppressWarnings("static-access")
	public static void addTileEntityNBTInt(TileEntity te, String[] args, Player p){
		NBTTagCompound tag = new NBTTagCompound();
		te.c(tag);
		tag.setInt(args[1], Integer.parseInt(args[2]));
		te.a(tag);
		p.sendMessage("§aChange successful.");
		te.update();
		return;
    }
	public static void getEntityNBT(Entity e, String[] args, Player p){
    	net.minecraft.server.v1_7_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
		NBTTagCompound tag = new NBTTagCompound();
		nmsEntity.c(tag);
		p.sendMessage("" + tag.getShort("Health"));
    }
    public static void addItemNBTInt(Player p, String[] args){
    }
    public static void dismount(Player p){
    	p.setPassenger(p);
    	p.eject();
    }
    public static void createCube(int dimx, int dimy, int dimz, int x, int y, int z, Material m, World w){
    	int tmpx = x;
    	int tmpy = y;
    	int tmpz = z;
    	while (0 != 1){
    		w.getBlockAt(tmpx, tmpy, tmpz).setType(m);
    		tmpx++;
    		if (tmpx > x + dimx - 1){
    			tmpx = x;
    			tmpz++;
    		}
    		if (tmpz > z + dimz - 1){
    			tmpz = z;
    			tmpy++;
    		}
    		if (tmpy > y + dimy - 1){
    			break;
    		}
    	}
    }
    public static void fillSelectedBlocks(List<Block> Blocks, Material m, World w){
    	for (Block block : Blocks){
    		block.setType(m);
    	}
    }
    public static int replaceSelectedBlocks(List<Block> Blocks, Material prem, Material newm, World w){
    	int be = 0;
    	for (Block block : Blocks){
    		if (block.getType().equals(prem)){
    			block.setType(newm);
    			be++;
    		}
    	}
    	return be;
    }
    public static List<Block> getBlocksInSelection(Location loc1, Location loc2, World w){
    	List<Block> Blocks = new ArrayList<Block>();
        int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
	    miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
	    minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
	    maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
	    maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
	    maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	    for (int x = minx; x<=maxx; x++) {
	        for (int y = miny; y<=maxy; y++) {
	            for (int z = minz; z<=maxz; z++) {
	                Block b = w.getBlockAt(x, y, z);
	                Blocks.add(b);
	            }
	        }
	    }
		return Blocks;
    }
	
    public static void shootArrow(Player p){
    	Entity arrow = p.getWorld().spawnEntity(p.getLocation(), EntityType.ARROW);
    	arrow.setVelocity(p.getLocation().getDirection().multiply(3));
    }
    @SuppressWarnings("null")
	public static Block[] getBlockColomn(int x, int z, int from, int to){
    	Block[] Blocks = null;
    	int counter = from;
    	while (counter < to){
        	Blocks[counter] = Bukkit.getWorld("world").getBlockAt(x, counter, z);
    	}
    	return Blocks;
    }
    public static void openFakeInventory(Player p, String inventoryType){
    	Inventory inv = Bukkit.createInventory(null, InventoryType.valueOf(inventoryType));
    	p.openInventory(inv);
    }
    public static List<Entity> filterEntity(EntityType et){
    	List<Entity> e = new ArrayList<Entity>();
    	for (World world : Bukkit.getWorlds()){
    		e.addAll(world.getEntities());
    	}
    	List<Entity> entities = new ArrayList<Entity>();
    	for (Entity entity : e){
    		if (entity.getType().equals(et)){
    			entities.add(entity);
    		}
    	}
    	return entities;
    }
    public static void closePlayerInventory(Player p){
    	p.openInventory(p.getInventory());
    	p.closeInventory();
    }
    public static int getPing(Player p) {
    	CraftPlayer cp = (CraftPlayer) p;
    	EntityPlayer ep = cp.getHandle();
    	return ep.ping;
    }
    public static boolean arrayContains(String[] list, String string){
    	for (String liststring : list){
    		if (liststring.equals(string))
    			return true;
    	}
    	return false;
    }
}
