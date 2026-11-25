package pl.asie.foamfix.common;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraftforge.common.property.IUnlistedProperty;

public class FoamyBlockStateContainer extends BlockStateContainer {
   private PropertyValueMapper foamfix_mapper;

   public FoamyBlockStateContainer(Block blockIn, IProperty<?>... properties) {
      super(blockIn, properties);
   }

   protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
      if (block != null && !block.getClass().getName().startsWith("jds.bibliocraft")) {
         if (this.foamfix_mapper == null) {
            this.foamfix_mapper = new PropertyValueMapper(this);
         }

         return (StateImplementation)(this.foamfix_mapper.isValid() ? new FoamyBlockState(this.foamfix_mapper, block, properties) : this.createState_foamfix_old(block, properties, unlistedProperties));
      } else {
         return this.createState_foamfix_old(block, properties, unlistedProperties);
      }
   }

   protected StateImplementation createState_foamfix_old(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
      return null;
   }
}
