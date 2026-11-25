package net.blay09.mods.waystones.client;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.CommonProxy;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.WarpMode;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.gui.GuiButtonInventoryWarp;
import net.blay09.mods.waystones.client.gui.GuiConfirmInventoryButtonReturn;
import net.blay09.mods.waystones.client.gui.GuiEditWaystone;
import net.blay09.mods.waystones.client.gui.GuiWaystoneList;
import net.blay09.mods.waystones.client.render.RenderWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
   private GuiButtonInventoryWarp buttonWarp;
   private boolean isVivecraftInstalled;
   private static final List<String> tmpTooltip = Lists.newArrayList();

   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      MinecraftForge.EVENT_BUS.register(this);
      ClientRegistry.bindTileEntitySpecialRenderer(TileWaystone.class, new RenderWaystone());
      this.isVivecraftInstalled = ClientBrandRetriever.getClientModName().toLowerCase(Locale.ENGLISH).contains("vivecraft");
   }

   @SubscribeEvent
   public void onInitGui(Post event) {
      if (WaystoneConfig.general.teleportButton && event.getGui() instanceof GuiInventory) {
         this.buttonWarp = new GuiButtonInventoryWarp((GuiContainer)event.getGui());
         event.getButtonList().add(this.buttonWarp);
      }

   }

   @SubscribeEvent
   public void onActionPerformed(Pre event) {
      if (event.getButton() instanceof GuiButtonInventoryWarp) {
         EntityPlayer entityPlayer = FMLClientHandler.instance().getClientPlayerEntity();
         if (PlayerWaystoneHelper.canFreeWarp(entityPlayer)) {
            if (!WaystoneConfig.general.teleportButtonTarget.isEmpty()) {
               event.getGui().mc.displayGuiScreen(new GuiConfirmInventoryButtonReturn(WaystoneConfig.general.teleportButtonTarget));
            } else if (WaystoneConfig.general.teleportButtonReturnOnly) {
               if (PlayerWaystoneHelper.getLastWaystone(entityPlayer) != null) {
                  event.getGui().mc.displayGuiScreen(new GuiConfirmInventoryButtonReturn());
               }
            } else {
               Waystones.proxy.openWaystoneSelection(entityPlayer, WarpMode.INVENTORY_BUTTON, EnumHand.MAIN_HAND, (WaystoneEntry)null);
            }
         } else {
            event.getGui().mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 0.5F));
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onDrawScreen(net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post event) {
      if (event.getGui() instanceof GuiInventory && this.buttonWarp != null && this.buttonWarp.isHovered()) {
         tmpTooltip.clear();
         long timeSince = System.currentTimeMillis() - PlayerWaystoneHelper.getLastFreeWarp(FMLClientHandler.instance().getClientPlayerEntity());
         int secondsLeft = (int)(((long)(WaystoneConfig.general.teleportButtonCooldown * 1000) - timeSince) / 1000L);
         if (!WaystoneConfig.general.teleportButtonTarget.isEmpty()) {
            tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone", new Object[0]));
            tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", new Object[]{TextFormatting.DARK_AQUA + WaystoneConfig.general.teleportButtonTarget}));
            if (secondsLeft > 0) {
               tmpTooltip.add("");
            }
         } else if (WaystoneConfig.general.teleportButtonReturnOnly) {
            tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:returnToWaystone", new Object[0]));
            WaystoneEntry lastEntry = PlayerWaystoneHelper.getLastWaystone(FMLClientHandler.instance().getClientPlayerEntity());
            if (lastEntry != null) {
               tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", new Object[]{TextFormatting.DARK_AQUA + lastEntry.getName()}));
            } else {
               tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:boundTo", new Object[]{I18n.format("tooltip.waystones:none", new Object[0])}));
            }

            if (secondsLeft > 0) {
               tmpTooltip.add("");
            }
         } else {
            tmpTooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.waystones:openWaystoneMenu", new Object[0]));
         }

         if (secondsLeft > 0) {
            tmpTooltip.add(TextFormatting.GRAY + I18n.format("tooltip.waystones:cooldownLeft", new Object[]{secondsLeft}));
         }

         event.getGui().drawHoveringText(tmpTooltip, event.getMouseX(), event.getMouseY());
      }

   }

   @SubscribeEvent
   public void onFOV(FOVUpdateEvent event) {
      if (!event.getEntity().getActiveItemStack().isEmpty() && event.getEntity().getActiveItemStack().getItem() == Waystones.itemReturnScroll) {
         event.setNewfov((float)event.getEntity().getItemInUseCount() / 64.0F * 2.0F + 0.5F);
      }

   }

   public void openWaystoneSelection(EntityPlayer player, WarpMode mode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
      if (player == Minecraft.getMinecraft().player) {
         WaystoneEntry[] waystones = PlayerWaystoneData.fromPlayer(FMLClientHandler.instance().getClientPlayerEntity()).getWaystones();
         Minecraft.getMinecraft().displayGuiScreen(new GuiWaystoneList(waystones, mode, hand, fromWaystone));
      }

   }

   public void openWaystoneSettings(EntityPlayer player, WaystoneEntry waystone, boolean fromSelectionGui) {
      if (player == Minecraft.getMinecraft().player) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiEditWaystone(waystone, fromSelectionGui));
      }

   }

   public void playSound(SoundEvent sound, BlockPos pos, float pitch) {
      Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, WaystoneConfig.client.soundVolume, pitch, pos));
   }

   public boolean isVivecraftInstalled() {
      return this.isVivecraftInstalled;
   }
}
