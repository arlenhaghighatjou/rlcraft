package vazkii.quark.world.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.world.base.EnumStonelingVariant;
import vazkii.quark.world.entity.EntityStoneling;

public class ItemDiamondHeart extends ItemMod implements IQuarkItem {
   public ItemDiamondHeart() {
      super("diamond_heart", new String[0]);
      this.setCreativeTab(CreativeTabs.MISC);
   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState stateAt = worldIn.getBlockState(pos);
      ItemStack stack = player.getHeldItem(hand);
      if (player.canPlayerEdit(pos, facing, stack) && stateAt.getBlockHardness(worldIn, pos) != -1.0F) {
         EnumStonelingVariant variant = null;

         for(EnumStonelingVariant possibleVariant : EnumStonelingVariant.values()) {
            if (possibleVariant.acceptsState(stateAt)) {
               variant = possibleVariant;
            }
         }

         if (variant != null) {
            if (!worldIn.isRemote) {
               worldIn.setBlockToAir(pos);
               worldIn.playEvent(2001, pos, Block.getStateId(stateAt));
               EntityStoneling stoneling = new EntityStoneling(worldIn);
               stoneling.setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
               stoneling.setPlayerMade(true);
               stoneling.rotationYaw = player.rotationYaw + 180.0F;
               stoneling.onInitialSpawn(worldIn.getDifficultyForLocation(pos), variant);
               worldIn.spawnEntity(stoneling);
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }
            }

            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.PASS;
   }

   @Nonnull
   public IRarity getForgeRarity(@Nonnull ItemStack stack) {
      return EnumRarity.UNCOMMON;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}
