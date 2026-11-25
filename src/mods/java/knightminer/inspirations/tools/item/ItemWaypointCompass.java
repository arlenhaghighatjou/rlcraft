package knightminer.inspirations.tools.item;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.ClientUtil;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.client.WaypointCompassGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWaypointCompass extends Item {
   public static final String TAG_POS = "pos";
   public static final String TAG_DIMENSION = "dimension";
   public static final String TAG_CHECK_BEACON = "check_beacon";

   public ItemWaypointCompass() {
      this.setHasSubtypes(true);
      this.setCreativeTab(CreativeTabs.TOOLS);
      this.addPropertyOverride(new ResourceLocation("angle"), new WaypointCompassGetter());
   }

   public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!world.isRemote && stack.hasTagCompound() && world.getTotalWorldTime() % 160L == 20L) {
         Integer dimension = getDimension(stack);
         if (dimension != null) {
            if (dimension.intValue() == world.provider.getDimension()) {
               this.checkPos(stack, world, getPos(stack));
            } else if (Config.waypointCompassCrossDimension && world.getTotalWorldTime() % 320L == 20L) {
               World other = DimensionManager.getWorld(dimension.intValue());
               if (other != null) {
                  this.checkPos(stack, other, getPos(stack, dimension.intValue(), other.provider.getDimension()));
               }
            }
         }

      }
   }

   private void checkPos(ItemStack stack, World world, BlockPos pos) {
      if (pos != null && world.isBlockLoaded(pos, false)) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileEntityBeacon) {
            NBTTagCompound tags = stack.getTagCompound();
            if (((TileEntityBeacon)te).isComplete) {
               tags.removeTag("check_beacon");
            } else if (tags.getBoolean("check_beacon")) {
               clearNBT(stack);
            } else {
               stack.getTagCompound().setBoolean("check_beacon", true);
            }
         } else {
            clearNBT(stack);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
      DimensionType type = getDimensionType(stack);
      if (type != null) {
         String dimension = type.getName().toLowerCase(Locale.US);
         String key = "dimension." + dimension.replace(' ', '_') + ".name";
         if (Util.canTranslate(key)) {
            dimension = Util.translate(key);
         } else {
            dimension = ClientUtil.normalizeName(dimension);
         }

         if (flag == TooltipFlags.ADVANCED) {
            dimension = dimension + String.format(" (%d)", getDimension(stack));
            if (Config.waypointCompassAdvTooltip && !Minecraft.getMinecraft().isReducedDebug()) {
               BlockPos pos = getPos(stack);
               if (pos != null) {
                  dimension = Util.translateFormatted(this.getUnlocalizedName() + ".pos.tooltip", dimension, pos.getX(), pos.getZ());
               }
            }
         }

         tooltip.add(dimension);
      } else if (Config.craftWaypointCompass) {
         tooltip.add(TextFormatting.ITALIC + Util.translate(this.getUnlocalizedName() + ".blank.tooltip"));
      } else {
         tooltip.add(TextFormatting.ITALIC + Util.translateFormatted(this.getUnlocalizedName() + ".vanilla.tooltip", Util.translate(Items.COMPASS.getUnlocalizedName() + ".name")));
      }

   }

   @Nullable
   public static Integer getDimension(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         return null;
      } else {
         NBTTagCompound tags = stack.getTagCompound();
         return tags.hasKey("dimension", 99) ? tags.getInteger("dimension") : null;
      }
   }

   @Nullable
   public static DimensionType getDimensionType(ItemStack stack) {
      Integer dimension = getDimension(stack);
      if (dimension != null) {
         try {
            return DimensionManager.getProviderType(dimension.intValue());
         } catch (IllegalArgumentException var3) {
            ;
         }
      }

      return null;
   }

   @Nullable
   public static BlockPos getPos(ItemStack stack) {
      return !stack.hasTagCompound() ? null : TagUtil.readPos(stack.getTagCompound().getCompoundTag("pos"));
   }

   @Nullable
   public static BlockPos getPos(ItemStack stack, int compassDimension, int worldDimension) {
      BlockPos pos = getPos(stack);
      if (pos == null) {
         return null;
      } else {
         if (compassDimension != worldDimension) {
            if (!Config.waypointCompassCrossDimension) {
               return null;
            }

            if (compassDimension == DimensionType.NETHER.getId()) {
               return new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
            }

            if (worldDimension == DimensionType.NETHER.getId()) {
               return new BlockPos(Math.round((float)pos.getX() / 8.0F), pos.getY(), Math.round((float)pos.getZ() / 8.0F));
            }
         }

         return pos;
      }
   }

   public static void setNBT(@Nonnull ItemStack stack, @Nullable World world, @Nullable BlockPos pos) {
      if (world != null && pos != null) {
         setNBT(stack, world.provider.getDimension(), pos);
      } else {
         clearNBT(stack);
      }
   }

   private static void clearNBT(ItemStack stack) {
      if (stack.hasDisplayName()) {
         String name = stack.getDisplayName();
         stack.setTagCompound((NBTTagCompound)null);
         stack.setStackDisplayName(name);
      } else {
         stack.setTagCompound((NBTTagCompound)null);
      }

   }

   public static void copyNBT(@Nonnull ItemStack stack, @Nonnull ItemStack waypoint) {
      if (waypoint.hasTagCompound()) {
         setNBT(stack, getDimension(waypoint).intValue(), getPos(waypoint));
      }
   }

   private static void setNBT(@Nonnull ItemStack stack, int dimension, BlockPos pos) {
      if (pos == null) {
         stack.setTagCompound((NBTTagCompound)null);
      } else {
         NBTTagCompound tags = TagUtil.getTagSafe(stack);
         tags.setInteger("dimension", dimension);
         tags.setTag("pos", TagUtil.writePos(pos));
         stack.setTagCompound(tags);
      }
   }

   public static int getNeedleColor(EnumDyeColor color) {
      switch(color) {
      case WHITE:
         return 16761088;
      case SILVER:
         return EnumDyeColor.WHITE.colorValue;
      case GRAY:
         return EnumDyeColor.SILVER.colorValue;
      case BLACK:
         return EnumDyeColor.RED.colorValue;
      case RED:
         return EnumDyeColor.ORANGE.colorValue;
      case ORANGE:
         return EnumDyeColor.YELLOW.colorValue;
      case YELLOW:
         return 14393875;
      case LIME:
         return EnumDyeColor.BROWN.colorValue;
      case GREEN:
         return EnumDyeColor.LIME.colorValue;
      case CYAN:
         return EnumDyeColor.LIGHT_BLUE.colorValue;
      case LIGHT_BLUE:
         return 7842303;
      case BLUE:
         return 8279295;
      case PURPLE:
         return EnumDyeColor.MAGENTA.colorValue;
      case MAGENTA:
         return EnumDyeColor.PINK.colorValue;
      case PINK:
         return 15908814;
      case BROWN:
         return 10850418;
      default:
         return -1;
      }
   }

   public static boolean isWaypointCompass(ItemStack stack) {
      Item item = stack.getItem();
      return item == InspirationsTools.waypointCompass || !Config.craftWaypointCompass && item == Items.COMPASS;
   }
}
