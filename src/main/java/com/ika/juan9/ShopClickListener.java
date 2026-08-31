package com.ika.juan9;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShopClickListener implements Listener {

    private final Juan9 plugin;

    public ShopClickListener(Juan9 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (entity instanceof Villager && entity.getCustomName() != null && entity.getCustomName().startsWith("§e[상점] ")) {
            event.setCancelled(true);

            String shopName = entity.getCustomName().replace("§e[상점] ", "");

            Merchant merchant = Bukkit.createMerchant("§l" + shopName);
            List<MerchantRecipe> recipes = new ArrayList<>();

            ConfigurationSection section = plugin.getConfig().getConfigurationSection("shops." + shopName);
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    ItemStack result = section.getItemStack(key + ".result");
                    ItemStack cost = section.getItemStack(key + ".cost");

                    if (result != null && cost != null) {
                        MerchantRecipe recipe = new MerchantRecipe(result, 9999);
                        recipe.addIngredient(cost);
                        recipes.add(recipe);
                    }
                }
            }

            merchant.setRecipes(recipes);
            player.openMerchant(merchant, true);
        }
    }
}