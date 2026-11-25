package vazkii.quark.management.feature;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.capability.ShulkerBoxDropIn;

public class RightClickAddToShulkerBox extends Feature {
   public static List<ResourceLocation> shulkerBoxes;
   public static boolean dropoffAnyShulkerBox;
   private static final ResourceLocation SHULKER_BOX_CAP = new ResourceLocation("quark", "shulker_box_drop_in");

   public RightClickAddToShulkerBox() {
   }

   public static String[] getBasicShulkerBoxes() {
      return (String[])ImmutableSet.of(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, new Block[]{Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX}).stream().map(Impl::getRegistryName).map(Objects::toString).toArray((x$0) -> {
         return new String[x$0];
      });
   }

   public static boolean isShulkerBox(ItemStack stack, List<ResourceLocation> boxes, boolean acceptGeneric) {
      return !stack.isEmpty() && isShulkerBox(stack.getItem().getRegistryName(), boxes, acceptGeneric);
   }

   public static boolean isShulkerBox(Block block, List<ResourceLocation> boxes, boolean acceptGeneric) {
      return isShulkerBox(block.getRegistryName(), boxes, acceptGeneric);
   }

   public static boolean isShulkerBox(ResourceLocation loc, List<ResourceLocation> boxes, boolean acceptGeneric) {
      if (loc == null) {
         return false;
      } else if (boxes.contains(loc)) {
         return true;
      } else {
         return acceptGeneric && loc.toString().contains("shulker_box");
      }
   }

   public void setupConfig() {
      String[] shulkerArr = this.loadPropStringList("Shulker Boxes", "Blocks which should be interpreted as Shulker Boxes.", getBasicShulkerBoxes());
      shulkerBoxes = (List)Arrays.stream(shulkerArr).map(ResourceLocation::new).collect(Collectors.toList());
      dropoffAnyShulkerBox = this.loadPropBool("Dropoff to Any Shulker Box", "Allow anything with 'shulker_box' in its item identifier to be treated as a shulker box?", true);
   }

   @SubscribeEvent
   public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
      if (isShulkerBox((ItemStack)event.getObject(), shulkerBoxes, dropoffAnyShulkerBox)) {
         event.addCapability(SHULKER_BOX_CAP, new ShulkerBoxDropIn());
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
