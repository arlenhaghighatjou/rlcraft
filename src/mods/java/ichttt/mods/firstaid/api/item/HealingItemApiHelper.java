package ichttt.mods.firstaid.api.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class HealingItemApiHelper {
   static HealingItemApiHelper INSTANCE;

   public HealingItemApiHelper() {
   }

   public static void setImpl(HealingItemApiHelper impl) {
      INSTANCE = impl;
   }

   @Nonnull
   public abstract ActionResult<ItemStack> onItemRightClick(ItemHealing var1, World var2, EntityPlayer var3, EnumHand var4);

   @Nonnull
   public abstract CreativeTabs getFirstAidTab();
}
