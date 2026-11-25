package svenhjol.charm.world.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.world.entity.EntityEndermitePowder;
import svenhjol.meson.MesonItem;
import svenhjol.meson.helper.WorldHelper;

public class ItemEndermitePowder extends MesonItem {
   public ItemEndermitePowder() {
      super("endermite_powder");
      this.setCreativeTab(CreativeTabs.MISC);
   }

   public String getModId() {
      return "charm";
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack stack = playerIn.getHeldItem(handIn);
      if (worldIn.provider.getDimension() != 1) {
         return new ActionResult(EnumActionResult.FAIL, stack);
      } else {
         if (!playerIn.capabilities.isCreativeMode) {
            stack.shrink(1);
         }

         if (worldIn.isRemote) {
            playerIn.swingArm(handIn);
         }

         if (!worldIn.isRemote) {
            BlockPos pos = WorldHelper.getNearestStructure(worldIn, playerIn.getPosition(), "EndCity");
            if (pos != null) {
               EntityEndermitePowder entity = new EntityEndermitePowder(worldIn, pos.getX(), pos.getZ());
               Vec3d look = playerIn.getLookVec();
               entity.setPosition(playerIn.posX + look.x * 2.0D, playerIn.posY + 0.5D, playerIn.posZ + look.z * 2.0D);
               worldIn.spawnEntity(entity);
               worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, CharmSounds.ENDER_WHISPERS, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
         }

         StatBase stats = StatList.getObjectUseStats(this);
         if (stats != null) {
            playerIn.addStat(stats);
         }

         return new ActionResult(EnumActionResult.SUCCESS, stack);
      }
   }
}
