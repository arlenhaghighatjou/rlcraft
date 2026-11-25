package knightminer.inspirations.library.client;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import javax.annotation.Nonnull;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public class PropertyStateMapper extends StateMapperBase {
   private final PropertyEnum<?> prop;
   private final IProperty<?>[] ignore;
   private ResourceLocation name;

   public PropertyStateMapper(ResourceLocation name, PropertyEnum<?> prop, IProperty<?>... ignore) {
      this.name = name;
      this.prop = prop;
      this.ignore = ignore;
   }

   public PropertyStateMapper(PropertyEnum<?> prop, IProperty<?>... ignore) {
      this((ResourceLocation)null, prop, ignore);
   }

   @Nonnull
   protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
      ResourceLocation baseLoc = this.name;
      if (baseLoc == null) {
         baseLoc = state.getBlock().getRegistryName();
      }

      LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
      map.remove(this.prop);

      for(IProperty<?> ignored : this.ignore) {
         map.remove(ignored);
      }

      ResourceLocation res = new ResourceLocation(baseLoc.getResourceDomain(), baseLoc.getResourcePath() + "/" + ((IStringSerializable)((Enum)state.getValue(this.prop))).getName());
      return new ModelResourceLocation(res, this.getPropertyString(map));
   }
}
