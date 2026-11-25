package org.jwaresoftware.mcmods.armorunder.item;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jwaresoftware.mcmods.armorunder.ModInfo;
import org.jwaresoftware.mcmods.armorunder.ModItem;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.Effects;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.capability.ITemperatureChangeable;
import org.jwaresoftware.mcmods.lib.capability.ITemperatureChangeable.Category;
import org.jwaresoftware.mcmods.lib.capability.ITemperatureChangeable.TemperaturePropertyGetter;
import org.jwaresoftware.mcmods.lib.capability.ITemperatureChangeable.UsedEffect;
import org.jwaresoftware.mcmods.lib.impl.ItemSupport;

public final class GooPak extends ModItem implements ITemperatureChangeable {
   private static final String _NBT_USED_FLAG = ModInfo.rstring("Used");
   protected final UsedEffect _type;

   private GooPak(String oid, UsedEffect type) {
      super(oid);
      this._type = type;
      this.addPropertyOverride(new ResourceLocation("temperature"), TemperaturePropertyGetter.forEither());
      this.autoregister();
   }

   public static final GooPak cooling() {
      return new GooPak("goopak_cool", UsedEffect.COOLED);
   }

   public static final GooPak warming() {
      return new GooPak("goopak_heat", UsedEffect.HEATED);
   }

   public Category getTemperatureDelivery() {
      return Category.CARRIED;
   }

   public final int getCoolDuration(ItemStack pak) {
      return this.getHeatDuration(pak);
   }

   public int getTemperatureModifier(ItemStack pak) {
      return 2;
   }

   public void onCreated(ItemStack pak, World world, EntityPlayer player) {
      UsedEffect e = this.onCreateCheckBonusTemperature(pak);
      super.onCreated(pak, world, player);
      if (UsedEffect.HEATED.equals(e)) {
         this.setHeated(pak);
      } else if (UsedEffect.COOLED.equals(e)) {
         this.setCooled(pak);
      }

   }

   private boolean isUsed(ItemStack pak) {
      return pak.hasTagCompound() && pak.getTagCompound().hasKey(_NBT_USED_FLAG);
   }

   private void markUsed(ItemStack pak) {
      ItemStacks.getTagCompoundNonNull(pak).setBoolean(_NBT_USED_FLAG, true);
   }

   public boolean hasBonusTemperature(ItemStack pak) {
      return !this.isUsed(pak) && super.hasBonusTemperature(pak);
   }

   public int getItemStackLimit(ItemStack pak) {
      return this.hasBonusTemperature(pak) ? 1 : super.getItemStackLimit(pak);
   }

   private boolean canActivate(ItemStack pak) {
      if (!pak.isEmpty() && pak.getCount() <= 1) {
         return !this.hasBonusTemperature(pak);
      } else {
         return false;
      }
   }

   public EnumAction getItemUseAction(ItemStack pak) {
      return this.canActivate(pak) ? EnumAction.BOW : EnumAction.NONE;
   }

   public int getMaxItemUseDuration(ItemStack pak) {
      return this.canActivate(pak) ? SharedGlue.SHORT_FOOD_CONSUME_DURATION() : 0;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack helditem = player.getHeldItem(hand);
      EnumActionResult result = this.canActivate(helditem) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
      if (result == EnumActionResult.SUCCESS) {
         player.setActiveHand(hand);
      } else {
         Effects.playUseItemFailedSound(world, player);
      }

      return ActionResult.newResult(result, helditem);
   }

   public ItemStack onItemUseFinish(ItemStack pak, World world, EntityLivingBase entity) {
      ItemStack remaining = pak;
      EntityPlayer player = SharedGlue.getPlayerOrNull(entity);
      if (player != null) {
         ItemStack original = ItemStacks.copyItemStackSingle(pak);
         player.addStat(StatList.getObjectUseStats(this));
         remaining = this.onActivated(original, pak, world, player);
         ItemSupport.noteItemConsumed(player, original);
      }

      return remaining;
   }

   private ItemStack onActivated(ItemStack original, ItemStack pak, World world, EntityPlayer player) {
      if (SharedGlue.isaServerWorld(world)) {
         if (UsedEffect.HEATED.equals(this._type)) {
            this.setHeated(pak);
         } else {
            this.setCooled(pak);
         }
      }

      return pak;
   }

   public void onUpdate(ItemStack pak, World world, Entity holder, int slotindex, boolean selected) {
      if (this.hasBonusTemperature(pak)) {
         if (this.onUpdateTickBonusTemperature(pak, world, holder, -1, selected)) {
            EntityPlayer player = (EntityPlayer)holder;
            IItemHandler ih = (IItemHandler)SharedGlue.getCapability(player, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
            if (ih != null) {
               for(int i = 0; i < ih.getSlots(); ++i) {
                  ItemStack check = ih.getStackInSlot(i);
                  if (pak == check) {
                     this.markUsed(pak);
                     check = ih.extractItem(i, pak.getCount(), false);
                     if (!check.isEmpty()) {
                        ItemStack spent = new ItemStack(ModItems.spent_goopak);
                        ih.insertItem(i, spent, false);
                        ItemSupport.noteItemConsumed(player, spent);
                     }
                     break;
                  }
               }
            }
         }

      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || !oldStack.isItemEqual(newStack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack pak, World world, List<String> tooltip, ITooltipFlag flags) {
      if (!this.hasBonusTemperature(pak)) {
         super.addInformation(pak, world, tooltip, flags);
      } else {
         this.addBonusTemperatureInformation(pak, world, tooltip);
      }

   }
}
