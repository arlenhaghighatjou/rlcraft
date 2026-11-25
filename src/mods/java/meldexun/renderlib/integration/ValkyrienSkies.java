package meldexun.renderlib.integration;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.valkyrienskies.mod.common.ships.ship_transform.ShipTransformationManager;
import org.valkyrienskies.mod.common.ships.ship_world.PhysicsObject;
import org.valkyrienskies.mod.common.util.ValkyrienUtils;
import valkyrienwarfare.api.TransformType;

public class ValkyrienSkies {
   public ValkyrienSkies() {
   }

   public static AxisAlignedBB getAABB(TileEntity tileEntity) {
      return ValkyrienUtils.getAABBInGlobal(tileEntity.getRenderBoundingBox(), tileEntity.getWorld(), tileEntity.getPos());
   }

   public static BlockPos getPos(TileEntity tileEntity) {
      Optional var10000 = ValkyrienUtils.getPhysoManagingBlock(tileEntity.getWorld(), tileEntity.getPos()).map(PhysicsObject::getShipTransformationManager).map(ShipTransformationManager::getCurrentTickTransform).map((shipTransform) -> {
         return shipTransform.transform(tileEntity.getPos(), TransformType.SUBSPACE_TO_GLOBAL);
      });
      tileEntity.getClass();
      return (BlockPos)var10000.orElseGet(tileEntity::getPos);
   }

   public static boolean isOnShip(TileEntity tileEntity) {
      return ValkyrienUtils.getPhysoManagingBlock(tileEntity.getWorld(), tileEntity.getPos()).isPresent();
   }
}
