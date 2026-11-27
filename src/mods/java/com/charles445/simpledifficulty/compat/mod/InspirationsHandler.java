package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.CompatRightClick;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.SoundUtil;
import javax.annotation.Nullable;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class InspirationsHandler {
   CompatRightClick.IRightClick fallbackCauldron = CompatRightClick.cauldronHandler;

   public InspirationsHandler() {
      CompatRightClick.cauldronHandler = new InspirationsHandler.InspirationsRightClickCauldron();

      try {
         InspirationsHandler.CanteenRecipe.createCanteenRecipes();
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("Inspirations canteen recipes failed!...", var2);
      }

   }

   public int getCauldronLevel(IBlockState state) {
      return BlockEnhancedCauldron.getCauldronLevel(state);
   }

   public void setFluidLevel(TileEntity tileCauldron, int fluidLevel) {
      ((TileCauldron)tileCauldron).setFluidLevel(Math.max(0, fluidLevel));
   }

   @Nullable
   public ICauldronRecipe.CauldronState getCauldronState(TileEntity tileCauldron) {
      return ((TileCauldron)tileCauldron).getState();
   }

   @Nullable
   public Fluid getCauldronStateFluid(ICauldronRecipe.CauldronState cauldronState) {
      return cauldronState.getFluid();
   }

   protected void damageFromBoilingDrink(EntityPlayer player) {
      if (!player.isImmuneToFire()) {
         player.attackEntityFrom(SDDamageSources.INSPIRATIONS_CAULDRON_BURN, 1.0F);
      }

   }

   public static class CanteenRecipe implements ICauldronRecipe {
      public CanteenRecipe() {
      }

      public static void createCanteenRecipes() {
         InspirationsRegistry.addCauldronRecipe(new InspirationsHandler.CanteenRecipe());
      }

      public boolean matches(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
         if (!ModConfig.server.compatibility.toggles.inspirations) {
            return false;
         } else if (level == 0) {
            return false;
         } else {
            if (stack.getItem() instanceof IItemCanteen) {
               IItemCanteen canteen = (IItemCanteen)stack.getItem();
               Fluid fluid = state.getFluid();
               if (fluid == null) {
                  return false;
               }

               if (fluid == FluidRegistry.WATER) {
                  if (boiling) {
                     return canteen.tryAddDose(stack, ThirstEnum.PURIFIED);
                  }

                  return canteen.tryAddDose(stack, ThirstEnum.NORMAL);
               }
            }

            return false;
         }
      }

      public int getLevel(int level) {
         return level - 1;
      }

      public ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
         return ItemStack.EMPTY;
      }

      public ItemStack getContainer(ItemStack stack) {
         return ItemStack.EMPTY;
      }

      public SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
         return SoundEvents.ITEM_BUCKET_FILL;
      }

      public ItemStack transformInput(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
         return stack;
      }
   }

   public class InspirationsRightClickCauldron implements CompatRightClick.IRightClick {
      public InspirationsRightClickCauldron() {
      }

      public void process(RightClickBlock event, World world, BlockPos pos, IBlockState state, EntityPlayer player) {
         if (ModConfig.server.compatibility.toggles.inspirations && state.getBlock() instanceof BlockEnhancedCauldron) {
            ItemStack heldItem = player.getHeldItemMainhand();
            if (heldItem.isEmpty() && player.isSneaking() && SDCapabilities.getThirstData(player).isThirsty()) {
               boolean boiling = ((Boolean)state.getValue(BlockEnhancedCauldron.BOILING)).booleanValue();
               int level = InspirationsHandler.this.getCauldronLevel(state);
               if (level > 0) {
                  TileEntity te = world.getTileEntity(pos);
                  if (te instanceof TileCauldron) {
                     ICauldronRecipe.CauldronState cauldronState = InspirationsHandler.this.getCauldronState(te);
                     if (cauldronState != null) {
                        Fluid fluid = InspirationsHandler.this.getCauldronStateFluid(cauldronState);
                        if (fluid != null) {
                           if (fluid == FluidRegistry.WATER) {
                              if (boiling) {
                                 ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
                                 InspirationsHandler.this.damageFromBoilingDrink(player);
                                 InspirationsHandler.this.setFluidLevel(te, level - 1);
                              } else {
                                 ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                              }

                              SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
                           } else if (fluid == SDFluids.purifiedWater) {
                              ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
                              if (boiling) {
                                 InspirationsHandler.this.damageFromBoilingDrink(player);
                              }

                              SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
                              InspirationsHandler.this.setFluidLevel(te, 0);
                           }
                        }
                     }
                  } else {
                     if (boiling) {
                        ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
                        InspirationsHandler.this.damageFromBoilingDrink(player);
                     } else {
                        ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
                     }

                     SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ENTITY_GENERIC_DRINK);
                  }
               }
            }

         } else {
            InspirationsHandler.this.fallbackCauldron.process(event, world, pos, state, player);
         }
      }
   }
}
