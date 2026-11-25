package svenhjol.charm.tweaks.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.tweaks.item.ItemBoundCompass;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.PlayerHelper;

public class CompassBinding extends Feature {
   public static ItemBoundCompass boundCompass;
   public static List<Item> validItems = new ArrayList();
   public static boolean useBeacons;
   public static boolean useBeds;
   public static boolean useBanners;
   public static boolean showInformation;

   public CompassBinding() {
   }

   public String getDescription() {
      return "Right-click on a beacon whilst sneaking to bind the compass to the beacon's location.\nIf Quark is enabled the compass tooltip will be the same color as the beacon's beam.";
   }

   public void configure() {
      super.configure();
      showInformation = this.propBoolean("Add compass information", "If true, adds co-ordinates and dimension to the bound compass tooltip.", true);
      useBeacons = this.propBoolean("Use Beacons", "If true, compasses can bind to active beacons.", true);
      useBeds = this.propBoolean("Use Beds", "If true, compasses can bind to beds.", false);
      useBanners = this.propBoolean("Use Banners", "If true, compasses can bind to banners.", false);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      boundCompass = new ItemBoundCompass();
      validItems.add(Items.COMPASS);
      validItems.add(boundCompass);
   }

   @SubscribeEvent
   public void onCompassUse(RightClickBlock event) {
      if (!event.getWorld().isRemote && event.getEntityPlayer() != null && event.getEntityPlayer().isSneaking() && validItems.contains(event.getEntityPlayer().getHeldItem(event.getHand()).getItem())) {
         EntityPlayer player = event.getEntityPlayer();
         World world = event.getWorld();
         BlockPos pos = event.getPos();
         EnumHand hand = event.getHand();
         boolean validCompass = false;
         int color = 0;
         String name = "";
         ItemStack compass = new ItemStack(boundCompass);
         TileEntity tile = world.getTileEntity(pos);
         if (useBeacons && tile instanceof TileEntityBeacon) {
            TileEntityBeacon beacon = (TileEntityBeacon)tile;
            if (beacon.getLevels() < 1) {
               return;
            }

            for(int i = 256 - pos.getY(); i > 0; --i) {
               IBlockState above = world.getBlockState(pos.offset(EnumFacing.UP, i));
               if (above.getBlock() == Blocks.STAINED_GLASS) {
                  color = ((EnumDyeColor)above.getValue(BlockStainedGlass.COLOR)).getDyeDamage();
                  break;
               }
            }

            name = ((ITextComponent)Objects.requireNonNull(beacon.getDisplayName())).getUnformattedText();
            validCompass = true;
         }

         if (useBeds && tile instanceof TileEntityBed) {
            TileEntityBed bed = (TileEntityBed)tile;
            color = bed.getColor().getDyeDamage();
            if (bed.getDisplayName() != null) {
               name = bed.getDisplayName().getUnformattedText();
            }

            validCompass = true;
         }

         if (useBanners && tile instanceof TileEntityBanner) {
            TileEntityBanner banner = (TileEntityBanner)tile;
            List<EnumDyeColor> colorList = banner.getColorList();
            EnumDyeColor c = (EnumDyeColor)colorList.iterator().next();
            color = c.getDyeDamage();
            if (banner.getDisplayName() != null) {
               name = banner.getDisplayName().getUnformattedText();
            }

            validCompass = true;
         }

         if (!validCompass) {
            return;
         }

         BlockPos compassPos = new BlockPos(pos.getX(), world.provider.getDimension(), pos.getZ());
         name = name.length() > 0 ? name : "Bound Compass";
         compass.setStackDisplayName(name);
         ItemBoundCompass.setPos(compass, compassPos);
         ItemBoundCompass.setColor(compass, color);
         PlayerHelper.setHeldItem(player, hand, compass);
      }

   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      int xpCost = 0;
      ItemStack in = event.getLeft();
      ItemStack combine = event.getRight();
      if (!in.isEmpty() || !combine.isEmpty()) {
         if (in.getItem() == boundCompass && combine.getItem() == Items.IRON_INGOT) {
            ItemStack out = new ItemStack(Items.COMPASS);
            event.setCost(xpCost);
            event.setMaterialCost(1);
            event.setOutput(out);
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
