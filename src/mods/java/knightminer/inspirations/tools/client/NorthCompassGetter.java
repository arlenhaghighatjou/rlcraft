package knightminer.inspirations.tools.client;

import javax.annotation.Nullable;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NorthCompassGetter implements IItemPropertyGetter {
   double rotation;
   double rota;
   long lastUpdateTick;

   public NorthCompassGetter() {
   }

   @SideOnly(Side.CLIENT)
   public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
      if (stack.isOnItemFrame()) {
         EntityItemFrame frame = stack.getItemFrame();
         EnumFacing facing = frame.getHorizontalFacing();
         return facing != null && facing.getAxis() != Axis.Y ? MathHelper.positiveModulo((float)facing.getHorizontalIndex() / 4.0F + 0.5F + (float)frame.getRotation() / 8.0F, 1.0F) : 0.0F;
      } else if (entity == null) {
         return 0.0F;
      } else {
         if (world == null) {
            if (entity.world == null) {
               return 0.0F;
            }

            world = entity.world;
         }

         double angle = (double)MathHelper.positiveModulo(entity.rotationYaw / 360.0F, 1.0F);
         return (float)(entity instanceof EntityPlayerSP ? this.wobble(world, angle) : angle);
      }
   }

   @SideOnly(Side.CLIENT)
   private double wobble(World world, double angle) {
      if (world.getTotalWorldTime() != this.lastUpdateTick) {
         this.lastUpdateTick = world.getTotalWorldTime();
         double d0 = angle - this.rotation;
         d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
         this.rota += d0 * 0.1D;
         this.rota *= 0.9D;
         this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
      }

      return this.rotation;
   }
}
