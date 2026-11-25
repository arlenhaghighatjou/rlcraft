package svenhjol.charm.world.compat;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FutureMcBlocks {
   public static final Block barrel = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("futuremc", "barrel"));
   public static final Block composter = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("futuremc", "composter"));
   public static final Block lantern = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation("futuremc", "lantern"));
   public static final PropertyBool lanternHangingProperty;

   public FutureMcBlocks() {
   }

   static {
      PropertyBool property = null;
      if (lantern != null) {
         for(IProperty<?> propertyKey : lantern.getDefaultState().getPropertyKeys()) {
            if (propertyKey instanceof PropertyBool && propertyKey.getName().toLowerCase().equals("hanging")) {
               property = (PropertyBool)propertyKey;
               break;
            }
         }
      }

      lanternHangingProperty = property;
   }
}
