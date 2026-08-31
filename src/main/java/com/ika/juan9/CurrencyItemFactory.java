package com.ika.juan9;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CurrencyItemFactory {

    private final NamespacedKey cashKey;
    private final DecimalFormat format = new DecimalFormat("#,###");

    public CurrencyItemFactory(Juan9 plugin) {
        this.cashKey = new NamespacedKey(plugin, "cash_amount");
    }

    public ItemStack createCashItem(int amount, int count) {
        if (amount <= 0) {
            throw new IllegalArgumentException("화폐 금액은 1 이상이어야 합니다.");
        }
        if (count <= 0 || count > 64) {
            throw new IllegalArgumentException("화폐 개수는 1 이상 64 이하여야 합니다.");
        }

        ItemStack cash = new ItemStack(getMaterial(amount), count);
        ItemMeta meta = cash.getItemMeta();
        if (meta == null) {
            return cash;
        }

        meta.setDisplayName("§6§l[공식 화폐] §e" + format.format(amount) + " GOLD");

        List<String> lore = new ArrayList<>();
        lore.add("§7서버 공식 인증 실물 화폐입니다.");
        lore.add("§8금액 정보는 서버 고유 데이터로 보호됩니다.");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(cashKey, PersistentDataType.INTEGER, amount);
        cash.setItemMeta(meta);

        applyVisual(cash, amount);
        return cash;
    }

    public boolean refreshCashItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        Integer amount = meta.getPersistentDataContainer().get(cashKey, PersistentDataType.INTEGER);
        if (amount == null || amount <= 0) {
            return false;
        }

        item.setType(getMaterial(amount));
        applyVisual(item, amount);
        return true;
    }

    private void applyVisual(ItemStack item, int amount) {
        item.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData()
                        .addString(getModelId(amount))
                        .build()
        );
    }

    private Material getMaterial(int amount) {
        return amount >= 1_000 ? Material.PAPER : Material.ARMS_UP_POTTERY_SHERD;
    }

    private String getModelId(int amount) {
        if (amount >= 10_000) return "juan9:cash_10000";
        if (amount >= 1_000) return "juan9:cash_1000";
        if (amount >= 100) return "juan9:cash_100";
        return "juan9:cash_10";
    }
}
