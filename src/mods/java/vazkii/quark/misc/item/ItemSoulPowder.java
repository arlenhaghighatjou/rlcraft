package vazkii.quark.misc.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.entity.EntitySoulPowder;

public class ItemSoulPowder extends ItemMod implements IQuarkItem {
   public ItemSoulPowder() {
      super("soul_powder", new String[0]);
      this.setCreativeTab(CreativeTabs.MISC);
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote) {
         BlockPos blockpos = playerIn.getEntityWorld().findNearestStructure("Fortress", playerIn.getPosition(), false);
         if (blockpos != null) {
            itemstack.shrink(1);
            EntitySoulPowder entity = new EntitySoulPowder(worldIn, blockpos.getX(), blockpos.getZ());
            Vec3d look = playerIn.getLookVec();
            entity.setPosition(playerIn.posX + look.x * 2.0D, playerIn.posY + 0.25D, playerIn.posZ + look.z * 2.0D);
            worldIn.spawnEntity(entity);
            worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, QuarkSounds.ITEM_SOUL_POWDER_SPAWN, SoundCategory.PLAYERS, 1.0F, 1.0F);
         }
      } else {
         playerIn.swingArm(handIn);
      }

      playerIn.addStat(StatList.getObjectUseStats(this));
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}
