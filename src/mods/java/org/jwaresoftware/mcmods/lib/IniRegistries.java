package org.jwaresoftware.mcmods.lib;

import net.minecraft.util.ResourceLocation;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jwaresoftware.mcmods.lib.api.IRegistrationListener;

public final class IniRegistries {
   private static final Map<String, IniRegistries.ModDefs> registryDefsINSTANCE = new ConcurrentHashMap();

   public static final boolean isStarted(String modid) {
      return registryDefsINSTANCE.containsKey(modid);
   }

   public static final void clearAll(@Nullable String modid) {
      if (modid != null) {
         registryDefsINSTANCE.remove(modid);
      } else {
         registryDefsINSTANCE.clear();
      }

   }

   @Nullable
   static final IniRegistries.ModDefs getModDefs(@Nonnull String modid, boolean force) {
      IniRegistries.ModDefs modDefs = (IniRegistries.ModDefs)registryDefsINSTANCE.get(modid);
      if (modDefs == null && force) {
         modDefs = new IniRegistries.ModDefs();
         registryDefsINSTANCE.put(modid, modDefs);
      }

      return modDefs;
   }

   @Nonnull
   public static final List<Block> autoregisterBlocks(@Nonnull String modid, @Nonnull IForgeRegistry<Block> blocks) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      List<Block> pending;
      if (modDefs != null && !modDefs._unregisteredBlocks.isEmpty()) {
         pending = (List)modDefs._unregisteredBlocks.clone();
         modDefs._unregisteredBlocks.clear();

         for(Block block : pending) {
            blocks.register(block);
         }

         for(Block block : pending) {
            if (block instanceof IRegistrationListener) {
               ((IRegistrationListener)block).notifyRegistered(blocks);
            }
         }
      } else {
         pending = new ArrayList();
      }

      return pending;
   }

   @Nonnull
   public static final List<Item> autoregisterItems(@Nonnull String modid, @Nonnull IForgeRegistry<Item> items) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      List<Item> pending;
      if (modDefs != null && !modDefs._unregisteredItems.isEmpty()) {
         pending = (List)modDefs._unregisteredItems.clone();
         modDefs._unregisteredItems.clear();
         modDefs._unregisteredItemFromBlockMap.clear();

         for(Item item : pending) {
            items.register(item);
         }

         for(Item item : pending) {
            if (item instanceof IRegistrationListener) {
               ((IRegistrationListener)item).notifyRegistered(items);
            }
         }
      } else {
         pending = new ArrayList();
      }

      return pending;
   }

   public static final <T> T autoregisterItem(@Nonnull String modid, @Nonnull Item item, @Nonnull String oid) {
      item.setRegistryName(oid);
      getModDefs(modid, true)._unregisteredItems.add(item);
      return (T)item;
   }

   public static final void autoregisterBlockItem(@Nonnull String modid, @Nonnull ItemBlock blockitem, @Nonnull String oid) {
      autoregisterItem(modid, blockitem, oid);
      Block block = blockitem.getBlock();
      getModDefs(modid, true)._unregisteredItemFromBlockMap.put(block, blockitem);
   }

   @Nonnull
   public static final Block autoregisterBlock(@Nonnull String modid, @Nonnull Block block, @Nonnull String oid, boolean only) {
      block.setRegistryName(oid);
      getModDefs(modid, true)._unregisteredBlocks.add(block);
      if (!only) {
         ItemBlock itemblock = new ItemBlock(block);
         autoregisterBlockItem(modid, itemblock, oid);
      }

      return block;
   }

   public static final <T extends TileEntity> void autoregisterBlockTile(Class<T> teClass, String fq_oid, Supplier<T> factory) {
      GameRegistry.registerTileEntity(teClass, new ResourceLocation(fq_oid), factory);
   }

   @Nullable
   public static final Item getItemFromBlock(@Nonnull String modid, @Nonnull Block block) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      if (modDefs != null) {
         ItemBlock blockitem = (ItemBlock)modDefs._unregisteredItemFromBlockMap.get(block);
         if (blockitem != null) {
            return blockitem;
         }
      }

      return Item.getItemFromBlock(block);
   }

   public static final Potion autoregisterPotion(@Nonnull String modid, @Nonnull Potion potion, @Nonnull String oid) {
      potion.setRegistryName(modid, oid);
      getModDefs(modid, true)._unregisteredPotions.add(potion);
      return potion;
   }

   public static final PotionType autoregisterPotionType(@Nonnull String modid, @Nonnull PotionType mix, @Nonnull String oid) {
      mix.setRegistryName(modid, oid);
      getModDefs(modid, true)._unregisteredPotionMixes.add(mix);
      return mix;
   }

   public static final void autoregisterPotions(@Nonnull String modid, @Nonnull IForgeRegistry<Potion> potions) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      if (modDefs != null && !modDefs._unregisteredPotions.isEmpty()) {
         List<Potion> pending = (List)modDefs._unregisteredPotions.clone();
         modDefs._unregisteredPotions.clear();

         for(Potion p : pending) {
            potions.register(p);
         }

         for(Potion p : pending) {
            if (p instanceof IRegistrationListener) {
               ((IRegistrationListener)p).notifyRegistered(potions);
            }
         }
      }

   }

   public static final void autoregisterPotionMixes(@Nonnull String modid, @Nonnull IForgeRegistry<PotionType> mixes) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      if (modDefs != null && !modDefs._unregisteredPotionMixes.isEmpty()) {
         List<PotionType> pending = (List)modDefs._unregisteredPotionMixes.clone();
         modDefs._unregisteredPotionMixes.clear();

         for(PotionType mix : pending) {
            mixes.register(mix);
         }
      }

   }

   public static final Enchantment autoregisterEnchantment(@Nonnull String modid, @Nonnull Enchantment enchant, @Nonnull String oid) {
      enchant.setRegistryName(modid, oid);
      getModDefs(modid, true)._unregisteredEnchantments.add(enchant);
      return enchant;
   }

   public static final void autoregisterEnchantments(@Nonnull String modid, @Nonnull IForgeRegistry<Enchantment> enchantments) {
      IniRegistries.ModDefs modDefs = getModDefs(modid, false);
      if (modDefs != null && !modDefs._unregisteredEnchantments.isEmpty()) {
         List<Enchantment> pending = (List)modDefs._unregisteredEnchantments.clone();
         modDefs._unregisteredEnchantments.clear();

         for(Enchantment e : pending) {
            enchantments.register(e);
         }

         for(Enchantment e : pending) {
            if (e instanceof IRegistrationListener) {
               ((IRegistrationListener)e).notifyRegistered(enchantments);
            }
         }
      }

   }

   private IniRegistries() {
   }

   public static final class ModDefs {
      final ArrayList<Block> _unregisteredBlocks = new ArrayList(91);
      final ArrayList<Item> _unregisteredItems = new ArrayList(301);
      final HashMap<Block, ItemBlock> _unregisteredItemFromBlockMap = new HashMap(71, 0.8F);
      final ArrayList<Potion> _unregisteredPotions = new ArrayList(31);
      final ArrayList<PotionType> _unregisteredPotionMixes = new ArrayList(71);
      final ArrayList<Enchantment> _unregisteredEnchantments = new ArrayList(31);

      public ModDefs() {
      }
   }
}
