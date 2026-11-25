package xzeroair.trinkets.util.helpers;

import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class TrinketReflectionHelper {
   public static final Method ENTITY_SETSIZE = ObfuscationReflectionHelper.findMethod(Entity.class, "setSize", Void.TYPE, new Class[]{Float.TYPE, Float.TYPE});

   public TrinketReflectionHelper() {
   }

   public static List<LootPool> getPools(LootTable table) {
      return (List)ObfuscationReflectionHelper.getPrivateValue(LootTable.class, table, "pools");
   }

   public static List<LootEntry> getLootEntries(LootPool pool) {
      return (List)ObfuscationReflectionHelper.getPrivateValue(LootPool.class, pool, "lootEntries");
   }
}
