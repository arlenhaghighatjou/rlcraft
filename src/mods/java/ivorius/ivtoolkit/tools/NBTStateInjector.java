package ivorius.ivtoolkit.tools;

import com.google.common.primitives.Ints;
import ivorius.ivtoolkit.IvToolkit;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class NBTStateInjector {
   public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

   public NBTStateInjector() {
   }

   public static void recursivelyInject(NBTBase nbt) {
      NBTWalker.walkCompounds(nbt, (cmp) -> {
         inject(cmp);
         return true;
      });
   }

   public static void inject(NBTTagCompound compound) {
      NBTTagList list = new NBTTagList();
      injectTEBlockFixTags(compound, "vanishingTileEntity", list, "BlockID");
      injectTEBlockFixTags(compound, "fenceGateTileEntity", list, "camoBlock");
      injectTEBlockFixTags(compound, "mixedBlockTileEntity", list, "block1", "block2");
      injectTEBlockFixTags(compound, "customDoorTileEntity", list, "frame", "topMaterial", "bottomMaterial");
      if (list.tagCount() > 0) {
         compound.setTag("SG_ID_FIX_TAG", list);
      }

   }

   private static boolean hasPrimitive(NBTTagCompound compound, String key) {
      return compound.hasKey(key) && compound.getTag(key) instanceof NBTPrimitive;
   }

   public static void injectTEBlockFixTags(NBTTagCompound compound, String tileEntityID, NBTTagList list, String... keys) {
      if (tileEntityID.equals(compound.getString("id"))) {
         for(String key : keys) {
            if (hasPrimitive(compound, key)) {
               addBlockTag(compound.getInteger(key), list, key);
            }
         }
      }

   }

   public static void addBlockTag(int blockID, NBTTagList tagList, String tagDest) {
      Block block = Block.getBlockById(blockID);
      if (block != null) {
         String stringID = ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString();
         NBTTagCompound idCompound = new NBTTagCompound();
         idCompound.setString("type", "block");
         idCompound.setString("tagDest", tagDest);
         idCompound.setString("blockID", stringID);
         tagList.appendTag(idCompound);
      } else {
         IvToolkit.logger.warn("Failed to apply block tag for structure with ID '" + blockID + "'");
      }

   }

   public static void recursivelyApply(NBTBase nbt, MCRegistry registry, boolean remove) {
      NBTWalker.walkCompounds(nbt, (cmp) -> {
         apply(cmp, registry);
         if (remove) {
            cmp.removeTag("SG_ID_FIX_TAG");
         }

         return true;
      });
   }

   public static void apply(NBTTagCompound compound, MCRegistry registry) {
      if (compound.hasKey("SG_ID_FIX_TAG")) {
         NBTTagList list = compound.getTagList("SG_ID_FIX_TAG", 10);

         for(int i = 0; i < list.tagCount(); ++i) {
            applyIDFixTag(compound, registry, list.getCompoundTagAt(i));
         }
      }

      if (compound.hasKey("id") && compound.hasKey("Count") && compound.hasKey("Damage") && Ints.tryParse(compound.getString("id")) == null) {
         registry.modifyItemStackCompound(compound, new ResourceLocation(compound.getString("id")));
      }

   }

   public static void applyIDFixTag(NBTTagCompound compound, MCRegistry registry, NBTTagCompound fixTag) {
      String type = fixTag.getString("type");
      byte var5 = -1;
      switch(type.hashCode()) {
      case 3242771:
         if (type.equals("item")) {
            var5 = 0;
         }
         break;
      case 93832333:
         if (type.equals("block")) {
            var5 = 1;
         }
      }

      switch(var5) {
      case 0:
         compound.setString(fixTag.getString("tagDest"), fixTag.getString("itemID"));
         break;
      case 1:
         String dest = fixTag.getString("tagDest");
         ResourceLocation blockID = new ResourceLocation(fixTag.getString("blockID"));
         Block block = registry.blockFromID(blockID);
         if (block != null) {
            compound.setInteger(dest, Block.getIdFromBlock(block));
         } else {
            IvToolkit.logger.warn("Failed to fix block tag from structure with ID '" + fixTag.getString("blockID") + "'");
         }
         break;
      default:
         IvToolkit.logger.warn("Unrecognized ID fix tag in structure with type '" + type + "'");
      }

   }
}
