package net.theawesomegem.fishingmadebetter.common.item.attachment.bobber;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemBobber extends Item {
   protected final boolean lavaBobber;
   protected final boolean voidBobber;
   protected final int varianceModifier;
   protected final int tensioningModifier;

   public ItemBobber(String name, int maxDamage, boolean lavaBobber, boolean voidBobber, int varianceModifier, int tensioningModifier) {
      this.lavaBobber = lavaBobber;
      this.voidBobber = voidBobber;
      this.varianceModifier = varianceModifier;
      this.tensioningModifier = tensioningModifier;
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setNoRepair();
      this.setMaxStackSize(1);
      this.setMaxDamage(maxDamage);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   public boolean isLavaBobber() {
      return this.lavaBobber;
   }

   public boolean isVoidBobber() {
      return this.voidBobber;
   }

   public int getVarianceModifier() {
      return this.varianceModifier;
   }

   public int getTensioningModifier() {
      return this.tensioningModifier;
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (this.lavaBobber) {
         tooltip.add(I18n.format("item.fishingmadebetter.bobber_obsidian.tooltip", new Object[0]));
      }

      if (this.voidBobber) {
         tooltip.add(I18n.format("item.fishingmadebetter.bobber_void.tooltip", new Object[0]));
      }

      if (!this.lavaBobber && !this.voidBobber) {
         tooltip.add(I18n.format("item.fishingmadebetter.bobber_water.tooltip", new Object[0]));
      }

      if (this.varianceModifier != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.bobber_heavy.tooltip", new Object[0]) + ": +" + this.varianceModifier);
      }

      if (this.tensioningModifier != 0) {
         tooltip.add(I18n.format("item.fishingmadebetter.bobber_lightweight.tooltip", new Object[0]) + ": +" + this.tensioningModifier);
      }

   }
}
