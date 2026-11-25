package vazkii.quark.tweaks.client.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;

public class ClockTimeGetter implements IItemPropertyGetter {
   private static final String TAG_CALCULATED = "quark:clock_calculated";
   private double rotation;
   private double rota;
   private long lastUpdateTick;

   public ClockTimeGetter() {
   }

   public static void tickClock(ItemStack stack) {
      boolean calculated = isCalculated(stack);
      if (!calculated) {
         ItemNBTHelper.setBoolean(stack, "quark:clock_calculated", true);
      }

   }

   static boolean isCalculated(ItemStack stack) {
      return stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, "quark:clock_calculated", false);
   }

   @SideOnly(Side.CLIENT)
   public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
      if (!isCalculated(stack)) {
         return 0.0F;
      } else {
         boolean carried = entityIn != null;
         Entity entity = (Entity)(carried ? entityIn : stack.getItemFrame());
         if (worldIn == null && entity != null) {
            worldIn = entity.world;
         }

         if (worldIn == null) {
            return 0.0F;
         } else {
            double angle;
            if (worldIn.provider.isSurfaceWorld()) {
               angle = (double)worldIn.getCelestialAngle(1.0F);
            } else {
               angle = Math.random();
            }

            angle = this.wobble(worldIn, angle);
            return (float)angle;
         }
      }
   }

   private double wobble(World world, double time) {
      if (world.getTotalWorldTime() != this.lastUpdateTick) {
         this.lastUpdateTick = world.getTotalWorldTime();
         double d0 = time - this.rotation;
         d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
         this.rota += d0 * 0.1D;
         this.rota *= 0.9D;
         this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
      }

      return this.rotation;
   }
}
