package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class CompatRightClick {
   public static CompatRightClick.IRightClick cauldronHandler = new CompatRightClick.IRightClick() {
      public void process(RightClickBlock event, World world, BlockPos pos, IBlockState state, EntityPlayer player) {
         ItemStack heldItem = player.getHeldItemMainhand();
         if (heldItem.isEmpty() && player.isSneaking()) {
            if (SDCapabilities.getThirstData(player).isThirsty()) {
               int level = ((Integer)state.getValue(BlockCauldron.LEVEL)).intValue();
               if (level > 0) {
                  ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                  SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
               }
            }
         } else if (heldItem.getItem() instanceof IItemCanteen) {
            int level = ((Integer)state.getValue(BlockCauldron.LEVEL)).intValue();
            if (level > 0) {
               IItemCanteen canteen = (IItemCanteen)heldItem.getItem();
               if (canteen.tryAddDose(heldItem, ThirstEnum.NORMAL)) {
                  SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
               }
            }
         }

      }
   };

   public CompatRightClick() {
   }

   public interface IRightClick {
      void process(RightClickBlock var1, World var2, BlockPos var3, IBlockState var4, EntityPlayer var5);
   }
}
