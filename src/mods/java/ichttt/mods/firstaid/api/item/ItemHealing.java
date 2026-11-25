package ichttt.mods.firstaid.api.item;

import ichttt.mods.firstaid.api.FirstAidRegistry;
import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemHealing extends Item {
   public ItemHealing(Function<ItemStack, AbstractPartHealer> healerFunction, Function<ItemStack, Integer> time) {
      this.setCreativeTab(HealingItemApiHelper.INSTANCE.getFirstAidTab());
      ((FirstAidRegistry)Objects.requireNonNull(FirstAidRegistry.getImpl(), "FirstAid not loaded or not present!")).registerHealingType(this, healerFunction, time);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
      return HealingItemApiHelper.INSTANCE.onItemRightClick(this, worldIn, playerIn, handIn);
   }
}
