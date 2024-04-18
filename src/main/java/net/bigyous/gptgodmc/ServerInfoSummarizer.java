package net.bigyous.gptgodmc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.bigyous.gptgodmc.enums.GptGameMode;
import net.bigyous.gptgodmc.utils.NicknameCommand;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ServerInfoSummarizer {
    public static String getInventoryInfo(Player player) {
        StringBuilder sb = new StringBuilder();
        // Armor Items
        StringBuilder armorString = new StringBuilder();
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) {
                armorString.append(formatItemStack(armor) + ", ");
            }
        }
        if (!armorString.isEmpty()) {
            sb.append("Armor: " + armorString.toString() + "\n");
        }

        // Inventory Items
        // sb.append("Inventory: ");
        // for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
        // ItemStack stack = player.getInventory().getItem(i);
        // if (!stack.isEmpty()) {
        // sb.append(formatItemStack(stack) + ", ");
        // }
        // }
        // sb.append("\n");

        // Equipped Item (Main Hand)
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        sb.append(String.format("Main Hand: %s, Off Hand %s\n", formatItemStack(main), formatItemStack(off)));
        return sb.toString();
    }

    private static String getStructures() {
        StringBuilder sb = new StringBuilder();
        for (String structure : StructureManager.getStructures()) {
            sb.append(String.format("%s: size: %d builder: %s, ", structure,
                StructureManager.getStructure(structure).getSize(),
                    StructureManager.getStructure(structure).getBuilder().getName()));
        }
        return sb.toString();
    }

    private static String getDangerLevel(Player player) {
        // can't use getNearbyEntities because it is not Thread safe
        List<Entity> nearby = new ArrayList<Entity>();
        for(Entity entity : player.getChunk().getEntities()){
            if(player.getLocation().distanceSquared(entity.getLocation()) <= 100){
                nearby.add(entity);
            }
        }
        List<Entity> enemies = nearby.stream().filter((Entity entity) -> entity instanceof Enemy).toList();
        List<Entity> bosses = nearby.stream().filter((Entity entity) -> entity instanceof Boss).toList();
        if (enemies.size() < 1 && bosses.size() < 1) {
            return "Safe";
        } else if (enemies.size() <= 3 && bosses.size() < 1) {
            return "Minor";
        } else if (enemies.size() < 10 && bosses.size() < 1) {
            return "Moderate";
        } else if (enemies.size() < 17 || bosses.size() == 1) {
            return "High";
        }
        return "Crtitical";
    }

    private static String formatItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "None";
        }
        // stack.getItem()
        String name = stack.getItemMeta().hasDisplayName()
                ? PlainTextComponentSerializer.plainText().serialize(stack.getItemMeta().displayName())
                : stack.getType().name();
        return name;
    }

    private static String getPlayerHealth(Player player) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double health = player.getHealth();
        double healthRatio = health / maxHealth;
        if (healthRatio > 0.50) {
            return "Healthy";
        }
        if (healthRatio <= 0.5 && healthRatio > 0.3) {
            return "Injured";
        }
        if (healthRatio <= 0.3) {
            return "Gravely Wounded";
        }
        return "Unknown";
    }

    private static String getWeather() {
        World world = WorldManager.getCurrentWorld();
        return String.format("%s %s", world.isThundering() ? "Thunder" : "",
                world.isClearWeather() ? "Clear" : "Storm");
    }

    private static String getObjectives() {
        return GPTGOD.SCOREBOARD.getObjectives().isEmpty() ? "" :
            String.format("Objectives: %s", String.join(",", GPTGOD.SCOREBOARD.getEntries().stream().filter(entry -> GPTGOD.SERVER.getPlayer(entry)==null).toList()));
    }

    public static String getStatusSummary() {
        StringBuilder sb = new StringBuilder("Server Status:\n");
        sb.append(String.format("Time of day: %s\n", WorldManager.getCurrentWorld().isDayTime() ? "Day" : "Night"));
        sb.append(String.format("Weather: %s\n", getWeather()));
        sb.append("Structures: " + getStructures() + "\n");
        sb.append(getObjectives());
        for (Player player : GPTGOD.SERVER.getOnlinePlayers()) {
            // player.getP
            String name = player.getName();
            String nickname = NicknameCommand.getNickname(player);
            boolean isDead = player.isDead() || player.getGameMode().equals(GameMode.SPECTATOR);
            String health = isDead ? "Dead" : getPlayerHealth(player);
            boolean isSleeping = player.isSleeping();
            // player.getInventory()
            String inventoryInfo = getInventoryInfo(player);
            sb.append("Status of Player " + name + ":\n");
            if (!nickname.isBlank()) sb.append("Nickname: " + nickname + "\n");
            if(GPTGOD.gameMode.equals(GptGameMode.DEATHMATCH)){
                sb.append(String.format("Team: %s\n", GPTGOD.SCOREBOARD.getEntityTeam(player).getName()));
            }
            sb.append("Health: " + health + '\n');
            if (!isDead) {
                sb.append(StructureManager.getClosestStructureToLocation(player.getLocation()));
                // sb.append("\tDead? " + isDead + "\n");
                // sb.append("\tInventory: " + inventoryInfo + "\n");
                // sb.append(isDead? "Dead\n" : "Alive\n");
                sb.append(isSleeping ? "Asleep\n" : "");
                sb.append("Danger Level: " + getDangerLevel(player) + "\n");
                sb.append(inventoryInfo + "\n");
            }
        }
        ;
        return sb.toString();
    }
}
