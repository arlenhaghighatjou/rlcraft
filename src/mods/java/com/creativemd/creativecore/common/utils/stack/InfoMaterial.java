package com.creativemd.creativecore.common.utils.stack;

import com.creativemd.creativecore.common.utils.mc.BlockUtils;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class InfoMaterial extends InfoStack {
   public Material material;

   public InfoMaterial(Material material, int stackSize) {
      super(stackSize);
      this.material = material;
   }

   public InfoMaterial(Material material) {
      this(material, 1);
   }

   public InfoMaterial() {
   }

   protected void writeToNBTExtra(NBTTagCompound nbt) {
      ResourceLocation blockName = null;

      for(Block block : Block.REGISTRY) {
         if (block != null && block.getDefaultState().getMaterial() == this.material) {
            blockName = block.getRegistryName();
            break;
         }
      }

      if (blockName != null) {
         nbt.setString("material", blockName.toString());
      }

   }

   protected void loadFromNBTExtra(NBTTagCompound nbt) {
      Block block = Block.getBlockFromName(nbt.getString("material"));
      if (block != null) {
         this.material = block.getDefaultState().getMaterial();
      }

   }

   public IBlockState getBlockState(ItemStack stack) {
      return BlockUtils.getState(stack);
   }

   public boolean isInstanceIgnoreSize(InfoStack info) {
      if (info instanceof InfoMaterial) {
         return ((InfoMaterial)info).material == this.material;
      } else if (info instanceof InfoBlock) {
         return ((InfoBlock)info).block.getDefaultState().getMaterial() == this.material;
      } else if (info instanceof InfoItemStack) {
         return this.getBlockState(((InfoItemStack)info).stack).getMaterial() == this.material;
      } else {
         return false;
      }
   }

   public InfoStack copy() {
      return new InfoMaterial(this.material, this.stackSize);
   }

   public ItemStack getItemStack(int stacksize) {
      for(Block block : Block.REGISTRY) {
         if (block != null && block.getDefaultState().getMaterial() == this.material) {
            NonNullList<ItemStack> stacks = NonNullList.create();

            try {
               block.getSubBlocks((CreativeTabs)displayOnCreativeTab.get(block), stacks);
               if (!stacks.isEmpty() && !((ItemStack)stacks.get(0)).isEmpty()) {
                  return ((ItemStack)stacks.get(0)).copy();
               }
            } catch (IllegalAccessException | IllegalArgumentException var6) {
               var6.printStackTrace();
            }
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean isStackInstanceIgnoreSize(ItemStack stack) {
      return this.getBlockState(stack).getMaterial() == this.material;
   }

   public boolean equalsIgnoreSize(Object object) {
      return object instanceof InfoMaterial && ((InfoMaterial)object).material == this.material;
   }

   public ArrayList<ItemStack> getAllPossibleItemStacks() {
      ArrayList<ItemStack> result = new ArrayList();
      NonNullList<ItemStack> stacks = NonNullList.create();

      for(Block block : Block.REGISTRY) {
         try {
            block.getSubBlocks((CreativeTabs)displayOnCreativeTab.get(block), stacks);
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }

      for(int i = 0; i < stacks.size(); ++i) {
         if (this.isInstanceIgnoreSize((ItemStack)stacks.get(i))) {
            result.add(stacks.get(i));
         }
      }

      return result;
   }
}
