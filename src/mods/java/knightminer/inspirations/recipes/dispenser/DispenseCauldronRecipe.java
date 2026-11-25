package knightminer.inspirations.recipes.dispenser;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseCauldronRecipe extends BehaviorDefaultDispenseItem {
   private static final BehaviorDefaultDispenseItem DEFAULT = new BehaviorDefaultDispenseItem();
   private IBehaviorDispenseItem fallback;
   private int[] validMeta;

   public DispenseCauldronRecipe(IBehaviorDispenseItem fallback, int... validMeta) {
      this.fallback = fallback;
      this.validMeta = validMeta;
   }

   private boolean isMetaValid(ItemStack stack) {
      if (this.validMeta.length == 0) {
         return true;
      } else {
         int meta = stack.getMetadata();

         for(int i : this.validMeta) {
            if (i == meta) {
               return true;
            }
         }

         return false;
      }
   }

   protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
      if (!this.isMetaValid(stack)) {
         return this.fallback.dispense(source, stack);
      } else {
         EnumFacing side = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
         BlockPos pos = source.getBlockPos().offset(side);
         World world = source.getWorld();
         IBlockState state = world.getBlockState(pos);
         if (!InspirationsRegistry.isNormalCauldron(state)) {
            return this.fallback.dispense(source, stack);
         } else {
            TileCauldron cauldron = null;
            ICauldronRecipe.CauldronState cauldronState = ICauldronRecipe.CauldronState.WATER;
            boolean boiling = false;
            Block block = state.getBlock();
            if (Config.enableExtendedCauldron && block instanceof BlockEnhancedCauldron) {
               TileEntity te = world.getTileEntity(pos);
               if (te instanceof TileCauldron) {
                  cauldron = (TileCauldron)te;
                  cauldronState = cauldron.getState();
                  boiling = ((Boolean)state.getValue(BlockEnhancedCauldron.BOILING)).booleanValue();
               }
            } else {
               cauldronState = InspirationsRegistry.getCauldronState(state);
               boiling = InspirationsRegistry.isCauldronFire(world.getBlockState(pos.down()));
            }

            int level = BlockEnhancedCauldron.getCauldronLevel(state);
            ICauldronRecipe recipe = InspirationsRegistry.getCauldronResult(stack, boiling, level, cauldronState);
            if (recipe == null) {
               return DEFAULT.dispense(source, stack);
            } else {
               ICauldronRecipe.CauldronState newState = recipe.getState(stack, boiling, level, cauldronState);
               if (!Config.enableExtendedCauldron && !ICauldronRecipe.CauldronState.WATER.matches(newState)) {
                  return DEFAULT.dispense(source, stack);
               } else {
                  SoundEvent sound = recipe.getSound(stack, boiling, level, cauldronState);
                  if (sound != null) {
                     world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, recipe.getVolume(sound), 1.0F);
                  }

                  int newLevel = recipe.getLevel(level);
                  if (newLevel != level || !cauldronState.matches(newState)) {
                     if (newLevel == InspirationsRegistry.getCauldronMax() && InspirationsRegistry.isNormalCauldron(state) && InspirationsRegistry.hasFullCauldron(newState)) {
                        world.setBlockState(pos, InspirationsRegistry.getFullCauldron(newState));
                        cauldron = null;
                     } else {
                        if (!(block instanceof BlockCauldron)) {
                           Blocks.CAULDRON.setWaterLevel(world, pos, Blocks.CAULDRON.getDefaultState(), newLevel);
                           if (Config.enableExtendedCauldron) {
                              TileEntity te = world.getTileEntity(pos);
                              if (te instanceof TileCauldron) {
                                 cauldron = (TileCauldron)te;
                              }
                           }
                        } else {
                           ((BlockCauldron)block).setWaterLevel(world, pos, state, newLevel);
                        }

                        if (newLevel == 0) {
                           newState = ICauldronRecipe.CauldronState.WATER;
                        }
                     }
                  }

                  if (cauldron != null) {
                     cauldron.setState(newState, true);
                  }

                  ItemStack result = recipe.getResult(stack, boiling, level, cauldronState);
                  ItemStack container = recipe.getContainer(stack);
                  int oldSize = stack.getCount();
                  ItemStack remainder = recipe.transformInput(stack.copy(), boiling, level, cauldronState);
                  if (remainder.isEmpty()) {
                     if (container.isEmpty()) {
                        return result;
                     } else {
                        container.setCount(container.getCount() * oldSize);
                        dispenseItem(source, result);
                        return container;
                     }
                  } else {
                     dispenseItem(source, result);
                     if (!container.isEmpty()) {
                        container.setCount(container.getCount() * (oldSize - remainder.getCount()));
                        dispenseItem(source, container);
                     }

                     return remainder;
                  }
               }
            }
         }
      }
   }

   private static void dispenseItem(IBlockSource source, ItemStack stack) {
      if (((TileEntityDispenser)source.getBlockTileEntity()).addItemStack(stack) < 0) {
         DEFAULT.dispense(source, stack);
      }

   }
}
