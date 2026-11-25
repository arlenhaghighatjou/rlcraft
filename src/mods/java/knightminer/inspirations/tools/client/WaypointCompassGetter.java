package knightminer.inspirations.tools.client;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import javax.annotation.Nullable;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WaypointCompassGetter implements IItemPropertyGetter {
   private Map<ItemStack, WaypointCompassGetter.Wobble> wobbleMap = new WeakHashMap();

   public WaypointCompassGetter() {
   }

   @SideOnly(Side.CLIENT)
   public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entityIn) {
      if (entityIn == null && !stack.isOnItemFrame()) {
         return 0.0F;
      } else {
         boolean isHeld = entityIn != null;
         Entity entity = (Entity)(isHeld ? entityIn : stack.getItemFrame());
         if (world == null) {
            if (entity.world == null) {
               return 0.0F;
            }

            world = entity.world;
         }

         double angle = this.getAngle(stack, world, entity, isHeld);
         if (entity == Minecraft.getMinecraft().player) {
            angle = this.wobble(stack, world, angle);
         }

         return MathHelper.positiveModulo((float)angle, 1.0F);
      }
   }

   @SideOnly(Side.CLIENT)
   private double getAngle(ItemStack stack, @Nullable World world, @Nullable Entity entity, boolean isHeld) {
      Integer dimension = ItemWaypointCompass.getDimension(stack);
      if (dimension != null) {
         BlockPos pos = ItemWaypointCompass.getPos(stack, dimension.intValue(), world.provider.getDimension());
         if (pos != null) {
            double entityAngle = isHeld ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
            entityAngle = MathHelper.positiveModulo(entityAngle / 360.0D, 1.0D);
            double posAngle = this.posToAngle(pos, entity) / 6.283185307179586D;
            return 0.5D - (entityAngle - 0.25D - posAngle);
         }
      }

      return Math.random();
   }

   @SideOnly(Side.CLIENT)
   private double getFrameRotation(EntityItemFrame frame) {
      return (double)MathHelper.wrapDegrees(180 + frame.facingDirection.getHorizontalIndex() * 90);
   }

   @SideOnly(Side.CLIENT)
   private double posToAngle(BlockPos pos, Entity entity) {
      return Math.atan2((double)pos.getZ() + 0.5D - entity.posZ, (double)pos.getX() + 0.5D - entity.posX);
   }

   @SideOnly(Side.CLIENT)
   private double wobble(ItemStack stack, World world, double angle) {
      WaypointCompassGetter.Wobble wobble = (WaypointCompassGetter.Wobble)this.wobbleMap.computeIfAbsent(stack, (s) -> {
         return new WaypointCompassGetter.Wobble();
      });
      if (world.getTotalWorldTime() != wobble.lastUpdateTick) {
         wobble.lastUpdateTick = world.getTotalWorldTime();
         double newAngle = angle - wobble.rotation;
         newAngle = MathHelper.positiveModulo(newAngle + 0.5D, 1.0D) - 0.5D;
         wobble.rota += newAngle * 0.1D;
         wobble.rota *= 0.8D;
         wobble.rotation = MathHelper.positiveModulo(wobble.rotation + wobble.rota, 1.0D);
      }

      return wobble.rotation;
   }

   private static class Wobble {
      double rotation;
      double rota;
      long lastUpdateTick;

      private Wobble() {
      }
   }
}
