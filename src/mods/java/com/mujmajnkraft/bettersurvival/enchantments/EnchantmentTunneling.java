package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchantmentTunneling extends Enchantment {
   public EnchantmentTunneling() {
      super(Rarity.RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setRegistryName("tunneling");
      this.setName("mujmajnkraftsbettersurvival.tunneling");
   }

   public static void mineManyBlocks(EntityPlayer miner, IBlockState state, BlockPos pos) {
      World world = miner.getEntityWorld();
      ItemStack stack = miner.getHeldItemMainhand();
      int l = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.tunneling, miner.getHeldItemMainhand());
      if (l > 0 && canMineEffectively(miner, state, pos)) {
         EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, miner).getOpposite();
         String x = facing.getName();
         byte y = -1;
         switch(x.hashCode()) {
         case 3739:
            if (x.equals("up")) {
               y = 3;
            }
            break;
         case 3089570:
            if (x.equals("down")) {
               y = 2;
            }
            break;
         case 3105789:
            if (x.equals("east")) {
               y = 1;
            }
            break;
         case 3645871:
            if (x.equals("west")) {
               y = 0;
            }
            break;
         case 105007365:
            if (x.equals("north")) {
               y = 5;
            }
            break;
         case 109627853:
            if (x.equals("south")) {
               y = 4;
            }
         }

         int dir;
         switch(y) {
         case 0:
            dir = 1;
            break;
         case 1:
            dir = 2;
            break;
         case 2:
            dir = 3;
            break;
         case 3:
            dir = 4;
            break;
         case 4:
            dir = 5;
            break;
         case 5:
            dir = 6;
            break;
         default:
            dir = 0;
         }

         if (dir != 0) {
            if (!stack.hasTagCompound()) {
               stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setBoolean("tunnelCooldown", true);

            for(int x = -l; x < l + 1; ++x) {
               if (dir != 1 && dir != 2 || x == 0) {
                  for(int y = -l; y < l + 1; ++y) {
                     if (dir != 3 && dir != 4 || y == 0) {
                        for(int z = -l; z < l + 1; ++z) {
                           if ((dir != 5 && dir != 6 || z == 0) && Math.sqrt((double)(x * x + y * y + z * z)) <= (double)(((float)l + 1.0F) / 2.0F) && (x != 0 || y != 0 || z != 0)) {
                              BlockPos pos1 = pos.add(x, y, z);
                              if (canMineEffectively(miner, world.getBlockState(pos1), pos1)) {
                                 ((EntityPlayerMP)miner).interactionManager.tryHarvestBlock(pos1);
                              }
                           }
                        }
                     }
                  }
               }
            }

            stack.getTagCompound().setBoolean("tunnelCooldown", false);
         }
      }

   }

   static boolean canMineEffectively(EntityPlayer player, IBlockState state, BlockPos pos) {
      ItemStack stack = player.getHeldItemMainhand();
      Block block = state.getBlock();
      if (block == Blocks.AIR) {
         return false;
      } else if (ForgeConfigHandler.enchantments.preventTunnelingTileEntities && player.world.getTileEntity(pos) != null) {
         return false;
      } else {
         for(String type : stack.getItem().getToolClasses(player.getHeldItemMainhand())) {
            if (block.isToolEffective(type, state) && block.getHarvestLevel(state) <= stack.getItem().getHarvestLevel(player.getHeldItemMainhand(), type, player, state)) {
               return true;
            }
         }

         return false;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 25 + (enchantmentLevel - 1) * 15;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.tunnelingLevel;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.tunnelingTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.tunnelingLevel != 0;
   }
}
