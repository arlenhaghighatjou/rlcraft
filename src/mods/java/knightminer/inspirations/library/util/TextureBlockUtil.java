package knightminer.inspirations.library.util;

import java.util.Iterator;
import knightminer.inspirations.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.property.PropertyString;

public final class TextureBlockUtil {
   public static final String TAG_TEXTURE = "texture";
   public static final PropertyString TEXTURE_PROP = new PropertyString("TEXTURE");

   private TextureBlockUtil() {
   }

   public static void placeTextureBlock(World world, BlockPos pos, ItemStack stack) {
      NBTTagCompound tag = TagUtil.getTagSafe(stack);
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
         NBTTagCompound textureTag = tag.getCompoundTag("texture");
         if (textureTag == null) {
            textureTag = new NBTTagCompound();
         }

         updateTextureBlock(te, textureTag);
      }

   }

   public static ItemStack getBlockItemStack(IBlockAccess world, BlockPos pos, IBlockState state) {
      Block block = state.getBlock();
      ItemStack stack = new ItemStack(block, 1, block.damageDropped(state));
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
         NBTTagCompound texture = getTextureBlock(te);
         if (texture.getSize() > 0) {
            NBTTagCompound tags = new NBTTagCompound();
            tags.setTag("texture", texture);
            stack.setTagCompound(tags);
         }
      }

      return stack;
   }

   public static void updateTextureBlock(TileEntity te, NBTTagCompound tag) {
      if (te != null) {
         te.getTileData().setTag("texture", tag);
      }

   }

   public static NBTTagCompound getTextureBlock(TileEntity te) {
      return te == null ? new NBTTagCompound() : te.getTileData().getCompoundTag("texture");
   }

   public static ItemStack createTexturedStack(Block texturable, int texMeta, Block block, int blockMeta) {
      ItemStack stack = new ItemStack(texturable, 1, texMeta);
      if (block != null) {
         ItemStack blockStack = new ItemStack(block, 1, blockMeta);
         NBTTagCompound tag = new NBTTagCompound();
         NBTTagCompound subTag = new NBTTagCompound();
         blockStack.writeToNBT(subTag);
         tag.setTag("texture", subTag);
         stack.setTagCompound(tag);
      }

      return stack;
   }

   public static ItemStack getStackTexture(ItemStack table) {
      NBTTagCompound tag = TagUtil.getTagSafe(table).getCompoundTag("texture");
      return new ItemStack(tag);
   }

   public static void addBlocksFromOredict(String oredict, Block block, int meta, NonNullList<ItemStack> list) {
      label31:
      for(ItemStack stack : OreDictionary.getOres(oredict, false)) {
         Block textureBlock = Block.getBlockFromItem(stack.getItem());
         int textureMeta = stack.getMetadata();
         if (textureMeta == 32767) {
            NonNullList<ItemStack> subBlocks = NonNullList.create();
            textureBlock.getSubBlocks(CreativeTabs.SEARCH, subBlocks);
            Iterator var9 = subBlocks.iterator();

            while(true) {
               if (!var9.hasNext()) {
                  continue label31;
               }

               ItemStack subBlock = (ItemStack)var9.next();
               list.add(createTexturedStack(block, meta, Block.getBlockFromItem(subBlock.getItem()), subBlock.getMetadata()));
               if (!Config.showAllVariants) {
                  break;
               }
            }

            return;
         } else {
            list.add(createTexturedStack(block, meta, textureBlock, textureMeta));
            if (!Config.showAllVariants) {
               return;
            }
         }
      }

   }
}
