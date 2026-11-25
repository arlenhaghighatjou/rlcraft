package vazkii.quark.tweaks.feature;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.automation.block.BlockColorSlime;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.util.MutableVectorHolder;
import vazkii.quark.tweaks.block.BlockSpringySlime;

public class SpringySlime extends Feature {
   public static BlockSpringySlime springySlime;
   private static final ThreadLocal<MutableVectorHolder> motionRecorder = ThreadLocal.withInitial(MutableVectorHolder::new);

   public SpringySlime() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      springySlime = new BlockSpringySlime();
      springySlime.setUnlocalizedName("slime");
      OverrideRegistryHandler.registerBlock(springySlime, "slime");
   }

   public static void recordMotion(Entity entity) {
      MutableVectorHolder value = (MutableVectorHolder)motionRecorder.get();
      value.x = entity.motionX;
      value.y = entity.motionY;
      value.z = entity.motionZ;
   }

   public static void collideWithSlimeBlock(BlockPos pos, Entity entity) {
      if (entity instanceof EntityArrow) {
         EnumFacing sideHit = EnumFacing.getFacingFromVector((float)(entity.posX + entity.motionX) - ((float)pos.getX() + 0.5F), (float)(entity.posY + entity.motionY) - ((float)pos.getY() + 0.5F), (float)(entity.posZ + entity.motionZ) - ((float)pos.getZ() + 0.5F));
         switch(sideHit.getAxis()) {
         case X:
            if (Math.abs(entity.motionX) < 0.1D) {
               return;
            }

            entity.motionX = 0.8D * Math.min(Math.abs(entity.motionX), 0.25D) * (double)sideHit.getFrontOffsetX();
            break;
         case Y:
            if (Math.abs(entity.motionY) < 0.1D) {
               return;
            }

            entity.motionY = 0.8D * Math.min(Math.abs(entity.motionY), 0.25D) * (double)sideHit.getFrontOffsetY();
            break;
         case Z:
            if (Math.abs(entity.motionZ) < 0.1D) {
               return;
            }

            entity.motionZ = 0.8D * Math.min(Math.abs(entity.motionZ), 0.25D) * (double)sideHit.getFrontOffsetZ();
         }

         ObfuscationReflectionHelper.setPrivateValue(EntityArrow.class, (EntityArrow)entity, Boolean.valueOf(false), "inGround");
      }

   }

   public static void onEntityCollision(Entity entity, double attemptedX, double attemptedY, double attemptedZ, double dX, double dY, double dZ) {
      if (ModuleLoader.isFeatureEnabled(SpringySlime.class)) {
         if (!entity.isSneaking() && (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isFlying)) {
            double height = (double)entity.height;
            double width = (double)entity.width;
            double minX = entity.posX - width / 2.0D;
            double minY = entity.posY;
            double minZ = entity.posZ - width / 2.0D;
            double maxX = entity.posX + width / 2.0D;
            double maxY = entity.posY + height;
            double maxZ = entity.posZ + width / 2.0D;
            if (attemptedX != dX) {
               applyForAxis(entity, Axis.X, minX, minY, minZ, maxX, maxY, maxZ, dX, attemptedX);
            }

            if (attemptedY != dY) {
               applyForAxis(entity, Axis.Y, minX, minY, minZ, maxX, maxY, maxZ, dY, attemptedY);
            }

            if (attemptedZ != dZ) {
               applyForAxis(entity, Axis.Z, minX, minY, minZ, maxX, maxY, maxZ, dZ, attemptedZ);
            }

         }
      }
   }

   private static double axial(Axis axis, double we, double ud, double ns) {
      switch(axis) {
      case X:
         return we;
      case Y:
         return ud;
      default:
         return ns;
      }
   }

   private static void applyForAxis(Entity entity, Axis axis, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double dV, double attemptedV) {
      double baseValue = dV < 0.0D ? axial(axis, minX, minY, minZ) : axial(axis, maxX, maxY, maxZ);
      double clampedAttempt = attemptedV;
      if (Math.abs(attemptedV) > Math.abs(dV) + 1.0D) {
         clampedAttempt = dV + Math.signum(dV);
      }

      double v1 = baseValue + dV;
      double v2 = baseValue + clampedAttempt;
      double minV = Math.min(v1, v2);
      double maxV = Math.max(v1, v2);
      EnumFacing impactedSide = EnumFacing.getFacingFromAxis(dV < 0.0D ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);
      int lowXBound = (int)Math.floor(axial(axis, minV, minX, minX));
      int highXBound = (int)Math.floor(axial(axis, maxV, maxX, maxX));
      int lowYBound = (int)Math.floor(axial(axis, minY, minV, minY));
      int highYBound = (int)Math.floor(axial(axis, maxY, maxV, maxY));
      int lowZBound = (int)Math.floor(axial(axis, minZ, minZ, minV));
      int highZBound = (int)Math.floor(axial(axis, maxZ, maxZ, maxV));
      boolean restoredZ = false;

      for(BlockPos position : BlockPos.getAllInBoxMutable(lowXBound, lowYBound, lowZBound, highXBound, highYBound, highZBound)) {
         restoredZ = applyCollision(entity, position, impactedSide, restoredZ);
      }

   }

   private static boolean applyCollision(Entity entity, BlockPos position, EnumFacing impacted, boolean restoredMotion) {
      IBlockState state = entity.world.getBlockState(position);
      if (isSlime(state)) {
         if (impacted == EnumFacing.UP && entity instanceof EntityItem) {
            entity.onGround = false;
         }

         switch(impacted.getAxis()) {
         case X:
            if (!restoredMotion) {
               restoredMotion = true;
               entity.motionX = ((MutableVectorHolder)motionRecorder.get()).x;
            }

            entity.motionX = Math.abs(entity.motionX) * (double)impacted.getFrontOffsetX();
            if (!(entity instanceof EntityLivingBase)) {
               entity.motionX *= 0.8D;
            }
            break;
         case Y:
            if (!restoredMotion) {
               restoredMotion = true;
               entity.motionY = ((MutableVectorHolder)motionRecorder.get()).y;
            }

            entity.motionY = Math.abs(entity.motionY) * (double)impacted.getFrontOffsetY();
            if (!(entity instanceof EntityLivingBase)) {
               entity.motionY *= 0.8D;
            }
            break;
         case Z:
            if (!restoredMotion) {
               restoredMotion = true;
               entity.motionZ = ((MutableVectorHolder)motionRecorder.get()).z;
            }

            entity.motionZ = Math.abs(entity.motionZ) * (double)impacted.getFrontOffsetZ();
            if (!(entity instanceof EntityLivingBase)) {
               entity.motionZ *= 0.8D;
            }
         }
      }

      return restoredMotion;
   }

   private static boolean isSlime(IBlockState state) {
      Block block = state.getBlock();
      return block instanceof BlockSlime || block instanceof BlockColorSlime;
   }
}
