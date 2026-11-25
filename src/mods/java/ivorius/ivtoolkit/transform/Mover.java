package ivorius.ivtoolkit.transform;

import ivorius.ivtoolkit.tools.EntityCreatureAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class Mover {
   public Mover() {
   }

   public static void moveTileEntity(TileEntity tileEntity, BlockPos dist) {
      if (tileEntity instanceof Movable) {
         ((Movable)tileEntity).move(dist);
      } else {
         moveTileEntityDefault(tileEntity, dist);
      }

   }

   public static void moveTileEntityDefault(TileEntity tileEntity, BlockPos dist) {
      tileEntity.setPos(tileEntity.getPos().add(dist));
   }

   public static void setTileEntityPos(TileEntity tileEntity, BlockPos coord) {
      moveTileEntity(tileEntity, coord.subtract(tileEntity.getPos()));
   }

   public static void moveEntity(Entity entity, BlockPos dist) {
      if (entity instanceof Movable) {
         ((Movable)entity).move(dist);
      } else {
         moveEntityDefault(entity, dist);
      }

   }

   public static void moveEntityDefault(Entity entity, BlockPos dist) {
      if (entity instanceof EntityHanging) {
         EntityHanging entityHanging = (EntityHanging)entity;
         BlockPos hangingPosition = entityHanging.getHangingPosition().add(dist);
         entityHanging.setPosition((double)hangingPosition.getX(), (double)hangingPosition.getY(), (double)hangingPosition.getZ());
      } else {
         entity.setPosition(entity.posX + (double)dist.getX(), entity.posY + (double)dist.getY(), entity.posZ + (double)dist.getZ());
      }

      if (entity instanceof EntityCreature) {
         EntityCreature entityCreature = (EntityCreature)entity;
         EntityCreatureAccessor.setHomePosition(entityCreature, entityCreature.getHomePosition().add(dist));
      }

   }
}
