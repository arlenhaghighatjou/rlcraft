package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.MCRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class IvBlockMapper {
   private List<Block> mapping;

   public IvBlockMapper() {
      this.mapping = new ArrayList();
   }

   public IvBlockMapper(NBTTagCompound compound, String key, MCRegistry registry) {
      this(compound.getTagList(key, 8), registry);
   }

   public IvBlockMapper(NBTTagList list, MCRegistry registry) {
      this.mapping = new ArrayList(list.tagCount());

      for(int i = 0; i < list.tagCount(); ++i) {
         this.mapping.add(registry.blockFromID(new ResourceLocation(list.getStringTagAt(i))));
      }

   }

   public void addMapping(Block block) {
      if (!this.mapping.contains(block)) {
         this.mapping.add(block);
      }

   }

   public void addMapping(List<Block> blocks) {
      for(Block block : blocks) {
         this.addMapping(block);
      }

   }

   public int getMapping(Block block) {
      return this.mapping.indexOf(block);
   }

   public Block getBlock(int mapping) {
      return (Block)this.mapping.get(mapping);
   }

   public int getMapSize() {
      return this.mapping.size();
   }

   public NBTTagList createTagList() {
      NBTTagList list = new NBTTagList();

      for(Block block : this.mapping) {
         String name = ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString();
         if (name != null) {
            list.appendTag(new NBTTagString(name));
         } else {
            list.appendTag(new NBTTagString("minecraft:stone"));
            System.out.println("Did not find name for block '" + block + "' to map");
         }
      }

      return list;
   }

   public NBTTagCompound createNBTForBlocks(List<Block> blocks) {
      NBTTagCompound compound = new NBTTagCompound();
      int[] vals = new int[blocks.size()];

      for(int i = 0; i < blocks.size(); ++i) {
         vals[i] = this.getMapping((Block)blocks.get(i));
      }

      NBTTagCompound compressed = new NBTTagCompound();
      IvNBTHelper.writeCompressed("data", vals, this.getMapSize() - 1, compressed);
      compound.setTag("blocksCompressed", compressed);
      return compound;
   }

   public Block[] createBlocksFromNBT(NBTTagCompound compound) {
      Block[] blocks;
      if (compound.hasKey("blocksCompressed")) {
         NBTTagCompound compressed = compound.getCompoundTag("blocksCompressed");
         int[] vals = IvNBTHelper.readCompressed("data", compressed);
         blocks = new Block[vals.length];

         for(int i = 0; i < vals.length; ++i) {
            blocks[i] = this.getBlock(vals[i]);
         }
      } else if (compound.hasKey("blockBytes")) {
         byte[] byteArray = compound.getByteArray("blockBytes");
         blocks = new Block[byteArray.length];

         for(int i = 0; i < byteArray.length; ++i) {
            blocks[i] = this.getBlock(byteArray[i]);
         }
      } else {
         if (!compound.hasKey("blockInts")) {
            throw new RuntimeException("Unrecognized block collection type " + compound);
         }

         int[] intArray = compound.getIntArray("blockInts");
         blocks = new Block[intArray.length];

         for(int i = 0; i < intArray.length; ++i) {
            blocks[i] = this.getBlock(intArray[i]);
         }
      }

      return blocks;
   }
}
