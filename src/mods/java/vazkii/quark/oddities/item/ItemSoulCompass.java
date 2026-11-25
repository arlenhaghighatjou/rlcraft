package vazkii.quark.oddities.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.oddities.feature.TotemOfHolding;

public class ItemSoulCompass extends ItemMod implements IQuarkItem, IItemPropertyGetter {
   private static final String TAG_POS_X = "posX";
   private static final String TAG_POS_Y = "posY";
   private static final String TAG_POS_Z = "posZ";
   @SideOnly(Side.CLIENT)
   private static double rotation;
   @SideOnly(Side.CLIENT)
   private static double rota;
   @SideOnly(Side.CLIENT)
   private static long lastUpdateTick;

   public ItemSoulCompass() {
      super("soul_compass", new String[0]);
      this.addPropertyOverride(new ResourceLocation("angle"), this);
      this.setCreativeTab(CreativeTabs.TOOLS);
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         BlockPos pos = TotemOfHolding.getPlayerDeathPosition(entityIn);
         ItemNBTHelper.setInt(stack, "posX", pos.getX());
         ItemNBTHelper.setInt(stack, "posY", pos.getY());
         ItemNBTHelper.setInt(stack, "posZ", pos.getZ());
      }

   }

   private BlockPos getPos(ItemStack stack) {
      if (stack.hasTagCompound()) {
         int x = ItemNBTHelper.getInt(stack, "posX", 0);
         int y = ItemNBTHelper.getInt(stack, "posY", -1);
         int z = ItemNBTHelper.getInt(stack, "posZ", 0);
         return new BlockPos(x, y, z);
      } else {
         return new BlockPos(0, -1, 0);
      }
   }

   @SideOnly(Side.CLIENT)
   public float apply(@Nonnull ItemStack stack, World world, EntityLivingBase entityIn) {
      if (entityIn == null && !stack.isOnItemFrame()) {
         return 0.0F;
      } else {
         boolean hasEntity = entityIn != null;
         Entity entity = (Entity)(hasEntity ? entityIn : stack.getItemFrame());
         if (entity == null) {
            return 0.0F;
         } else {
            if (world == null) {
               world = entity.world;
            }

            BlockPos pos = this.getPos(stack);
            double angle;
            if (pos.getY() == world.provider.getDimension()) {
               double yaw = hasEntity ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
               yaw = MathHelper.positiveModulo(yaw / 360.0D, 1.0D);
               double relAngle = this.getDeathToAngle(entity, pos) / 6.283185307179586D;
               angle = 0.5D - (yaw - 0.25D - relAngle);
            } else {
               angle = Math.random();
            }

            if (hasEntity) {
               angle = this.wobble(world, angle);
            }

            return MathHelper.positiveModulo((float)angle, 1.0F);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   private double wobble(World worldIn, double angle) {
      if (worldIn.getTotalWorldTime() != lastUpdateTick) {
         lastUpdateTick = worldIn.getTotalWorldTime();
         double relAngle = angle - rotation;
         relAngle = MathHelper.positiveModulo(relAngle + 0.5D, 1.0D) - 0.5D;
         rota += relAngle * 0.1D;
         rota *= 0.8D;
         rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
      }

      return rotation;
   }

   @SideOnly(Side.CLIENT)
   private double getFrameRotation(EntityItemFrame frame) {
      EnumFacing facing = frame.facingDirection;
      if (facing == null) {
         facing = EnumFacing.NORTH;
      }

      return (double)MathHelper.wrapDegrees(180.0F + facing.getHorizontalAngle());
   }

   @SideOnly(Side.CLIENT)
   private double getDeathToAngle(Entity entity, BlockPos blockpos) {
      return Math.atan2((double)blockpos.getZ() - entity.posZ, (double)blockpos.getX() - entity.posX);
   }
}
