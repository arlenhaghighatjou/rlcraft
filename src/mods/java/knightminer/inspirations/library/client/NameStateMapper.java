package knightminer.inspirations.library.client;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class NameStateMapper extends StateMapperBase {
   private final IProperty<?>[] ignore;
   private ResourceLocation name;

   public NameStateMapper(ResourceLocation name, IProperty<?>... ignore) {
      this.name = name;
      this.ignore = ignore;
   }

   @Nonnull
   protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
      LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());

      for(IProperty<?> ignored : this.ignore) {
         map.remove(ignored);
      }

      return new ModelResourceLocation(this.name, this.getPropertyString(map));
   }
}
