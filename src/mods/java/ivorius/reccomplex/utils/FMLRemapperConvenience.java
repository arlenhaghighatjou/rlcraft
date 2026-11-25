package ivorius.reccomplex.utils;

import ivorius.ivtoolkit.tools.MCRegistry;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class FMLRemapperConvenience {
   protected String domain;
   protected MCRegistry parent;
   protected FMLRemapper remapper;

   public FMLRemapperConvenience(String domain, MCRegistry parent, FMLRemapper remapper) {
      this.domain = domain;
      this.parent = parent;
      this.remapper = remapper;
   }

   public void registerLegacyIDs(Block block, boolean inferItem, String... oldIDs) {
      this.remapper.registerLegacyBlockIDs(this.parent.idFromBlock(block), inferItem, (ResourceLocation[])Stream.of(oldIDs).map((id) -> {
         return new ResourceLocation(this.domain, id);
      }).toArray((x$0) -> {
         return new ResourceLocation[x$0];
      }));
   }

   public void registerLegacyIDs(Item item, String... oldIDs) {
      this.remapper.registerLegacyItemIDs(this.parent.idFromItem(item), (ResourceLocation[])Stream.of(oldIDs).map((id) -> {
         return new ResourceLocation(this.domain, id);
      }).toArray((x$0) -> {
         return new ResourceLocation[x$0];
      }));
   }
}
