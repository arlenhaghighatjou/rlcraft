package ivorius.reccomplex.world.gen.feature.structure.schematics;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.set.TShortSet;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SchematicMapping {
   public static final String COMPOUND_KEY = "SchematicaMapping";
   private TShortObjectMap<Block> blockMapping = new TShortObjectHashMap();

   public SchematicMapping() {
   }

   public SchematicMapping(NBTTagCompound compound) {
      for(String name : compound.getKeySet()) {
         Block block = Block.getBlockFromName(name);
         if (block != null) {
            this.blockMapping.put(compound.getShort(name), block);
         }
      }

   }

   public void putBlock(int id, Block block) {
      this.blockMapping.put((short)id, block);
   }

   public void removeBlock(int id) {
      this.blockMapping.remove((short)id);
   }

   public TShortSet allIDs() {
      return this.blockMapping.keySet();
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      this.blockMapping.forEachEntry((a, b) -> {
         compound.setShort(((ResourceLocation)Block.REGISTRY.getNameForObject(b)).toString(), a);
         return true;
      });
      return compound;
   }

   public Block blockFromID(int id) {
      Block block = (Block)this.blockMapping.get((short)id);
      return block != null ? block : Block.getBlockById(id);
   }
}
