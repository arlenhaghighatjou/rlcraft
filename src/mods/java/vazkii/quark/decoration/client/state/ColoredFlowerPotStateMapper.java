package vazkii.quark.decoration.client.state;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;

@SideOnly(Side.CLIENT)
public class ColoredFlowerPotStateMapper extends StateMapperBase {
   public ColoredFlowerPotStateMapper() {
   }

   @Nonnull
   protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
      ResourceLocation loc = state.getBlock().getRegistryName();

      assert loc != null;

      if (((Boolean)state.getValue(BlockCustomFlowerPot.CUSTOM)).booleanValue()) {
         return new ModelResourceLocation(loc, "contents=custom");
      } else {
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
         map.remove(BlockCustomFlowerPot.CUSTOM);
         map.remove(BlockCustomFlowerPot.LEGACY_DATA);
         return new ModelResourceLocation(loc, this.getPropertyString(map));
      }
   }
}
