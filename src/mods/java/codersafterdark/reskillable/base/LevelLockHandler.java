package codersafterdark.reskillable.base;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.data.FuzzyLockKey;
import codersafterdark.reskillable.api.data.GenericLockKey;
import codersafterdark.reskillable.api.data.GenericNBTLockKey;
import codersafterdark.reskillable.api.data.ItemInfo;
import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.data.ModLockKey;
import codersafterdark.reskillable.api.data.ParentLockKey;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.network.MessageLockedItem;
import codersafterdark.reskillable.network.PacketHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class LevelLockHandler {
   public static final String[] DEFAULT_SKILL_LOCKS = new String[]{"minecraft:iron_shovel:*=reskillable:gathering|5", "minecraft:iron_axe:*=reskillable:gathering|5", "minecraft:iron_sword:*=reskillable:attack|5", "minecraft:iron_pickaxe:*=reskillable:mining|5", "minecraft:iron_hoe:*=reskillable:farming|5", "minecraft:iron_helmet:*=reskillable:defense|5", "minecraft:iron_chestplate:*=reskillable:defense|5", "minecraft:iron_leggings:*=reskillable:defense|5", "minecraft:iron_boots:*=reskillable:defense|5", "minecraft:golden_shovel:*=reskillable:gathering|5,reskillable:magic|5", "minecraft:golden_axe:*=reskillable:gathering|5,reskillable:magic|5", "minecraft:golden_sword:*=reskillable:attack|5,reskillable:magic|5", "minecraft:golden_pickaxe:*=reskillable:mining|5,reskillable:magic|5", "minecraft:golden_hoe:*=reskillable:farming|5,reskillable:magic|5", "minecraft:golden_helmet:*=reskillable:defense|5,reskillable:magic|5", "minecraft:golden_chestplate:*=reskillable:defense|5,reskillable:magic|5", "minecraft:golden_leggings:*=reskillable:defense|5,reskillable:magic|5", "minecraft:golden_boots:*=reskillable:defense|5,reskillable:magic|5", "minecraft:diamond_shovel:*=reskillable:gathering|16", "minecraft:diamond_axe:*=reskillable:gathering|16", "minecraft:diamond_sword:*=reskillable:attack|16", "minecraft:diamond_pickaxe:*=reskillable:mining|16", "minecraft:diamond_hoe:*=reskillable:farming|16", "minecraft:diamond_helmet:*=reskillable:defense|16", "minecraft:diamond_chestplate:*=reskillable:defense|16", "minecraft:diamond_leggings:*=reskillable:defense|16", "minecraft:diamond_boots:*=reskillable:defense|16", "minecraft:shears:*=reskillable:farming|5,reskillable:gathering|5", "minecraft:fishing_rod:*=reskillable:gathering|8", "minecraft:shield:*=reskillable:defense|8", "minecraft:bow:*=reskillable:attack|8", "minecraft:ender_pearl=reskillable:magic|8", "minecraft:ender_eye=reskillable:magic|16,reskillable:building|8", "minecraft:elytra:*=reskillable:defense|16,reskillable:agility|24,reskillable:magic|16", "minecraft:lead=reskillable:farming|5", "minecraft:end_crystal=reskillable:building|24,reskillable:magic|32", "minecraft:iron_horse_armor:*=reskillable:defense|5,reskillable:agility|5", "minecraft:golden_horse_armor:*=reskillable:defense|5,reskillable:magic|5,reskillable:agility|5", "minecraft:diamond_horse_armor:*=reskillable:defense|16,reskillable:agility|16", "minecraft:fireworks=reskillable:agility|24", "minecraft:dye:15=reskillable:farming|12", "minecraft:saddle=reskillable:agility|12", "minecraft:redstone=reskillable:building|5", "minecraft:redstone_torch=reskillable:building|5", "minecraft:skull:1=reskillable:building|20,reskillable:attack|20,reskillable:defense|20"};
   private static final Map<LockKey, RequirementHolder> locks = new HashMap();
   public static RequirementHolder EMPTY_LOCK = new RequirementHolder();
   private static Map<Class<?>, List<Class<? extends LockKey>>> lockTypesMap = new HashMap();
   private static Map<LockKey, Set<FuzzyLockKey>> fuzzyLockInfo = new HashMap();
   private static String[] configLocks;

   public LevelLockHandler() {
   }

   public static void loadFromConfig(String[] configValues) {
      configLocks = configValues;
   }

   public static void setupLocks() {
      registerDefaultLockKeys();
      if (configLocks != null) {
         for(String s : configLocks) {
            String[] tokens = s.split("=");
            if (tokens.length == 2) {
               String itemName = tokens[0].toLowerCase();
               String[] itemParts = itemName.split(":");
               if (itemParts.length == 1) {
                  addModLock(itemName, RequirementHolder.fromString(tokens[1]));
               } else {
                  int metadata = 0;
                  if (itemParts.length > 2) {
                     String meta = itemParts[2];

                     try {
                        if (meta.equals("*")) {
                           metadata = 32767;
                        } else {
                           metadata = Integer.parseInt(meta);
                        }

                        itemName = itemParts[0] + ':' + itemParts[1];
                     } catch (NumberFormatException var10) {
                        ;
                     }
                  }

                  Item item = Item.getByNameOrId(itemName);
                  if (item != null) {
                     addLock(new ItemStack(item, 1, metadata), RequirementHolder.fromString(tokens[1]));
                  }
               }
            }
         }
      }

   }

   private static void registerDefaultLockKeys() {
      registerLockKey(ItemStack.class, ItemInfo.class, ModLockKey.class, GenericNBTLockKey.class);
   }

   public static void registerLockKey(Class<?> lockTypeClass, Class<? extends LockKey>... keyClasses) {
      for(Class<? extends LockKey> keyClass : keyClasses) {
         try {
            keyClass.getDeclaredConstructor(lockTypeClass);
            ((List)lockTypesMap.computeIfAbsent(lockTypeClass, (k) -> {
               return (void)(new ArrayList());
            })).add(keyClass);
         } catch (SecurityException | NoSuchMethodException var7) {
            ReskillableAPI.getInstance().log(Level.ERROR, keyClass.getSimpleName() + " does not have a constructor with the parameter: " + lockTypeClass.getSimpleName());
         }
      }

   }

   public static void addLockByKey(LockKey key, RequirementHolder holder) {
      if (key != null && !(key instanceof GenericLockKey)) {
         if (holder != null && !holder.equals(EMPTY_LOCK) && holder.getRestrictionLength() != 0) {
            locks.put(key, holder);
            if (key instanceof FuzzyLockKey) {
               FuzzyLockKey fuzzy = (FuzzyLockKey)key;
               if (!fuzzy.isNotFuzzy()) {
                  LockKey without = fuzzy.getNotFuzzy();
                  if (without == null) {
                     without = new GenericLockKey(key.getClass());
                  }

                  ((Set)fuzzyLockInfo.computeIfAbsent(without, (k) -> {
                     return new HashSet();
                  })).add(fuzzy);
               }
            }

            ToolTipHandler.resetLast();
         }
      }
   }

   public static void addModLock(String modName, RequirementHolder holder) {
      addLockByKey(new ModLockKey(modName), holder);
   }

   public static void addLock(ItemStack stack, RequirementHolder holder) {
      addLockByKey(new ItemInfo(stack), holder);
   }

   public static RequirementHolder getLockByKey(LockKey key) {
      if (locks.containsKey(key)) {
         RequirementHolder holder = (RequirementHolder)locks.get(key);
         if (!holder.hasNone()) {
            return holder;
         }
      }

      return EMPTY_LOCK;
   }

   public static RequirementHolder getLockByFuzzyKey(FuzzyLockKey key) {
      List<RequirementHolder> requirements = getFuzzyRequirements(key);
      if (requirements.isEmpty()) {
         return EMPTY_LOCK;
      } else {
         RequirementHolder holder = new RequirementHolder((RequirementHolder[])requirements.toArray(new RequirementHolder[0]));
         return holder.hasNone() ? EMPTY_LOCK : holder;
      }
   }

   private static List<RequirementHolder> getFuzzyRequirements(FuzzyLockKey key) {
      List<RequirementHolder> requirements = new ArrayList();
      if (!key.isNotFuzzy()) {
         LockKey baseLock = key.getNotFuzzy();
         if (baseLock == null) {
            baseLock = new GenericLockKey(key.getClass());
         } else if (locks.containsKey(baseLock)) {
            RequirementHolder holder = (RequirementHolder)locks.get(baseLock);
            if (holder.hasNone()) {
               return Collections.singletonList(holder);
            }

            requirements.add(holder);
         }

         Set<FuzzyLockKey> fuzzyLookup = (Set)fuzzyLockInfo.get(baseLock);
         if (fuzzyLookup != null) {
            for(FuzzyLockKey fuzzyLock : fuzzyLookup) {
               if (key.fuzzyEquals(fuzzyLock) && locks.containsKey(fuzzyLock)) {
                  RequirementHolder holder = (RequirementHolder)locks.get(fuzzyLock);
                  if (holder.hasNone()) {
                     return Collections.singletonList(holder);
                  }

                  requirements.add(holder);
               }
            }
         }
      } else if (locks.containsKey(key)) {
         RequirementHolder holder = (RequirementHolder)locks.get(key);
         if (holder.hasNone()) {
            return Collections.singletonList(holder);
         }

         requirements.add(holder);
      }

      return requirements;
   }

   public static RequirementHolder getSkillLock(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? getLocks(ItemStack.class, stack) : EMPTY_LOCK;
   }

   @SafeVarargs
   public static <T> RequirementHolder getLocks(Class<? extends T> classType, T... tToCheck) {
      if (tToCheck != null && tToCheck.length != 0 && lockTypesMap.containsKey(classType)) {
         List<Class<? extends LockKey>> lockTypes = (List)lockTypesMap.get(classType);
         List<RequirementHolder> requirements = new ArrayList();

         for(T toCheck : tToCheck) {
            for(Class<? extends LockKey> keyClass : lockTypes) {
               LockKey lock;
               try {
                  lock = (LockKey)keyClass.getDeclaredConstructor(classType).newInstance(toCheck);
               } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException var12) {
                  var12.printStackTrace();
                  continue;
               }

               if (lock instanceof FuzzyLockKey) {
                  requirements.addAll(getFuzzyRequirements((FuzzyLockKey)lock));
               } else if (locks.containsKey(lock)) {
                  RequirementHolder holder = (RequirementHolder)locks.get(lock);
                  if (holder.hasNone()) {
                     return EMPTY_LOCK;
                  }

                  requirements.add(holder);
               }

               if (lock instanceof ParentLockKey) {
                  RequirementHolder subLocks = ((ParentLockKey)lock).getSubRequirements();
                  if (subLocks != null && !subLocks.equals(EMPTY_LOCK)) {
                     if (subLocks.hasNone()) {
                        return EMPTY_LOCK;
                     }

                     requirements.add(subLocks);
                  }
               }
            }
         }

         if (requirements.isEmpty()) {
            return EMPTY_LOCK;
         } else {
            RequirementHolder holder = new RequirementHolder((RequirementHolder[])requirements.toArray(new RequirementHolder[0]));
            return holder.hasNone() ? EMPTY_LOCK : holder;
         }
      } else {
         return EMPTY_LOCK;
      }
   }

   public static boolean canPlayerUseItem(EntityPlayer player, ItemStack stack) {
      RequirementHolder lock = getSkillLock(stack);
      return lock.equals(EMPTY_LOCK) || PlayerDataHandler.get(player).matchStats(lock);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void hurtEvent(LivingAttackEvent event) {
      if (!event.isCanceled() && event.getSource().getTrueSource() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
         genericEnforce(event, player, player.getHeldItemMainhand(), "reskillable.misc.locked.item");
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void leftClick(LeftClickBlock event) {
      enforce(event);
      if (!event.isCanceled()) {
         IBlockState state = event.getWorld().getBlockState(event.getPos());
         Block block = state.getBlock();
         ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
         if (stack.isEmpty()) {
            stack = block.getItem(event.getWorld(), event.getPos(), state);
         }

         if (block.hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
               stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
         }

         genericEnforce(event, event.getEntityPlayer(), stack, "reskillable.misc.locked.block_break");
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void rightClickItem(RightClickItem event) {
      enforce(event);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void rightClickBlock(RightClickBlock event) {
      enforce(event);
      if (!event.isCanceled()) {
         if (event.getItemStack().isEmpty()) {
            EntityPlayer player = event.getEntityPlayer();
            genericEnforce(event, player, event.getHand().equals(EnumHand.MAIN_HAND) ? player.getHeldItemOffhand() : player.getHeldItemMainhand(), "reskillable.misc.locked.item");
            if (event.isCanceled()) {
               return;
            }
         }

         IBlockState state = event.getWorld().getBlockState(event.getPos());
         Block block = state.getBlock();
         ItemStack stack = new ItemStack(block, 1, state.getBlock().getMetaFromState(state));
         if (stack.isEmpty()) {
            stack = block.getItem(event.getWorld(), event.getPos(), state);
         }

         if (block.hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
               stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
         }

         genericEnforce(event, event.getEntityPlayer(), stack, "reskillable.misc.locked.block_use");
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onBlockBreak(BreakEvent event) {
      if (!event.isCanceled()) {
         IBlockState state = event.getWorld().getBlockState(event.getPos());
         ItemStack stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
         if (state.getBlock().hasTileEntity(state)) {
            TileEntity te = event.getWorld().getTileEntity(event.getPos());
            if (te != null && !te.isInvalid()) {
               stack.setTagCompound(te.writeToNBT(new NBTTagCompound()));
            }
         }

         genericEnforce(event, event.getPlayer(), stack, "reskillable.misc.locked.block_break");
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void entityInteract(EntityInteract event) {
      enforce(event);
   }

   @SubscribeEvent
   public static void onArmorEquip(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if ((ConfigHandler.enforceOnCreative || !player.isCreative()) && !isFake(player)) {
            EntityEquipmentSlot slot = event.getSlot();
            if (slot.getSlotType().equals(Type.ARMOR)) {
               ItemStack stack = (ItemStack)player.inventory.armorInventory.get(slot.getIndex());
               if (!canPlayerUseItem(player, stack)) {
                  if (!player.inventory.addItemStackToInventory(stack)) {
                     player.dropItem(stack, false);
                  }

                  player.inventory.armorInventory.set(slot.getIndex(), ItemStack.EMPTY);
                  tellPlayer(player, stack, "reskillable.misc.locked.armor_equip");
               }
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onEntityDrops(LivingDropsEvent event) {
      if (!event.isCanceled() && ConfigHandler.disableSheepWool && event.getEntity() instanceof EntitySheep) {
         event.getDrops().removeIf((e) -> {
            return e.getItem().getItem() == Item.getItemFromBlock(Blocks.WOOL);
         });
      }

   }

   public static boolean isFake(Entity e) {
      return e instanceof FakePlayer;
   }

   public static void enforce(PlayerInteractEvent event) {
      genericEnforce(event, event.getEntityPlayer(), event.getItemStack(), "reskillable.misc.locked.item");
   }

   public static void genericEnforce(Event event, EntityPlayer player, ItemStack stack, String lockMessage) {
      if (event.isCancelable() && !event.isCanceled() && player != null && stack != null && !stack.isEmpty() && (ConfigHandler.enforceOnCreative || !player.isCreative())) {
         if (ConfigHandler.enforceFakePlayers) {
            if (!canPlayerUseItem(player, stack)) {
               event.setCanceled(true);
               if (!isFake(player)) {
                  tellPlayer(player, stack, lockMessage);
               }
            }
         } else if (!isFake(player) && !canPlayerUseItem(player, stack)) {
            tellPlayer(player, stack, lockMessage);
            event.setCanceled(true);
         }

      }
   }

   public static void tellPlayer(EntityPlayer player, ItemStack stack, String msg) {
      if (player instanceof EntityPlayerMP) {
         PacketHandler.INSTANCE.sendTo(new MessageLockedItem(stack, msg), (EntityPlayerMP)player);
      }

   }
}
