package vazkii.quark.decoration.client.state;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

public class FlowerPotStateMapper extends StateMapperBase {
   public static final FlowerPotStateMapper INSTANCE = new FlowerPotStateMapper();
   public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation("quark", "custom_flower_pot"), "normal");

   public FlowerPotStateMapper() {
   }

   @Nonnull
   protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
      if (((Boolean)state.getValue(BlockCustomFlowerPot.CUSTOM)).booleanValue()) {
         return LOCATION;
      } else {
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
         map.remove(BlockCustomFlowerPot.CUSTOM);
         map.remove(BlockCustomFlowerPot.LEGACY_DATA);
         return new ModelResourceLocation((ResourceLocation)Objects.requireNonNull(state.getBlock().getRegistryName()), this.getPropertyString(map));
      }
   }
}
