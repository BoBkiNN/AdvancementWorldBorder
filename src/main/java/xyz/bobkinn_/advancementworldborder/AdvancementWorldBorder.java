package xyz.bobkinn_.advancementworldborder;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.SoundCategory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;


public class AdvancementWorldBorder extends JavaPlugin implements Listener {
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§b[AWB] §eEnabling..");
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if(!config.exists()){
            Bukkit.getConsoleSender().sendMessage("§b[AWB] §eCreating new config file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }

        boolean debugEnabled = getConfig().getBoolean("debug");
        if (debugEnabled){
            //world list for debug
            Bukkit.getConsoleSender().sendMessage("§b[AWB] §eDebug enabled");
            String[] worldNames = new String[Bukkit.getServer().getWorlds().size()];
            int count = 0;
            for(World w : Bukkit.getServer().getWorlds()){
                worldNames[count] = w.getName();
                count++;
            }
            for(String s : worldNames){
                Bukkit.getConsoleSender().sendMessage("§b[AWB Debug] §eServer worlds = §c§n" + s);
            }
        }

        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getConsoleSender().sendMessage("§b[AWB] §eEnabled");
        debugEnabled(1, "");

    }
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§b[AWB] §eDisabled");
    }

    //config.yml
    String onExpandMsg = getConfig().getString("OnExpandMsg");
    Integer ExpandBlocks =  getConfig().getInt("ExpandBlocks");
    Long ExpandSeconds = getConfig().getLong("ExpandSeconds");
    boolean ChatMsgToggle = getConfig().getBoolean("ChatMsgToggle");
    boolean EnableSound = getConfig().getBoolean("EnableSound");
    boolean DivideNether = getConfig().getBoolean("DivideNether");
    String SoundName = getConfig().getString("SoundName");
    Double locX = getConfig().getDouble("SoundX");
    Double locY = getConfig().getDouble("SoundY");
    Double locZ = getConfig().getDouble("SoundZ");
    Integer SoundVolume = getConfig().getInt("SoundVolume");
    Integer SoundPitch = getConfig().getInt("SoundPitch");
    List<String> worlds = getConfig().getStringList("EnabledWorlds");
    List<String> DisabledAdv = getConfig().getStringList("DisabledAdv");
    boolean debugEnabled = getConfig().getBoolean("debug");

    Sound snd = Sound.valueOf(SoundName);
    String ExpandMsg = onExpandMsg.replace("&", "§");

    public void debugEnabled(int i, String s){
        if (!debugEnabled){return;}
        if (DivideNether && i == 1){
            Bukkit.getConsoleSender().sendMessage("§b[AWB Debug] §eDivideNether is enabled!");
        }
        if (i == 2){
            Bukkit.getConsoleSender().sendMessage("§b[AWB Debug] §eAdvancement key: §c§n" + s);
        }
        if (i == 3){
            Bukkit.getConsoleSender().sendMessage("§b[AWB Debug] §eSkipping key: §c§n" + s);
        }
    }

    @EventHandler
    public void onAdvancementGain(PlayerAdvancementDoneEvent e) {
        NamespacedKey key = e.getAdvancement().getKey();
        String keyString = key.toString();
        debugEnabled(2, keyString);
        for (String dKey : DisabledAdv){
            if (keyString.contains(dKey)){
                debugEnabled(3, keyString);
                return;
            }
        }

        for (String worldName : worlds) {
            World world = Bukkit.getWorld(worldName);
            if (world==null) continue;
            WorldBorder border = world.getWorldBorder();
            double borderSize = border.getSize();
            if (EnableSound) {
                Location loc = new Location(world, locX, locY, locZ);
                world.playSound(loc, snd, SoundCategory.AMBIENT, SoundVolume, SoundPitch);
            }

            if (DivideNether && worldName.equals("world_nether")){
                ExpandBlocks = ExpandBlocks / 8;
            }
            border.setSize(borderSize + ExpandBlocks, ExpandSeconds);
            ExpandBlocks =  getConfig().getInt("ExpandBlocks");

        }
        Player player = e.getPlayer();
        String DisplayName = player.getDisplayName();
        String pName = player.getName();
        if (ChatMsgToggle) {
            String msg = ExpandMsg.replace("%displayname%", DisplayName)
                    .replace("%name%", pName);
            //ExpandMsg = ExpandMsg.replace("%border%", "" + MsgWorldBorderSize); (unknown bug)
            Bukkit.broadcastMessage(msg);
        }
    }
}
