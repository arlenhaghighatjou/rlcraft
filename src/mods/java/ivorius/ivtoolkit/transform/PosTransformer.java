package ivorius.ivtoolkit.transform;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.MinecraftTransforms;
import ivorius.ivtoolkit.tools.EntityCreatureAccessor;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class PosTransformer {
   public PosTransformer() {
   }

   public static void transformTileEntityPos(TileEntity tileEntity, AxisAlignedTransform2D transform, int[] size) {
      if (tileEntity instanceof AreaTransformable) {
         ((AreaTransformable)tileEntity).transform(transform.getRotation(), transform.isMirrorX(), size);
      } else {
         Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
         tileEntity.mirror((Mirror)mct.getRight());
         tileEntity.rotate((Rotation)mct.getLeft());
         Mover.setTileEntityPos(tileEntity, transform.apply(tileEntity.getPos(), size));
      }

   }

   public static void transformEntityPos(Entity entity, AxisAlignedTransform2D transform, int[] size) {
      if (entity instanceof AreaTransformable) {
         ((AreaTransformable)entity).transform(transform.getRotation(), transform.isMirrorX(), size);
      } else {
         double[] newEntityPos = transform.applyOn(new double[]{entity.posX, entity.posY, entity.posZ}, size);
         Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
         float yaw = entity.getMirroredYaw((Mirror)mct.getRight());
         yaw = yaw + (entity.rotationYaw - entity.getRotatedYaw((Rotation)mct.getLeft()));
         entity.setLocationAndAngles(newEntityPos[0], newEntityPos[1], newEntityPos[2], yaw, entity.rotationPitch);
         if (entity instanceof EntityCreature) {
            EntityCreature entityCreature = (EntityCreature)entity;
            EntityCreatureAccessor.setHomePosition(entityCreature, transform.apply(entityCreature.getHomePosition(), size));
         }
      }

   }

   public static IBlockState transformBlockState(IBlockState state, AxisAlignedTransform2D transform) {
      return (IBlockState)MinecraftTransforms.map(transform, (rotation, mirror) -> {
         return state.withMirror(mirror).withRotation(rotation);
      });
   }

   /** @deprecated */
   @Deprecated
   public static void transformBlock(World world, BlockPos coord, AxisAlignedTransform2D transform) {
   }

   /** @deprecated */
   @Deprecated
   public static void transformBlock(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block) {
      transformBlockDefault(transform, world, state, coord, block);
   }

   /** @deprecated */
   @Deprecated
   public static void transformBlockDefault(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block) {
      IBlockState newState = state;
      ImmutableSet<Entry<IProperty<?>, Comparable<?>>> propertySet = state.getProperties().entrySet();
      UnmodifiableIterator var7 = propertySet.iterator();

      while(var7.hasNext()) {
         Entry<IProperty<?>, Comparable<?>> entry = (Entry)var7.next();
         IProperty property = (IProperty)entry.getKey();
         if (property.getValueClass() == EnumFacing.class && property.getAllowedValues().containsAll(Arrays.asList(EnumFacing.HORIZONTALS))) {
            EnumFacing value = (EnumFacing)entry.getValue();
            newState = newState.withProperty(property, transform.apply(value));
         }
      }

      if (newState != state) {
         world.setBlockState(coord, newState);
      }

   }
}
