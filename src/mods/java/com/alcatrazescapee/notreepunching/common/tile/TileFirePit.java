package com.alcatrazescapee.notreepunching.common.tile;

import com.alcatrazescapee.alcatrazcore.tile.ITileFields;
import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import com.alcatrazescapee.notreepunching.common.blocks.BlockFirePit;
import com.alcatrazescapee.notreepunching.common.recipe.FirePitRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class TileFirePit extends TileInventory implements ITickable, ITileFields {
   private static final byte BURN_FIELD_ID = 0;
   private static final byte MAX_BURN_FIELD_ID = 1;
   private static final byte COOK_FIELD_ID = 2;
   public static final byte FUEL_SLOT_ID = 0;
   public static final byte INPUT_SLOT_ID = 1;
   public static final byte OUTPUT_SLOT_ID = 2;
   private static final byte NUM_FIELDS = 3;
   private int burnTicks;
   private int maxBurnTicks;
   private int cookTimer;
   private boolean hasCachedRecipe = false;
   private FirePitRecipe cachedRecipe = null;

   private static boolean isStackValidFuel(ItemStack stack) {
      return TileEntityFurnace.getItemBurnTime(stack) > 0 && TileEntityFurnace.getItemBurnTime(stack) <= ModConfig.BALANCE.firePitFuelMaxAmount;
   }

   public TileFirePit() {
      super(3);
   }

   public void light(boolean addStartingFuel) {
      this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockFirePit.LIT, Boolean.valueOf(true)));
      if (addStartingFuel) {
         this.burnTicks = (int)(0.5D * (double)TileEntityFurnace.getItemBurnTime(new ItemStack(Blocks.LOG)) * (double)ModConfig.BALANCE.firePitFuelMultiplier);
         this.maxBurnTicks = this.burnTicks * 2;
      }

   }

   public void extinguish() {
      this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(BlockFirePit.LIT, Boolean.valueOf(false)));
      this.burnTicks = 0;
      this.maxBurnTicks = 0;
      this.cookTimer = 0;
   }

   public void update() {
      if (!this.world.isRemote) {
         IBlockState state = this.world.getBlockState(this.pos);
         if (((Boolean)this.world.getBlockState(this.pos).getValue(BlockFirePit.LIT)).booleanValue()) {
            if (!this.hasCachedRecipe) {
               this.updateRecipe();
            }

            if (this.cachedRecipe != null) {
               ++this.cookTimer;
               if (this.cookTimer >= ModConfig.BALANCE.firePitCookTime) {
                  this.updateRecipe();
                  if (this.cachedRecipe != null) {
                     ItemStack cookStack = this.inventory.getStackInSlot(1);
                     ItemStack outStack = this.inventory.getStackInSlot(2);
                     this.inventory.setStackInSlot(1, this.cachedRecipe.consumeInput(cookStack));
                     this.inventory.setStackInSlot(2, CoreHelpers.mergeStacks(outStack, this.cachedRecipe.getOutput()));
                     this.updateRecipe();
                  }

                  this.cookTimer = 0;
               }
            } else if (this.cookTimer > 0) {
               this.cookTimer -= 4;
               if (this.cookTimer < 0) {
                  this.cookTimer = 0;
               }
            }

            --this.burnTicks;
            if (this.burnTicks <= 0) {
               ItemStack stack = this.inventory.getStackInSlot(0);
               if (isStackValidFuel(stack)) {
                  this.burnTicks += TileEntityFurnace.getItemBurnTime(stack) * ModConfig.BALANCE.firePitFuelMultiplier;
                  this.maxBurnTicks = this.burnTicks;
                  stack = CoreHelpers.consumeItem(stack);
                  this.inventory.setStackInSlot(0, stack);
               } else {
                  this.burnTicks = 0;
                  this.world.setBlockState(this.pos, state.withProperty(BlockFirePit.LIT, Boolean.valueOf(false)));
               }
            }
         } else {
            this.burnTicks = 0;
            this.maxBurnTicks = 0;
         }
      }

   }

   public void setAndUpdateSlots(int slot) {
      if (!this.world.isRemote) {
         this.updateRecipe();
      }

      super.setAndUpdateSlots(slot);
   }

   public boolean isItemValid(int slot, ItemStack stack) {
      switch(slot) {
      case 0:
         return isStackValidFuel(stack);
      case 1:
         return true;
      case 2:
         return false;
      default:
         throw new IllegalArgumentException("Invalid slot id in isItemValid: " + slot);
      }
   }

   @SideOnly(Side.CLIENT)
   public int getScaledCookTime() {
      if (ModConfig.BALANCE.firePitCookTime != 0 && this.cookTimer != 0) {
         float f1 = (float)this.cookTimer / (float)ModConfig.BALANCE.firePitCookTime;
         return Math.round(23.0F * f1);
      } else {
         return 0;
      }
   }

   @SideOnly(Side.CLIENT)
   public int getScaledBurnTicks() {
      if (this.maxBurnTicks != 0 && this.burnTicks != 0) {
         float f1 = (float)this.burnTicks / (float)this.maxBurnTicks;
         return Math.round(13.0F * f1);
      } else {
         return 0;
      }
   }

   public int getFieldCount() {
      return 3;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.burnTicks = nbt.getInteger("burn_ticks");
      this.maxBurnTicks = nbt.getInteger("max_burn_ticks");
      this.cookTimer = nbt.getInteger("cook_ticks");
      super.readFromNBT(nbt);
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("burn_ticks", this.burnTicks);
      nbt.setInteger("max_burn_ticks", this.maxBurnTicks);
      nbt.setInteger("cook_ticks", this.cookTimer);
      return super.writeToNBT(nbt);
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   private void updateRecipe() {
      this.hasCachedRecipe = true;
      ItemStack cookStack = this.inventory.getStackInSlot(1);
      FirePitRecipe recipe = (FirePitRecipe)ModRecipes.FIRE_PIT.get(cookStack);
      if (recipe != null) {
         ItemStack outStack = this.inventory.getStackInSlot(2);
         ItemStack output = recipe.getOutput();
         if (CoreHelpers.canMergeStacks(outStack, output)) {
            this.cachedRecipe = recipe;
            return;
         }
      }

      this.cachedRecipe = null;
   }

   public int getField(int id) {
      switch(id) {
      case 0:
         return this.burnTicks;
      case 1:
         return this.maxBurnTicks;
      case 2:
         return this.cookTimer;
      default:
         NoTreePunching.getLog().warn("Invalid field ID: {}", Integer.valueOf(id));
         return 0;
      }
   }

   public void setField(int id, int value) {
      switch(id) {
      case 0:
         this.burnTicks = value;
         break;
      case 1:
         this.maxBurnTicks = value;
         break;
      case 2:
         this.cookTimer = value;
         break;
      default:
         NoTreePunching.getLog().warn("Invalid Field ID {}", Integer.valueOf(id));
      }

   }
}
