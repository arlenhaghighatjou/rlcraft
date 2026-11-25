package pl.asie.foamfix.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer.StateImplementation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class FoamyExtendedBlockStateContainer extends ExtendedBlockState {
   private PropertyValueMapper foamfix_mapper;

   public FoamyExtendedBlockStateContainer(Block blockIn, IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties) {
      super(blockIn, properties, unlistedProperties);
   }

   protected StateImplementation createState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
      if (block != null && !block.getClass().getName().startsWith("jds.bibliocraft")) {
         if (this.foamfix_mapper == null) {
            this.foamfix_mapper = new PropertyValueMapper(this);
         }

         if (!this.foamfix_mapper.isValid()) {
            return this.createState_foamfix_old(block, properties, unlistedProperties);
         } else if (unlistedProperties != null && !unlistedProperties.isEmpty()) {
            boolean has = false;
            UnmodifiableIterator var5 = unlistedProperties.values().iterator();

            while(var5.hasNext()) {
               Optional o = (Optional)var5.next();
               if (o.isPresent()) {
                  has = true;
                  break;
               }
            }

            return new FoamyExtendedBlockState(this.foamfix_mapper, block, properties, unlistedProperties, has);
         } else {
            return new FoamyBlockState(this.foamfix_mapper, block, properties);
         }
      } else {
         return this.createState_foamfix_old(block, properties, unlistedProperties);
      }
   }

   protected StateImplementation createState_foamfix_old(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties, ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
      return null;
   }
}
