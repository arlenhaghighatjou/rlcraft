package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockSlabCustom;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.GenericFoodItem;
import com.lycanitesmobs.core.item.ItemBlockBase;
import com.lycanitesmobs.core.item.ItemSlabCustom;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ObjectManager {
   public static Map<String, Block> blocks = new HashMap();
   public static Map<String, Item> items = new HashMap();
   public static Map<Item, ModInfo> itemGroups = new HashMap();
   public static Map<String, Fluid> fluids = new HashMap();
   public static Map<Block, Item> buckets = new HashMap();
   public static Map<String, Class> tileEntities = new HashMap();
   public static Map<String, PotionBase> effects = new HashMap();
   public static Map<String, Class<? extends Entity>> specialEntities = new HashMap();
   public static Map<String, DamageSource> damageSources = new HashMap();
   public static Map<String, StatBase> stats = new HashMap();
   public static ModInfo currentModInfo;
   protected static int nextSpecialEntityNetworkId = 0;

   public ObjectManager() {
   }

   public static void setCurrentModInfo(ModInfo group) {
      currentModInfo = group;
   }

   public static int getNextSpecialEntityNetworkId() {
      return nextSpecialEntityNetworkId++;
   }

   public static Block addBlock(String name, Block block) {
      name = name.toLowerCase();
      blocks.put(name, block);
      if (block instanceof BlockSlabCustom) {
         BlockSlabCustom blockSlab = (BlockSlabCustom)block;
         ItemSlabCustom itemSlabCustom = new ItemSlabCustom(blockSlab, blockSlab, blockSlab.getDoubleBlock());
         items.put(name, itemSlabCustom);
         itemGroups.put(itemSlabCustom, currentModInfo);
      } else {
         ItemBlock itemBlock = new ItemBlockBase(block);
         itemBlock.setRegistryName(block.getRegistryName());
         items.put(name, itemBlock);
         itemGroups.put(itemBlock, currentModInfo);
      }

      return block;
   }

   public static Fluid addFluid(String fluidName) {
      ModInfo group = currentModInfo;
      Fluid fluid = new Fluid(fluidName, new ResourceLocation(group.modid + ":blocks/" + fluidName + "_still"), new ResourceLocation(group.modid + ":blocks/" + fluidName + "_flow"));
      fluids.put(fluidName, fluid);
      if (!FluidRegistry.registerFluid(fluid)) {
         LycanitesMobs.logWarning("", "Another fluid was registered as " + fluidName);
      }

      return fluid;
   }

   public static Item addBucket(Item bucket, Block block, Fluid fluid) {
      buckets.put(block, bucket);
      return bucket;
   }

   public static Item addItem(String name, Item item) {
      name = name.toLowerCase();
      items.put(name, item);
      itemGroups.put(item, currentModInfo);
      if (item instanceof ItemBucket) {
         IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
            private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
               ItemBucket itembucket = (ItemBucket)stack.getItem();
               BlockPos blockpos = source.getBlockPos().offset((EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING));
               return itembucket.tryPlaceContainedLiquid((EntityPlayer)null, source.getWorld(), blockpos) ? new ItemStack(Items.BUCKET) : this.dispenseBehavior.dispense(source, stack);
            }
         };
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, ibehaviordispenseitem);
      }

      return item;
   }

   public static Item addItem(String name, Item item, int weight, int minAmount, int maxAmount) {
      Utilities.addDungeonLoot(new ItemStack(item), minAmount, maxAmount, weight);
      Utilities.addStrongholdLoot(new ItemStack(item), minAmount, maxAmount, weight * 2);
      Utilities.addVillageLoot(new ItemStack(item), minAmount, maxAmount, weight);
      return addItem(name, item);
   }

   public static Class addTileEntity(String name, Class tileEntityClass) {
      name = name.toLowerCase();
      tileEntities.put(name, tileEntityClass);
      GameRegistry.registerTileEntity(tileEntityClass, "lycanitesmobs." + name);
      return tileEntityClass;
   }

   public static PotionBase addEffect(String name, ConfigBase config, boolean isBad, int color, boolean goodEffect) {
      if (!config.getBool("Effects", name + " enabled", true, "Set to false to disable this potion effect.")) {
         return null;
      } else {
         PotionBase effect = new PotionBase(name, isBad, color);
         effects.put(name, effect);
         ObjectLists.addEffect(goodEffect ? "buffs" : "debuffs", effect, name);
         return effect;
      }
   }

   public static void addSpecialEntity(String name, Class<? extends Entity> entityClass) {
      specialEntities.put(name, entityClass);
   }

   public static void addDamageSource(String name, DamageSource damageSource) {
      name = name.toLowerCase();
      damageSources.put(name, damageSource);
   }

   public static void addStat(String name, StatBase stat) {
      name = name.toLowerCase();
      if (!stats.containsKey(name)) {
         stat.registerStat();
         stats.put(name, stat);
      }
   }

   public static Block getBlock(String name) {
      name = name.toLowerCase();
      return !blocks.containsKey(name) ? null : (Block)blocks.get(name);
   }

   public static Item getItem(String name) {
      name = name.toLowerCase();
      return !items.containsKey(name) ? null : (Item)items.get(name);
   }

   public static Class getTileEntity(String name) {
      name = name.toLowerCase();
      return !tileEntities.containsKey(name) ? null : (Class)tileEntities.get(name);
   }

   public static PotionBase getEffect(String name) {
      name = name.toLowerCase();
      return !effects.containsKey(name) ? null : (PotionBase)effects.get(name);
   }

   public static DamageSource getDamageSource(String name) {
      name = name.toLowerCase();
      return !damageSources.containsKey(name) ? null : (DamageSource)damageSources.get(name);
   }

   public static StatBase getStat(String name) {
      name = name.toLowerCase();
      return !stats.containsKey(name) ? null : (StatBase)stats.get(name);
   }

   public static void registerBlocks(Register<Block> event) {
      event.getRegistry().registerAll((IForgeRegistryEntry[])blocks.values().toArray(new Block[blocks.size()]));

      for(Block block : blocks.values()) {
         if (block.getRegistryName() == null) {
            LycanitesMobs.logWarning("", "Block: " + block + " has no Registry Name!");
         }

         LycanitesMobs.proxy.addBlockRender(currentModInfo, block);
      }

   }

   public static void registerItems(Register<Item> event) {
      for(Item item : items.values()) {
         if (item.getRegistryName() == null) {
            LycanitesMobs.logWarning("", "Item: " + item + " has no Registry Name!");
         }

         event.getRegistry().register(item);
         LycanitesMobs.proxy.addItemRender((ModInfo)itemGroups.get(item), item);
         if (item instanceof GenericFoodItem) {
            String itemName = ((GenericFoodItem)item).itemName;
            if (item.getUnlocalizedName().contains("cooked_")) {
               OreDictionary.registerOre("listAllmeatcooked", getItem(itemName));
            } else if (itemName.contains("raw_")) {
               OreDictionary.registerOre("listAllmeatraw", getItem(itemName));
            }
         }
      }

   }

   public static void registerPotions(Register<Potion> event) {
      for(PotionBase potion : effects.values()) {
         event.getRegistry().register(potion);
      }

   }

   public static void registerSpecialEntities(Register<EntityEntry> event) {
      for(String entityName : specialEntities.keySet()) {
         String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;
         EntityEntry entityEntry = EntityEntryBuilder.create().entity((Class)specialEntities.get(entityName)).id(registryName, getNextSpecialEntityNetworkId()).name(entityName).tracker(80, 3, false).build();
         event.getRegistry().register(entityEntry);
      }

   }
}
