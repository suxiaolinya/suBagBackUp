package suxiaolin.subagbackup;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Serialize {
    public static Serialize serialize;

    public Serialize() {
        serialize = this;
    }
    public String serializePlayerInventory(Player player) {
        ItemStack[] items = player.getInventory().getContents();
        ReadWriteNBT nbt = NBT.itemStackArrayToNBT(items);
        String str = nbt.toString();
        return str;
    }

    public void deserializePlayerInventory(Player player, String str) {
        ReadWriteNBT nbt = NBT.parseNBT(str);
        ItemStack[] itemStacks = NBT.itemStackArrayFromNBT(nbt);
        player.getInventory().setContents(itemStacks);
    }
}
