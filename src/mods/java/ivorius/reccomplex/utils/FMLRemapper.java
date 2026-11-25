package ivorius.reccomplex.utils;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class FMLRemapper {
   protected final Map<ResourceLocation, ResourceLocation> blockRemaps = Maps.newHashMap();
   protected final Map<ResourceLocation, ResourceLocation> itemRemaps = Maps.newHashMap();
   protected final Map<ResourceLocation, ResourceLocation> tileEntityRemaps = Maps.newHashMap();

   public FMLRemapper() {
   }

   public void registerLegacyBlockIDs(ResourceLocation blockID, boolean inferItem, ResourceLocation... oldIDs) {
      for(ResourceLocation oldID : oldIDs) {
         this.blockRemaps.put(oldID, blockID);
         if (inferItem) {
            this.itemRemaps.put(oldID, blockID);
         }
      }

   }

   public void registerLegacyItemIDs(ResourceLocation itemID, ResourceLocation... oldIDs) {
      for(ResourceLocation oldID : oldIDs) {
         this.itemRemaps.put(oldID, itemID);
      }

   }

   public void registerLegacyTileEntityIDs(ResourceLocation tileEntityID, ResourceLocation... oldIDs) {
      for(ResourceLocation oldID : oldIDs) {
         this.tileEntityRemaps.put(oldID, tileEntityID);
      }

   }

   public ResourceLocation mapBlock(ResourceLocation id) {
      ResourceLocation remap = (ResourceLocation)this.blockRemaps.get(id);
      return remap != null ? remap : id;
   }

   @Nullable
   public ResourceLocation remapBlock(ResourceLocation id) {
      return (ResourceLocation)this.blockRemaps.get(id);
   }

   public ResourceLocation mapItem(ResourceLocation id) {
      ResourceLocation remap = (ResourceLocation)this.itemRemaps.get(id);
      return remap != null ? remap : id;
   }

   @Nullable
   public ResourceLocation remapItem(ResourceLocation id) {
      return (ResourceLocation)this.itemRemaps.get(id);
   }

   public ResourceLocation mapTileEntity(ResourceLocation id) {
      ResourceLocation remap = (ResourceLocation)this.tileEntityRemaps.get(id);
      return remap != null ? remap : id;
   }

   @Nullable
   public ResourceLocation remapTileEntity(ResourceLocation id) {
      return (ResourceLocation)this.tileEntityRemaps.get(id);
   }
}
