package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.compat.CompatRightClick;
import com.charles445.simpledifficulty.compat.HasShadows;
import com.charles445.simpledifficulty.compat.shadow.InspirationsShadow;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.ReflectUtil;
import com.charles445.simpledifficulty.util.SoundUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.block.properties.PropertyBool;
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
   Class c_BlockEnhancedCauldron;
   Field f_BlockEnhancedCauldron_BOILING;
   PropertyBool o_BlockEnhancedCauldron_BOILING;
   Method m_BlockEnhancedCauldron_getCauldronLevel;
   Class c_TileCauldron;
   Method m_TileCauldron_getState;
   Method m_TileCauldron_setFluidLevel;
   Class c_CauldronState;
   Method m_CauldronState_getFluid;

   public InspirationsHandler() {
      try {
         this.c_BlockEnhancedCauldron = Class.forName("knightminer.inspirations.recipes.block.BlockEnhancedCauldron");
         this.f_BlockEnhancedCauldron_BOILING = ReflectUtil.findField(this.c_BlockEnhancedCauldron, "BOILING");
         this.o_BlockEnhancedCauldron_BOILING = (PropertyBool)this.f_BlockEnhancedCauldron_BOILING.get((Object)null);
         this.m_BlockEnhancedCauldron_getCauldronLevel = ReflectUtil.findMethod(this.c_BlockEnhancedCauldron, "getCauldronLevel");
         this.c_TileCauldron = Class.forName("knightminer.inspirations.recipes.tileentity.TileCauldron");
         this.m_TileCauldron_getState = ReflectUtil.findMethod(this.c_TileCauldron, "getState");
         this.m_TileCauldron_setFluidLevel = ReflectUtil.findMethod(this.c_TileCauldron, "setFluidLevel");
         this.c_CauldronState = Class.forName("knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe$CauldronState");
         this.m_CauldronState_getFluid = ReflectUtil.findMethod(this.c_CauldronState, "getFluid");
         CompatRightClick.cauldronHandler = new InspirationsHandler.InspirationsRightClickCauldron();

         try {
            InspirationsHandler.CanteenRecipe.createCanteenRecipes();
         } catch (Exception var2) {
            SimpleDifficulty.logger.error("Inspirations canteen recipes failed!...", var2);
         }
      } catch (Exception var3) {
         SimpleDifficulty.logger.error("InspirationsHandler setup failed! Inspirations compatibility is now disabled!", var3);
         this.errorFallback();
      }

   }

   public void errorFallback() {
      CompatRightClick.cauldronHandler = this.fallbackCauldron;
   }

   public int getCauldronLevel(IBlockState state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Integer)this.m_BlockEnhancedCauldron_getCauldronLevel.invoke((Object)null, state)).intValue();
   }

   public void setFluidLevel(TileEntity tileCauldron, int fluidLevel) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_TileCauldron_setFluidLevel.invoke(tileCauldron, Math.max(0, fluidLevel));
   }

   @Nullable
   public Object getCauldronState(TileEntity tileCauldron) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_TileCauldron_getState.invoke(tileCauldron);
   }

   @Nullable
   public Fluid getCauldronStateFluid(Object cauldronState) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (Fluid)this.m_CauldronState_getFluid.invoke(cauldronState);
   }

   protected void damageFromBoilingDrink(EntityPlayer player) {
      if (!player.isImmuneToFire()) {
         player.attackEntityFrom(SDDamageSources.INSPIRATIONS_CAULDRON_BURN, 1.0F);
      }

   }

   @HasShadows
   public static class CanteenRecipe implements InspirationsShadow.ICauldronRecipe {
      public CanteenRecipe() {
      }

      public static void createCanteenRecipes() {
         InspirationsHandler.CanteenRecipe recipe = new InspirationsHandler.CanteenRecipe();

         try {
            Class c_ICauldronRecipe = Class.forName("knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe");
            Class c_InspirationsRegistry = Class.forName("knightminer.inspirations.library.InspirationsRegistry");
            Method m_InspirationsRegistry_addCauldronRecipe = ReflectUtil.findMethod(c_InspirationsRegistry, "addCauldronRecipe", c_ICauldronRecipe);
            m_InspirationsRegistry_addCauldronRecipe.invoke((Object)null, recipe);
         } catch (Exception var4) {
            SimpleDifficulty.logger.error("Inspirations compatibility failed while reflecting for cauldron recipes!", var4);
         }

      }

      public boolean matches(ItemStack stack, boolean boiling, int level, InspirationsShadow.ICauldronRecipe.CauldronState state) {
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

      public ItemStack getResult(ItemStack stack, boolean boiling, int level, InspirationsShadow.ICauldronRecipe.CauldronState state) {
         return ItemStack.EMPTY;
      }

      public ItemStack getContainer(ItemStack stack) {
         return ItemStack.EMPTY;
      }

      public SoundEvent getSound(ItemStack stack, boolean boiling, int level, InspirationsShadow.ICauldronRecipe.CauldronState state) {
         return SoundEvents.ITEM_BUCKET_FILL;
      }

      public ItemStack transformInput(ItemStack stack, boolean boiling, int level, InspirationsShadow.ICauldronRecipe.CauldronState state) {
         return stack;
      }
   }

   public class InspirationsRightClickCauldron implements CompatRightClick.IRightClick {
      public InspirationsRightClickCauldron() {
      }

      public void process(RightClickBlock event, World world, BlockPos pos, IBlockState state, EntityPlayer player) {
         if (ModConfig.server.compatibility.toggles.inspirations && InspirationsHandler.this.c_BlockEnhancedCauldron.isInstance(state.getBlock())) {
            ItemStack heldItem = player.getHeldItemMainhand();
            if (heldItem.isEmpty() && player.isSneaking() && SDCapabilities.getThirstData(player).isThirsty()) {
               boolean boiling = ((Boolean)state.getValue(InspirationsHandler.this.o_BlockEnhancedCauldron_BOILING)).booleanValue();

               try {
                  int level = InspirationsHandler.this.getCauldronLevel(state);
                  if (level > 0) {
                     TileEntity te = world.getTileEntity(pos);
                     if (InspirationsHandler.this.c_TileCauldron.isInstance(te)) {
                        Object cauldronState = InspirationsHandler.this.getCauldronState(te);
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
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var12) {
                  SimpleDifficulty.logger.error("Inspirations compatibility failed while handling a cauldron!", var12);
                  InspirationsHandler.this.errorFallback();
                  return;
               }
            }

         } else {
            InspirationsHandler.this.fallbackCauldron.process(event, world, pos, state, player);
         }
      }
   }
}
