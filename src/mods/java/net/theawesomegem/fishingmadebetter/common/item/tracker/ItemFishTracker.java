package net.theawesomegem.fishingmadebetter.common.item.tracker;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.IFishingData;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemFishTracker extends Item {
   protected final ItemFishTracker.TrackingVision trackingVision;
   protected final FishData.FishingLiquid trackingLiquid;
   protected final int maxDepth;

   public ItemFishTracker(ItemFishTracker.TrackingVision trackingVision, FishData.FishingLiquid trackingLiquid, int maxDepth, String name) {
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
      this.trackingVision = trackingVision;
      this.trackingLiquid = trackingLiquid;
      this.maxDepth = maxDepth;
   }

   public ItemFishTracker.TrackingVision getTrackingVision() {
      return this.trackingVision;
   }

   public int getMaxDepth() {
      return this.maxDepth;
   }

   public FishData.FishingLiquid getLiquidEnum() {
      return this.trackingLiquid;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (worldIn.isRemote) {
         return new ActionResult(EnumActionResult.FAIL, itemstack);
      } else {
         IFishingData fishingData = (IFishingData)playerIn.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
         if (fishingData == null) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         } else if (fishingData.getTimeSinceTracking() > 0L) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         } else {
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
            if (raytraceresult == null) {
               return new ActionResult(EnumActionResult.PASS, itemstack);
            } else if (raytraceresult.typeOfHit != Type.BLOCK) {
               return new ActionResult(EnumActionResult.PASS, itemstack);
            } else {
               BlockPos pos = raytraceresult.getBlockPos();
               IBlockState blockClicked = worldIn.getBlockState(pos);
               if (this.trackingLiquid.equals(FishData.FishingLiquid.VOID)) {
                  if (pos.getY() > 3) {
                     playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.only_void", new Object[0]));
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  }
               } else if (this.trackingLiquid.equals(FishData.FishingLiquid.WATER)) {
                  if (blockClicked.getMaterial() != Material.WATER) {
                     playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.only_water", new Object[0]));
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  }
               } else if (this.trackingLiquid.equals(FishData.FishingLiquid.LAVA) && blockClicked.getMaterial() != Material.LAVA) {
                  playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.only_lava", new Object[0]));
                  return new ActionResult(EnumActionResult.FAIL, itemstack);
               }

               if (blockClicked.getMaterial() == MaterialLiquid.WATER) {
                  int waterCount = 0;

                  for(BlockPos posit : BlockPos.getAllInBox(pos.getX() - 2, pos.getY() - 3, pos.getZ() - 2, pos.getX() + 2, pos.getY(), pos.getZ() + 2)) {
                     Material mat = worldIn.getBlockState(posit).getMaterial();
                     if (mat == MaterialLiquid.WATER) {
                        ++waterCount;
                     }

                     if (waterCount >= 25) {
                        break;
                     }
                  }

                  if (waterCount < 25) {
                     playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.too_shallow", new Object[0]));
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  }
               }

               if (blockClicked.getMaterial() == MaterialLiquid.LAVA) {
                  int lavaCount = 0;

                  for(BlockPos posit : BlockPos.getAllInBox(pos.getX() - 2, pos.getY() - 3, pos.getZ() - 2, pos.getX() + 2, pos.getY(), pos.getZ() + 2)) {
                     Material mat = worldIn.getBlockState(posit).getMaterial();
                     if (mat == MaterialLiquid.LAVA) {
                        ++lavaCount;
                     }

                     if (lavaCount >= 25) {
                        break;
                     }
                  }

                  if (lavaCount < 25) {
                     playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.too_shallow", new Object[0]));
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  }
               }

               playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_tracker.tracking_start", new Object[0]));
               fishingData.setTimeSinceTracking(playerIn.world.getTotalWorldTime());
               return super.onItemRightClick(worldIn, playerIn, handIn);
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(I18n.format("item.fishingmadebetter.fish_tracker.tooltip.max_depth", new Object[0]) + ": " + this.maxDepth);
      String rarityText;
      if (ItemFishTracker.TrackingVision.BAD.equals(this.getTrackingVision())) {
         rarityText = "item.fishingmadebetter.fish_tracker.tooltip.vision_bad";
      } else if (ItemFishTracker.TrackingVision.NORMAL.equals(this.getTrackingVision())) {
         rarityText = "item.fishingmadebetter.fish_tracker.tooltip.vision_normal";
      } else if (ItemFishTracker.TrackingVision.BEST.equals(this.getTrackingVision())) {
         rarityText = "item.fishingmadebetter.fish_tracker.tooltip.vision_best";
      } else {
         rarityText = "item.fishingmadebetter.fish_tracker.tooltip.vision_none";
      }

      tooltip.add(I18n.format(rarityText, new Object[0]));
      String liquidText;
      if (FishData.FishingLiquid.WATER.equals(this.getLiquidEnum())) {
         liquidText = "item.fishingmadebetter.fish_tracker.tooltip.probe_water";
      } else if (FishData.FishingLiquid.LAVA.equals(this.getLiquidEnum())) {
         liquidText = "item.fishingmadebetter.fish_tracker.tooltip.probe_lava";
      } else if (FishData.FishingLiquid.VOID.equals(this.getLiquidEnum())) {
         liquidText = "item.fishingmadebetter.fish_tracker.tooltip.probe_void";
      } else {
         liquidText = "item.fishingmadebetter.fish_tracker.tooltip.probe_etc";
      }

      tooltip.add(I18n.format(liquidText, new Object[0]));
      tooltip.add(I18n.format("item.fishingmadebetter.fish_tracker.tooltip.right_click", new Object[0]));
      tooltip.add(I18n.format("item.fishingmadebetter.fish_tracker.tooltip.info.1", new Object[0]) + " " + TextFormatting.GOLD + Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName() + TextFormatting.RESET + "" + TextFormatting.GRAY + " " + I18n.format("item.fishingmadebetter.fish_tracker.tooltip.info.2", new Object[0]) + TextFormatting.RESET);
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("fishingmadebetter:tracker/" + this.getRegistryName().getResourcePath(), "inventory"));
   }

   public static enum TrackingVision {
      BAD,
      NORMAL,
      BEST;

      private TrackingVision() {
      }

      public static int getMinRarity(ItemFishTracker.TrackingVision enumTracking) {
         switch(enumTracking) {
         case BAD:
            return 40;
         case NORMAL:
            return 20;
         case BEST:
            return 0;
         default:
            return 40;
         }
      }
   }
}
