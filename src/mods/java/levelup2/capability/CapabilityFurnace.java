package levelup2.capability;

import levelup2.api.IPlayerSkill;
import levelup2.config.LevelUpConfig;
import levelup2.skills.SkillRegistry;
import levelup2.util.SmeltingBlacklist;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class CapabilityFurnace extends PlayerCapability.CapabilityProcessorDefault {
   public CapabilityFurnace(TileEntityFurnace tile) {
      super(tile);
   }

   public void extraProcessing(EntityPlayer player) {
      if (this.tile != null) {
         TileEntityFurnace furnace = (TileEntityFurnace)this.tile;
         if (furnace.isBurning() && furnace.canSmelt()) {
            ItemStack stack = furnace.getStackInSlot(0);
            if (!stack.isEmpty()) {
               int bonus = SkillRegistry.getSkillLevel(player, "levelup:furnacespeed");
               if (bonus > 0 || !this.isSkillActive("levelup:furnacespeed")) {
                  int time = player.getRNG().nextInt(bonus + 1);
                  if (this.isSkillActive("levelup:furnacespeed") && time > 0 && furnace.getField(2) + time < furnace.getField(3)) {
                     furnace.setField(2, furnace.getField(2) + time);
                  }

                  if (furnace.getField(2) > furnace.getField(3) - 2 && this.isSkillActive("levelup:furnacebonus")) {
                     bonus = SkillRegistry.getSkillLevel(player, "levelup:furnacebonus");
                     if (bonus > 0 && this.isDoublingValid(furnace) && player.getRNG().nextFloat() < (float)bonus / 40.0F) {
                        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                        if (!LevelUpConfig.furnaceEjection) {
                           if (furnace.getStackInSlot(2).isEmpty()) {
                              furnace.setInventorySlotContents(2, result);
                           } else {
                              ItemStack product = furnace.getStackInSlot(2);
                              if (ItemStack.areItemsEqual(result, product) && product.getCount() + result.getCount() * 2 <= product.getMaxStackSize()) {
                                 furnace.getStackInSlot(2).grow(result.getCount());
                              }
                           }
                        } else {
                           this.ejectExtraItem(result);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private boolean isDoublingValid(TileEntityFurnace tile) {
      ItemStack smeltingItem = tile.getStackInSlot(0);
      return !FurnaceRecipes.instance().getSmeltingResult(smeltingItem).isEmpty() && !SmeltingBlacklist.contains(smeltingItem);
   }

   private void ejectExtraItem(ItemStack stack) {
      if (!stack.isEmpty() && (this.tile.getBlockType() == Blocks.FURNACE || this.tile.getBlockType() == Blocks.LIT_FURNACE)) {
         IBlockState furnace = this.tile.getWorld().getBlockState(this.tile.getPos());
         EnumFacing facing = (EnumFacing)furnace.getValue(BlockFurnace.FACING);
         BlockPos offset = this.tile.getPos().offset(facing);
         EntityItem item = new EntityItem(this.tile.getWorld(), (double)offset.getX() + 0.5D, (double)offset.getY() + 0.5D, (double)offset.getZ() + 0.5D, stack);
         this.tile.getWorld().spawnEntity(item);
      }

   }

   private boolean isSkillActive(String skill) {
      IPlayerSkill sk = SkillRegistry.getSkillFromName(skill);
      if (sk == null) {
         return false;
      } else {
         return sk.isActive() && sk.isEnabled();
      }
   }
}
