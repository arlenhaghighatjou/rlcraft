package vazkii.quark.client.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.feature.RightClickAddToShulkerBox;

public class ShulkerBoxTooltip extends Feature {
   public static final ResourceLocation WIDGET_RESOURCE = new ResourceLocation("quark", "textures/misc/shulker_widget.png");
   public static boolean useColors;
   public static boolean requireShift;
   public static List<ResourceLocation> shulkerBoxes;
   public static boolean dropoffAnyShulkerBox;
   private static final int[][] TARGET_RATIOS = new int[][]{{1, 1}, {9, 3}, {9, 5}, {9, 6}, {9, 8}, {9, 9}, {12, 9}};
   private static final int CORNER = 5;
   private static final int BUFFER = 1;
   private static final int EDGE = 18;

   public ShulkerBoxTooltip() {
   }

   public void setupConfig() {
      String[] shulkerArr = this.loadPropStringList("Shulker Boxes", "Blocks which should be interpreted as Shulker Boxes.", RightClickAddToShulkerBox.getBasicShulkerBoxes());
      shulkerBoxes = (List)Arrays.stream(shulkerArr).map(ResourceLocation::new).collect(Collectors.toList());
      dropoffAnyShulkerBox = this.loadPropBool("Dropoff to Any Shulker Box", "Allow anything with 'shulker_box' in its item identifier to be treated as a shulker box?", true);
      useColors = this.loadPropBool("Use Colors", "", true);
      requireShift = this.loadPropBool("Needs Shift to be visible", "", false);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void makeTooltip(ItemTooltipEvent event) {
      if (RightClickAddToShulkerBox.isShulkerBox(event.getItemStack(), shulkerBoxes, dropoffAnyShulkerBox) && event.getItemStack().hasTagCompound()) {
         Minecraft mc = Minecraft.getMinecraft();
         NBTTagCompound cmp = ItemNBTHelper.getCompound(event.getItemStack(), "BlockEntityTag", true);
         if (cmp != null) {
            if (!cmp.hasKey("id", 8)) {
               cmp = cmp.copy();
               cmp.setString("id", "minecraft:shulker_box");
            }

            TileEntity te = TileEntity.create(mc.world, cmp);
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
               List<String> tooltip = event.getToolTip();
               List<String> tooltipCopy = new ArrayList(tooltip);

               for(int i = 1; i < tooltipCopy.size(); ++i) {
                  String s = (String)tooltipCopy.get(i);
                  if (!s.startsWith("\u00a7") || s.startsWith("\u00a7o")) {
                     tooltip.remove(s);
                  }
               }

               if (requireShift && !GuiScreen.isShiftKeyDown()) {
                  tooltip.add(1, I18n.format("quarkmisc.shulkerBoxShift", new Object[0]));
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void renderTooltip(PostText event) {
      if (RightClickAddToShulkerBox.isShulkerBox(event.getStack(), shulkerBoxes, dropoffAnyShulkerBox) && event.getStack().hasTagCompound() && (!requireShift || GuiScreen.isShiftKeyDown())) {
         Minecraft mc = Minecraft.getMinecraft();
         NBTTagCompound cmp = ItemNBTHelper.getCompound(event.getStack(), "BlockEntityTag", true);
         if (cmp != null) {
            if (!cmp.hasKey("id", 8)) {
               cmp = cmp.copy();
               cmp.setString("id", "minecraft:shulker_box");
            }

            TileEntity te = TileEntity.create(mc.world, cmp);
            if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
               ItemStack currentBox = event.getStack();
               int currentX = event.getX() - 5;
               int currentY = event.getY() - 70;
               IItemHandler capability = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

               assert capability != null;

               int size = capability.getSlots();
               int[] dims = new int[]{Math.min(size, 9), Math.max(size / 9, 1)};

               for(int[] testAgainst : TARGET_RATIOS) {
                  if (testAgainst[0] * testAgainst[1] == size) {
                     dims = testAgainst;
                     break;
                  }
               }

               int texWidth = 10 + 18 * dims[0];
               if (currentY < 0) {
                  currentY = event.getY() + event.getLines().size() * 10 + 5;
               }

               ScaledResolution res = new ScaledResolution(mc);
               int right = currentX + texWidth;
               if (right > res.getScaledWidth()) {
                  currentX -= right - res.getScaledWidth();
               }

               GlStateManager.pushMatrix();
               RenderHelper.enableStandardItemLighting();
               GlStateManager.enableRescaleNormal();
               GlStateManager.color(1.0F, 1.0F, 1.0F);
               GlStateManager.translate(0.0F, 0.0F, 700.0F);
               mc.getTextureManager().bindTexture(WIDGET_RESOURCE);
               RenderHelper.disableStandardItemLighting();
               int color = -1;
               if (useColors && ((ItemBlock)currentBox.getItem()).getBlock() instanceof BlockShulkerBox) {
                  EnumDyeColor dye = ((BlockShulkerBox)((ItemBlock)currentBox.getItem()).getBlock()).getColor();
                  color = ItemDye.DYE_COLORS[dye.getDyeDamage()];
               }

               renderTooltipBackground(mc, currentX, currentY, dims[0], dims[1], color);
               RenderItem render = mc.getRenderItem();
               RenderHelper.enableGUIStandardItemLighting();
               GlStateManager.enableDepth();

               for(int i = 0; i < size; ++i) {
                  ItemStack itemstack = capability.getStackInSlot(i);
                  int xp = currentX + 6 + i % 9 * 18;
                  int yp = currentY + 6 + i / 9 * 18;
                  if (!itemstack.isEmpty()) {
                     render.renderItemAndEffectIntoGUI(itemstack, xp, yp);
                     render.renderItemOverlays(mc.fontRenderer, itemstack, xp, yp);
                  }

                  if (!ChestSearchBar.namesMatch(itemstack, ChestSearchBar.text)) {
                     GlStateManager.disableDepth();
                     Gui.drawRect(xp, yp, xp + 16, yp + 16, -1442840576);
                  }
               }

               GlStateManager.disableDepth();
               GlStateManager.disableRescaleNormal();
               GlStateManager.popMatrix();
            }
         }
      }

   }

   public static void renderTooltipBackground(Minecraft mc, int x, int y, int width, int height, int color) {
      mc.getTextureManager().bindTexture(WIDGET_RESOURCE);
      GlStateManager.color((float)((color & 16711680) >> 16) / 255.0F, (float)((color & '\uff00') >> 8) / 255.0F, (float)(color & 255) / 255.0F);
      RenderHelper.disableStandardItemLighting();
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 5, 5, 256.0F, 256.0F);
      Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * width, y + 5 + 18 * height, 25.0F, 25.0F, 5, 5, 256.0F, 256.0F);
      Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * width, y, 25.0F, 0.0F, 5, 5, 256.0F, 256.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y + 5 + 18 * height, 0.0F, 25.0F, 5, 5, 256.0F, 256.0F);

      for(int row = 0; row < height; ++row) {
         Gui.drawModalRectWithCustomSizedTexture(x, y + 5 + 18 * row, 0.0F, 6.0F, 5, 18, 256.0F, 256.0F);
         Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * width, y + 5 + 18 * row, 25.0F, 6.0F, 5, 18, 256.0F, 256.0F);

         for(int col = 0; col < width; ++col) {
            if (row == 0) {
               Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * col, y, 6.0F, 0.0F, 18, 5, 256.0F, 256.0F);
               Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * col, y + 5 + 18 * height, 6.0F, 25.0F, 18, 5, 256.0F, 256.0F);
            }

            Gui.drawModalRectWithCustomSizedTexture(x + 5 + 18 * col, y + 5 + 18 * row, 6.0F, 6.0F, 18, 18, 256.0F, 256.0F);
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
