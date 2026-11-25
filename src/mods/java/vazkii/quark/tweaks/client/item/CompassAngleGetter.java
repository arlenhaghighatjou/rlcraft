package vazkii.quark.tweaks.client.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.tweaks.feature.CompassesWorkEverywhere;

public class CompassAngleGetter implements IItemPropertyGetter {
   private static final String TAG_CALCULATED = "quark:compass_calculated";
   private static final String TAG_WAS_IN_NETHER = "quark:compass_in_nether";
   private static final String TAG_POSITION_SET = "quark:compass_position_set";
   private static final String TAG_NETHER_TARGET_X = "quark:nether_x";
   private static final String TAG_NETHER_TARGET_Z = "quark:nether_z";
   private double rotation;
   private double rota;
   private long lastUpdateTick;

   public CompassAngleGetter() {
   }

   public static void tickCompass(EntityPlayer player, ItemStack stack) {
      boolean calculated = isCalculated(stack);
      boolean nether = player.world.provider.getDimensionType() == DimensionType.NETHER;
      if (calculated) {
         boolean wasInNether = ItemNBTHelper.getBoolean(stack, "quark:compass_in_nether", false);
         BlockPos pos = player.getPosition();
         boolean isInPortal = player.world.getBlockState(pos).getBlock() == Blocks.PORTAL;
         if (nether && !wasInNether && isInPortal) {
            ItemNBTHelper.setInt(stack, "quark:nether_x", pos.getX());
            ItemNBTHelper.setInt(stack, "quark:nether_z", pos.getZ());
            ItemNBTHelper.setBoolean(stack, "quark:compass_in_nether", true);
            ItemNBTHelper.setBoolean(stack, "quark:compass_position_set", true);
         } else if (!nether && wasInNether) {
            ItemNBTHelper.setBoolean(stack, "quark:compass_in_nether", false);
            ItemNBTHelper.setBoolean(stack, "quark:compass_position_set", false);
         }
      } else {
         ItemNBTHelper.setBoolean(stack, "quark:compass_calculated", true);
         ItemNBTHelper.setBoolean(stack, "quark:compass_in_nether", nether);
      }

   }

   static boolean isCalculated(ItemStack stack) {
      return stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, "quark:compass_calculated", false);
   }

   @SideOnly(Side.CLIENT)
   public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
      if (entityIn == null && !stack.isOnItemFrame()) {
         return 0.0F;
      } else if (!CompassesWorkEverywhere.enableCompassNerf || stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, "quark:compass_calculated", false)) {
         boolean carried = entityIn != null;
         Entity entity = (Entity)(carried ? entityIn : stack.getItemFrame());
         if (entity == null) {
            return 0.0F;
         } else {
            if (worldIn == null) {
               worldIn = entity.world;
            }

            boolean calculate = false;
            BlockPos target = new BlockPos(0, 0, 0);
            if (worldIn.provider.isSurfaceWorld()) {
               calculate = true;
               target = worldIn.getSpawnPoint();
            } else if (worldIn.provider.getDimensionType() == DimensionType.THE_END && CompassesWorkEverywhere.enableEnd) {
               calculate = true;
            } else if (worldIn.provider.getDimensionType() == DimensionType.NETHER && isCalculated(stack) && CompassesWorkEverywhere.enableNether) {
               boolean set = ItemNBTHelper.getBoolean(stack, "quark:compass_position_set", false);
               if (set) {
                  int x = ItemNBTHelper.getInt(stack, "quark:nether_x", 0);
                  int z = ItemNBTHelper.getInt(stack, "quark:nether_z", 0);
                  calculate = true;
                  target = new BlockPos(x, 0, z);
               }
            }

            double angle;
            if (calculate) {
               double d1 = carried ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
               d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
               double d2 = this.getAngleToPosition(entity, target) / 6.283185307179586D;
               angle = 0.5D - (d1 - 0.25D - d2);
            } else {
               angle = Math.random();
            }

            if (carried) {
               angle = this.wobble(worldIn, angle);
            }

            return MathHelper.positiveModulo((float)angle, 1.0F);
         }
      } else {
         return 0.0F;
      }
   }

   private double wobble(World worldIn, double angle) {
      if (worldIn.getTotalWorldTime() != this.lastUpdateTick) {
         this.lastUpdateTick = worldIn.getTotalWorldTime();
         double d0 = angle - this.rotation;
         d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
         this.rota += d0 * 0.1D;
         this.rota *= 0.8D;
         this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
      }

      return this.rotation;
   }

   private double getFrameRotation(EntityItemFrame frame) {
      EnumFacing facing = frame.facingDirection;
      if (facing == null) {
         facing = EnumFacing.NORTH;
      }

      return (double)MathHelper.wrapDegrees(180.0F + facing.getHorizontalAngle());
   }

   private double getAngleToPosition(Entity entity, BlockPos blockpos) {
      return Math.atan2((double)blockpos.getZ() - entity.posZ, (double)blockpos.getX() - entity.posX);
   }
}
