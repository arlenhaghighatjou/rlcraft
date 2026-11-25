package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EnchantmentCurseofPossession extends EnchantmentBase implements IEnchantmentCurse {
   public static int interval;
   public Map<Integer, Integer> dimensionMap;

   public EnchantmentCurseofPossession() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
      this.setName("CurseofPossession");
      this.setRegistryName("CurseofPossession");
      this.dimensionMap = new HashMap();
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CurseofPossession;
   }

   public int getMinEnchantability(int par1) {
      return 25 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 25;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentCurseofDecay);
   }

   public boolean isAllowedOnBooks() {
      return false;
   }

   public boolean isCurse() {
      return true;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   private void damagePlayer(EntityPlayer player) {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void toss(ItemTossEvent event) {
      EntityItem item = event.getEntityItem();
      ItemStack theThrown = item.getItem().copy();
      int l = EnchantmentHelper.getEnchantmentLevel(this, theThrown);
      if (l > 0) {
         EntityPlayer player = event.getPlayer();
         if (!player.isCreative()) {
            boolean flag = player.inventory.addItemStackToInventory(theThrown);
            if (!flag) {
               EntityItem entityItem = player.entityDropItem(theThrown, 1.3F);
               entityItem.setOwner(player.getName());
               entityItem.setNoPickupDelay();
               entityItem.setEntityInvulnerable(true);
            } else {
               this.damagePlayer(player);
            }

            event.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onExist(WorldTickEvent event) {
      if (event.phase != Phase.START) {
         int dimension = event.world.provider.getDimension();
         Integer dimensionCounterObj = (Integer)this.dimensionMap.get(Integer.valueOf(dimension));
         int dimCount = dimensionCounterObj == null ? 0 : dimensionCounterObj.intValue();
         ++dimCount;
         if (dimCount <= 20) {
            this.dimensionMap.put(Integer.valueOf(dimension), Integer.valueOf(dimCount));
         } else {
            this.dimensionMap.put(Integer.valueOf(dimension), Integer.valueOf(0));
            if (interval > 3) {
               List<Entity> list = event.world.loadedEntityList;
               if (list == null || list.isEmpty()) {
                  return;
               }

               for(int x = 0; x < list.size(); ++x) {
                  Entity listGet = (Entity)list.get(x);
                  if (listGet instanceof EntityItem) {
                     EntityItem item = (EntityItem)listGet;
                     ItemStack stack = item.getItem();
                     if (item.ticksExisted > 3) {
                        if (Smc_040.CurseofDecay != null && EnchantmentHelper.getEnchantmentLevel(Smc_040.CurseofDecay, stack) > 0) {
                           item.lifespan = 40;
                           item.setPickupDelay(10);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(this, stack) > 0) {
                           if (EnchantmentHelper.getEnchantmentLevel(Smc_040.CurseofDecay, stack) <= 0) {
                              item.setNoDespawn();
                           }

                           EntityPlayer thrower = event.world.getClosestPlayerToEntity(item, 32.0D);
                           if (thrower != null && !thrower.isCreative()) {
                              boolean flag = thrower.addItemStackToInventory(stack);
                              if (flag) {
                                 list.remove(x);
                                 this.damagePlayer(thrower);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               ++interval;
            }

         }
      }
   }
}
