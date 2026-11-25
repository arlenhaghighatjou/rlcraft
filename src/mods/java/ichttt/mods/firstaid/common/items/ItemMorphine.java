package ichttt.mods.firstaid.common.items;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class ItemMorphine extends Item {
   public ItemMorphine() {
      this.setMaxStackSize(16);
      this.setRegistryName(new ResourceLocation("firstaid", "morphine"));
      this.setUnlocalizedName("morphine");
      this.setCreativeTab(FirstAid.CREATIVE_TAB);
   }

   @Nonnull
   public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving) {
      if (entityLiving instanceof EntityPlayer && !(entityLiving instanceof FakePlayer)) {
         AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)entityLiving.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
         ((AbstractPlayerDamageModel)Objects.requireNonNull(damageModel)).applyMorphine((EntityPlayer)entityLiving);
      }

      stack.shrink(1);
      return stack;
   }

   @Nonnull
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 40;
   }
}
