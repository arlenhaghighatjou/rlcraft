package ivorius.reccomplex.utils;

import com.google.common.collect.UnmodifiableIterator;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;

public class FMLMissingRemapper {
   protected MCRegistry parent;
   protected FMLRemapper remapper;

   public FMLMissingRemapper(MCRegistry parent, FMLRemapper remapper) {
      this.parent = parent;
      this.remapper = remapper;
   }

   public void onMissingMapping(MissingMappings<?> event) {
      UnmodifiableIterator var2 = event.getAllMappings().iterator();

      while(var2.hasNext()) {
         Mapping missingMapping = (Mapping)var2.next();
         if (missingMapping.getTarget() instanceof Block) {
            ResourceLocation remap = this.remapper.remapBlock(missingMapping.key);
            if (remap != null) {
               missingMapping.remap(this.parent.blockFromID(remap));
            }
            break;
         }

         if (missingMapping.getTarget() instanceof Item) {
            ResourceLocation remap = this.remapper.remapItem(missingMapping.key);
            if (remap != null) {
               missingMapping.remap(this.parent.itemFromID(remap));
            }
            break;
         }
      }

   }
}
