package svenhjol.charm.tweaks.item;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.tweaks.feature.CompassBinding;
import svenhjol.meson.MesonItem;
import svenhjol.meson.helper.ItemNBTHelper;
import svenhjol.meson.helper.TextHelper;

public class ItemBoundCompass extends MesonItem {
   private static final String POSX = "posX";
   private static final String POSY = "posY";
   private static final String POSZ = "posZ";
   private static final String COLOR = "Quark:ItemNameDye";
   private static final String ROTA = "rota";
   private static final String ROTATION = "rotation";
   private static final String LASTUPDATE = "lastUpdateTick";

   public ItemBoundCompass() {
      super("bound_compass");
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.MISC);
      this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
         public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null && !stack.isOnItemFrame()) {
               return 0.0F;
            } else {
               boolean hasEntity = entityIn != null;
               Entity entity = (Entity)(hasEntity ? entityIn : stack.getItemFrame());
               if (world == null) {
                  world = ((Entity)Objects.requireNonNull(entity)).world;
               }

               BlockPos pos = ItemBoundCompass.this.getPos(stack);
               boolean validDimension;
               if (pos != null) {
                  validDimension = world.provider.getDimension() == pos.getY();
               } else {
                  pos = world.provider.getSpawnPoint();
                  validDimension = world.provider.getDimension() == 0;
               }

               double angle;
               if (validDimension) {
                  double yaw = hasEntity ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
                  yaw = MathHelper.positiveModulo(yaw / 360.0D, 1.0D);
                  double relAngle = this.getPosToAngle(entity, pos) / 6.283185307179586D;
                  angle = 0.5D - (yaw - 0.25D - relAngle);
               } else {
                  angle = Math.random();
               }

               if (hasEntity) {
                  angle = this.wobble(world, stack, angle);
               }

               return MathHelper.positiveModulo((float)angle, 1.0F);
            }
         }

         @SideOnly(Side.CLIENT)
         private double wobble(World worldIn, ItemStack stack, double angle) {
            if (!stack.hasTagCompound()) {
               return 0.0D;
            } else {
               double rotation = ItemNBTHelper.getDouble(stack, "rotation", 0.0D);
               double rota = ItemNBTHelper.getDouble(stack, "rota", 0.0D);
               long lastUpdateTick = ItemNBTHelper.getLong(stack, "lastUpdateTick", 0L);
               if (worldIn.getTotalWorldTime() != lastUpdateTick) {
                  lastUpdateTick = worldIn.getTotalWorldTime();
                  double d0 = angle - rotation;
                  d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                  double var13 = rota + d0 * 0.1D;
                  rota = var13 * 0.8D;
                  rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);
               }

               ItemNBTHelper.setLong(stack, "lastUpdateTick", lastUpdateTick);
               ItemNBTHelper.setDouble(stack, "rotation", rotation);
               ItemNBTHelper.setDouble(stack, "rota", rota);
               return rotation;
            }
         }

         @SideOnly(Side.CLIENT)
         private double getFrameRotation(EntityItemFrame frame) {
            return (double)MathHelper.wrapDegrees(180 + ((EnumFacing)Objects.requireNonNull(frame.facingDirection)).getHorizontalIndex() * 90);
         }

         @SideOnly(Side.CLIENT)
         private double getPosToAngle(Entity entity, BlockPos pos) {
            return Math.atan2((double)pos.getZ() - entity.posZ, (double)pos.getX() - entity.posX);
         }
      });
   }

   public String getModId() {
      return "charm";
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (CompassBinding.showInformation) {
         BlockPos pos = this.getPos(stack);
         TextFormatting color = TextHelper.getTextFormattingByDyeDamage(getColor(stack));
         if (pos != null) {
            String x = String.valueOf(pos.getX());
            String z = String.valueOf(pos.getZ());
            String dim = String.valueOf(pos.getY());
            tooltip.add(color + x + " " + z + ", " + I18n.format("dimension", new Object[0]) + " " + dim);
         }

      }
   }

   public static ItemStack setPos(ItemStack stack, BlockPos pos) {
      ItemNBTHelper.setInt(stack, "posX", pos.getX());
      ItemNBTHelper.setInt(stack, "posY", pos.getY());
      ItemNBTHelper.setInt(stack, "posZ", pos.getZ());
      return stack;
   }

   public static ItemStack setColor(ItemStack stack, int meta) {
      if (meta > 0) {
         ItemNBTHelper.setInt(stack, "Quark:ItemNameDye", meta);
      }

      return stack;
   }

   public static int getColor(ItemStack stack) {
      return ItemNBTHelper.getInt(stack, "Quark:ItemNameDye", 0);
   }

   public BlockPos getPos(ItemStack stack) {
      if (stack.hasTagCompound()) {
         int x = ItemNBTHelper.getInt(stack, "posX", 0);
         int y = ItemNBTHelper.getInt(stack, "posY", 0);
         int z = ItemNBTHelper.getInt(stack, "posZ", 0);
         return new BlockPos(x, y, z);
      } else {
         return null;
      }
   }
}
