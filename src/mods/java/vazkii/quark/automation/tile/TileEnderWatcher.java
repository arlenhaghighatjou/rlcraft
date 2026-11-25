package vazkii.quark.automation.tile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.automation.block.BlockEnderWatcher;

public class TileEnderWatcher extends TileMod implements ITickable {
   public TileEnderWatcher() {
   }

   public void update() {
      if (!this.getWorld().isRemote) {
         boolean wasLooking = ((Boolean)this.getWorld().getBlockState(this.getPos()).getValue(BlockEnderWatcher.WATCHED)).booleanValue();
         int range = 80;
         List<EntityPlayer> players = this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.pos.add(-range, -range, -range), this.pos.add(range, range, range)));
         boolean looking = false;

         for(EntityPlayer player : players) {
            ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (helm.isEmpty() || helm.getItem() != Item.getItemFromBlock(Blocks.PUMPKIN)) {
               RayTraceResult pos = raytraceFromEntity(this.getWorld(), player, true, 64.0D);
               if (pos != null && pos.getBlockPos().equals(this.getPos())) {
                  looking = true;
                  break;
               }
            }
         }

         if (looking != wasLooking && !this.getWorld().isRemote) {
            this.getWorld().setBlockState(this.getPos(), this.getWorld().getBlockState(this.getPos()).withProperty(BlockEnderWatcher.WATCHED, Boolean.valueOf(looking)), 3);
         }

         if (looking) {
            double x = (double)this.getPos().getX() - 0.1D + Math.random() * 1.2D;
            double y = (double)this.getPos().getY() - 0.1D + Math.random() * 1.2D;
            double z = (double)this.getPos().getZ() - 0.1D + Math.random() * 1.2D;
            ((WorldServer)this.getWorld()).spawnParticle(EnumParticleTypes.REDSTONE, false, x, y, z, 0, 1.0D, 0.0D, 0.0D, 1.0D, new int[0]);
         }

      }
   }

   public static RayTraceResult raytraceFromEntity(World world, Entity player, boolean par3, double range) {
      float f = 1.0F;
      float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
      float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
      double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double)f;
      double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double)f;
      if (player instanceof EntityPlayer) {
         d1 += (double)((EntityPlayer)player).eyeHeight;
      }

      double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)f;
      Vec3d vec3 = new Vec3d(d0, d1, d2);
      float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
      float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
      float f5 = -MathHelper.cos(-f1 * 0.017453292F);
      float f6 = MathHelper.sin(-f1 * 0.017453292F);
      float f7 = f4 * f5;
      float f8 = f3 * f5;
      Vec3d vec31 = vec3.addVector((double)f7 * range, (double)f6 * range, (double)f8 * range);
      return world.rayTraceBlocks(vec3, vec31, par3);
   }
}
