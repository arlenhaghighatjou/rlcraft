package vazkii.quark.automation.feature;

import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class PistonsPushPullItems extends Feature {
   public static float force = 0.48F;

   public PistonsPushPullItems() {
   }

   public void setupConfig() {
      force = (float)this.loadPropDouble("Push Strength", "", (double)force);
   }

   public static void onPistonUpdate(TileEntityPiston piston) {
      if (ModuleLoader.isFeatureEnabled(PistonsPushPullItems.class) && !piston.getWorld().isRemote) {
         IBlockState state = piston.getPistonState();
         boolean pulling = state.getBlock() != Blocks.PISTON_HEAD;
         boolean sticky = state.getBlock() == Blocks.STICKY_PISTON || state.getPropertyKeys().contains(BlockPistonExtension.TYPE) && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY;
         if (pulling == sticky) {
            EnumFacing face = piston.getFacing();
            AxisAlignedBB aabb = new AxisAlignedBB(piston.getPos().offset(face, pulling ? 2 : 1));

            for(EntityItem entity : piston.getWorld().getEntitiesWithinAABB(EntityItem.class, aabb)) {
               onEntityHandled(entity, face, sticky);
            }

         }
      }
   }

   private static void onEntityHandled(EntityItem entity, EnumFacing face, boolean sticky) {
      if (sticky) {
         face = face.getOpposite();
      }

      World world = entity.getEntityWorld();
      if (sticky) {
         BlockPos offsetPos = entity.getPosition().offset(face);
         boolean closeToEdge = (new BlockPos(entity.posX + (double)face.getFrontOffsetX() * 0.5D, entity.posY + (double)face.getFrontOffsetY() * 0.5D, entity.posZ + (double)face.getFrontOffsetZ() * 0.5D)).equals(offsetPos);
         if (closeToEdge) {
            nudgeItem(world, entity, face, false);
         }
      } else {
         nudgeItem(world, entity, face, true);
      }

   }

   private static void nudgeItem(World world, EntityItem entity, EnumFacing whichWay, boolean showParticles) {
      float x = force * (float)whichWay.getFrontOffsetX();
      float y = force * (float)whichWay.getFrontOffsetY() * 1.5F;
      float z = force * (float)whichWay.getFrontOffsetZ();
      float px = x == 0.0F ? 0.4F : 0.0F;
      float py = y == 0.0F ? 0.4F : 0.0F;
      float pz = z == 0.0F ? 0.4F : 0.0F;
      entity.setPosition(entity.posX + 0.5D * (double)whichWay.getFrontOffsetX(), entity.posY + 0.5D * (double)whichWay.getFrontOffsetY(), entity.posZ + 0.5D * (double)whichWay.getFrontOffsetZ());
      entity.addVelocity((double)x, (double)y, (double)z);
      if (showParticles && world instanceof WorldServer) {
         ((WorldServer)world).spawnParticle(EnumParticleTypes.CRIT, entity.posX, entity.posY, entity.posZ, 12, (double)px, (double)py, (double)pz, 0.0D, new int[0]);
      }

   }
}
