package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Prop_Sector.IPlayerProperties;
import com.Shultrea.Rin.Prop_Sector.PlayerPropertiesProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class EnchantmentRune_Resurrection extends EnchantmentBase {
   private final Map<UUID, InventoryPlayer> INVENTORY_MAP = new HashMap();

   public EnchantmentRune_Resurrection() {
      super(Rarity.VERY_RARE, EnumList.COMBAT_GOLDEN_APPLE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("Rune_Resurrection");
      this.setRegistryName("Rune_Resurrection");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Rune_Resurrection;
   }

   public int getMaxLevel() {
      return ModConfig.level.Rune_Resurrection;
   }

   public int getMinEnchantability(int par1) {
      return 30 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 50;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onWielderDeath(LivingDeathEvent e) {
      int level = 0;
      if (e.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer diedPlayer = (EntityPlayer)e.getEntityLiving();
         if (!diedPlayer.world.isRemote) {
            List<ItemStack> list = diedPlayer.inventory.mainInventory;
            int x = 0;

            while(true) {
               if (x < list.size()) {
                  level = EnchantmentHelper.getEnchantmentLevel(Smc_040.Rune_Resurrection, (ItemStack)list.get(x));
                  if (level <= 0) {
                     if (x + 1 >= list.size()) {
                        x = 0;
                        list = diedPlayer.inventory.offHandInventory;
                     }

                     ++x;
                     continue;
                  }

                  if (diedPlayer.world.getWorldInfo().isHardcoreModeEnabled() || diedPlayer.posY < 0.0D) {
                     diedPlayer.setHealth(diedPlayer.getMaxHealth() * 0.5F * (float)level);
                     e.setCanceled(true);
                     ((ItemStack)list.get(x)).shrink(1);
                     diedPlayer.hurtResistantTime = 30;
                     return;
                  }

                  diedPlayer.setSpawnChunk(diedPlayer.getPosition(), true, diedPlayer.dimension);
                  diedPlayer.setSpawnPoint(diedPlayer.getPosition(), true);
                  BlockPos bp = diedPlayer.getBedLocation();
                  BlockPos bedSpawnPos = EntityPlayer.getBedSpawnLocation(diedPlayer.world, bp, false);
                  if (bedSpawnPos != null && bp != null) {
                     ItemStack stack = (ItemStack)list.get(x);
                     NBTTagCompound nbt = stack.serializeNBT();
                     nbt.setInteger("blockPosX", bedSpawnPos.getX());
                     nbt.setInteger("blockPosY", bedSpawnPos.getY());
                     nbt.setInteger("blockPosZ", bedSpawnPos.getZ());
                     nbt.setInteger("dimension", diedPlayer.dimension);
                     stack.deserializeNBT(nbt);
                  } else {
                     BlockPos originalWorldSpawn = diedPlayer.world.getSpawnPoint();
                     ItemStack stack = (ItemStack)list.get(x);
                     NBTTagCompound nbt = stack.serializeNBT();
                     nbt.setInteger("blockPosX", originalWorldSpawn.getX());
                     nbt.setInteger("blockPosY", originalWorldSpawn.getY());
                     nbt.setInteger("blockPosZ", originalWorldSpawn.getZ());
                     stack.deserializeNBT(nbt);
                  }

                  InventoryPlayer inv = new InventoryPlayer((EntityPlayer)null);
                  if (diedPlayer.hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null)) {
                     IPlayerProperties ar = (IPlayerProperties)diedPlayer.getCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null);
                     ar.setResurrecting(true);
                     if (!diedPlayer.world.getGameRules().getBoolean("keepInventory")) {
                        this.keepAllArmor(diedPlayer, inv);
                        this.keepOffHand(diedPlayer, inv);

                        for(int i = 0; i < diedPlayer.inventory.mainInventory.size(); ++i) {
                           inv.mainInventory.set(i, diedPlayer.inventory.mainInventory.get(i));
                           diedPlayer.inventory.mainInventory.set(i, ItemStack.EMPTY);
                        }

                        this.INVENTORY_MAP.put(diedPlayer.getUniqueID(), inv);
                     }
                  }
               }

               return;
            }
         }
      }
   }

   @SubscribeEvent
   public void onItemDrop(PlayerDropsEvent e) {
      int level = 0;
      EntityPlayer player = e.getEntityPlayer();
      List<EntityItem> lista = e.getDrops();
      if (player.hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null)) {
         IPlayerProperties p = (IPlayerProperties)player.getCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null);
         if (p.isResurrecting()) {
            e.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onPlayerRessurect(Clone e) {
      if (e.isWasDeath()) {
         EntityPlayer player = e.getOriginal();
         if (player.hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null) && e.getEntityPlayer().hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null)) {
            IPlayerProperties p = (IPlayerProperties)player.getCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null);
            IPlayerProperties cap = (IPlayerProperties)e.getEntityPlayer().getCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null);
            if (p.isResurrecting()) {
               cap.setResurrecting(true);
               return;
            }
         }

      }
   }

   @SubscribeEvent
   public void teleportLastDeathLocation(PlayerRespawnEvent e) {
      if (!e.isEndConquered()) {
         EntityPlayer player = e.player;
         this.regenerateItems(player);
         if (player.hasCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null)) {
            InventoryPlayer inv = null;
            new ArrayList();
            IPlayerProperties p = (IPlayerProperties)player.getCapability(PlayerPropertiesProvider.PLAYERPROPERTIES_CAP, (EnumFacing)null);
            inv = player.inventory;
            List<ItemStack> list = inv.mainInventory;
            boolean resurrect = p.isResurrecting();
            if (resurrect) {
               p.setResurrecting(false);
            }

            if (resurrect && inv != null && !player.world.getGameRules().getBoolean("keepInventory")) {
               if (EnchantmentHelper.getEnchantmentLevel(Smc_040.Rune_Resurrection, player.getHeldItemOffhand()) > 0) {
                  player.getHeldItemOffhand().shrink(1);
               } else {
                  for(int x = 0; x < list.size(); ++x) {
                     int level = EnchantmentHelper.getEnchantmentLevel(Smc_040.Rune_Resurrection, (ItemStack)list.get(x));
                     if (level > 0) {
                        player.addStat(StatList.DEATHS, -1);
                        ItemStack stack = (ItemStack)list.get(x);
                        NBTTagCompound nbt = stack.serializeNBT();
                        BlockPos pos = new BlockPos(nbt.getInteger("blockPosX"), nbt.getInteger("blockPosY"), nbt.getInteger("blockPosZ"));
                        player.setSpawnChunk(pos, false, nbt.getInteger("dimension"));
                        player.setSpawnPoint(pos, false);
                        ((ItemStack)list.get(x)).shrink(1);
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public String getPrefix() {
      return TextFormatting.GREEN.toString();
   }

   private InventoryPlayer regenerateItems(EntityPlayer player) {
      InventoryPlayer keepInventory = (InventoryPlayer)this.INVENTORY_MAP.remove(player.getUniqueID());
      if (keepInventory != null) {
         NonNullList<ItemStack> displaced = NonNullList.create();

         for(int i = 0; i < player.inventory.armorInventory.size(); ++i) {
            ItemStack kept = (ItemStack)keepInventory.armorInventory.get(i);
            if (!kept.isEmpty()) {
               ItemStack existing = (ItemStack)player.inventory.armorInventory.set(i, kept);
               if (!existing.isEmpty()) {
                  displaced.add(existing);
               }
            }
         }

         for(int i = 0; i < player.inventory.offHandInventory.size(); ++i) {
            ItemStack kept = (ItemStack)keepInventory.offHandInventory.get(i);
            if (!kept.isEmpty()) {
               ItemStack existing = (ItemStack)player.inventory.offHandInventory.set(i, kept);
               if (!existing.isEmpty()) {
                  displaced.add(existing);
               }
            }
         }

         for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            ItemStack kept = (ItemStack)keepInventory.mainInventory.get(i);
            if (!kept.isEmpty()) {
               ItemStack existing = (ItemStack)player.inventory.mainInventory.set(i, kept);
               if (!existing.isEmpty()) {
                  displaced.add(existing);
               }
            }
         }

         for(ItemStack extra : displaced) {
            ItemHandlerHelper.giveItemToPlayer(player, extra);
         }
      }

      return keepInventory;
   }

   private void keepAllArmor(EntityPlayer player, InventoryPlayer keepInventory) {
      for(int i = 0; i < player.inventory.armorInventory.size(); ++i) {
         keepInventory.armorInventory.set(i, ((ItemStack)player.inventory.armorInventory.get(i)).copy());
         player.inventory.armorInventory.set(i, ItemStack.EMPTY);
      }

   }

   private void keepOffHand(EntityPlayer player, InventoryPlayer keepInventory) {
      for(int i = 0; i < player.inventory.offHandInventory.size(); ++i) {
         keepInventory.offHandInventory.set(i, ((ItemStack)player.inventory.offHandInventory.get(i)).copy());
         player.inventory.offHandInventory.set(i, ItemStack.EMPTY);
      }

   }
}
