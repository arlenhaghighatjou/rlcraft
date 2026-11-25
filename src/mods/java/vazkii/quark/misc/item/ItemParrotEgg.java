package vazkii.quark.misc.item;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.entity.EntityParrotEgg;

public class ItemParrotEgg extends ItemMod implements IQuarkItem, IItemColorProvider {
   private static final String[] VARIANTS = new String[]{"red_parrot_egg", "blue_parrot_egg", "green_parrot_egg", "cyan_parrot_egg", "gray_parrot_egg"};
   private static final int[][] COLORS = new int[][]{{11665920, 24247}, {927435, 266318}, {10213633, 4349952}, {1608631, 16700165}, {11250603, 6381921}};

   public ItemParrotEgg() {
      super("parrot_egg", VARIANTS);
      this.setCreativeTab(CreativeTabs.MATERIALS);
      this.setMaxStackSize(16);
   }

   public IItemColor getItemColor() {
      return (stack, tintIndex) -> {
         return COLORS[Math.min(COLORS.length - 1, stack.getItemDamage())][tintIndex];
      };
   }

   public String getUniqueModel() {
      return "parrot_egg";
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!playerIn.capabilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         EntityParrotEgg entityegg = new EntityParrotEgg(worldIn, playerIn, itemstack.getItemDamage());
         entityegg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
         worldIn.spawnEntity(entityegg);
      }

      playerIn.addStat(StatList.getObjectUseStats(this));
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}
