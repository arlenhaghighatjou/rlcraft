package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.PortalEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaffSummoning extends ItemScepter {
   public ItemStaffSummoning(String itemName, String textureName) {
      this.itemName = itemName;
      this.setup();
      this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
            return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
         }
      });
   }

   public int getDurability() {
      return 500;
   }

   public void damageItemRapid(ItemStack itemStack, EntityLivingBase entity) {
   }

   public void damageItemCharged(ItemStack itemStack, EntityLivingBase entity, float power) {
      ExtendedPlayer playerExt = null;
      if (entity instanceof EntityPlayer) {
         playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
      }

      if (playerExt != null && playerExt.staffPortal != null) {
         this.damage_item(itemStack, playerExt.staffPortal.summonAmount, entity);
      }

   }

   public int getChargeTime(ItemStack itemStack) {
      return 1;
   }

   public int getRapidTime(ItemStack itemStack) {
      return 20;
   }

   public int getSummonCostBoost() {
      return 0;
   }

   public float getSummonCostMod() {
      return 1.0F;
   }

   public int getSummonDuration() {
      return 1200;
   }

   public int getSummonAmount() {
      return 1;
   }

   public boolean getAdditionalCosts(EntityPlayer player) {
      return true;
   }

   public void applyMinionBehaviour(TameableCreatureEntity minion, EntityPlayer player) {
      SummonSet summonSet = ExtendedPlayer.getForPlayer(player).getSelectedSummonSet();
      summonSet.applyBehaviour(minion);
      minion.setSubspecies(summonSet.subspecies);
      minion.applyVariant(summonSet.variant);
   }

   public void applyMinionEffects(BaseCreatureEntity minion) {
   }

   public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entity, int useRemaining) {
      super.onPlayerStoppedUsing(itemStack, world, entity, useRemaining);
      ExtendedPlayer playerExt = null;
      if (entity instanceof EntityPlayer) {
         playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
      }

      if (playerExt != null) {
         playerExt.staffPortal = null;
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      if (!CreatureManager.getInstance().config.isSummoningAllowed(world)) {
         return new ActionResult(EnumActionResult.FAIL, player.getHeldItem(hand));
      } else {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (playerExt != null) {
            SummonSet summonSet = playerExt.getSelectedSummonSet();
            if (summonSet.isUseable()) {
               if (!player.getEntityWorld().isRemote) {
                  playerExt.staffPortal = new PortalEntity(world, player, summonSet.getCreatureClass(), this);
                  playerExt.staffPortal.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
                  world.spawnEntity(playerExt.staffPortal);
               }
            } else {
               playerExt.staffPortal = null;
               if (!player.getEntityWorld().isRemote) {
                  playerExt.sendAllSummonSetsToPlayer();
               }

               if (player.getEntityWorld().isRemote) {
                  SummoningBeastiaryScreen.openToPlayer(player);
               }
            }
         }

         return super.onItemRightClick(world, player, hand);
      }
   }

   public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
      return false;
   }

   private void damage_item(ItemStack itemStack, int amountToDamage, EntityLivingBase entity) {
      itemStack.damageItem(amountToDamage, entity);
      if (itemStack.getCount() == 0) {
         if (entity.getHeldItem(EnumHand.MAIN_HAND).equals(itemStack)) {
            entity.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
         } else if (entity.getHeldItem(EnumHand.OFF_HAND).equals(itemStack)) {
            entity.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
         }
      }

   }

   public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
      ExtendedPlayer playerExt = null;
      if (entity instanceof EntityPlayer) {
         playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)entity);
      }

      if (playerExt != null && playerExt.staffPortal != null) {
         int successCount = playerExt.staffPortal.summonCreatures();
         this.damage_item(itemStack, successCount, entity);
         return successCount > 0;
      } else {
         return false;
      }
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return repairStack.getItem() == Items.GOLD_INGOT ? true : super.getIsRepairable(itemStack, repairStack);
   }
}
