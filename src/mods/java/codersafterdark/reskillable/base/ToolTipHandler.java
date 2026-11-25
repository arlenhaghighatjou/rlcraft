package codersafterdark.reskillable.base;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.event.CacheInvalidatedEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ToolTipHandler {
   private static Map<Class<? extends GuiScreen>, Function<ToolTipHandler.ToolTipInfo, List<String>>> tooltipInjectors = new HashMap();
   private static RequirementHolder lastLock = LevelLockHandler.EMPTY_LOCK;
   private static Class<? extends GuiScreen> currentGui;
   private static List<String> toolTip = new ArrayList();
   private static ItemStack lastItem;
   private static boolean enabled;

   public ToolTipHandler() {
   }

   public static void resetLast() {
      lastItem = null;
      lastLock = LevelLockHandler.EMPTY_LOCK;
      toolTip = new ArrayList();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onTooltip(ItemTooltipEvent event) {
      if (enabled && !event.isCanceled()) {
         ItemStack current = event.getItemStack();
         PlayerData data = PlayerDataHandler.get(Minecraft.getMinecraft().player);
         if (lastItem != current) {
            lastItem = current;
            lastLock = LevelLockHandler.getSkillLock(current);
            lastLock.addRequirementsIgnoreShift(data, toolTip = new ArrayList());
         }

         boolean showDetails = !ConfigHandler.hideRequirements || GuiScreen.isShiftKeyDown();
         List<String> extraToolTips = new ArrayList();
         if (currentGui != null) {
            ToolTipHandler.ToolTipInfo info = new ToolTipHandler.ToolTipInfo(showDetails, data, lastItem);

            for(Entry<Class<? extends GuiScreen>, Function<ToolTipHandler.ToolTipInfo, List<String>>> injectorInfo : tooltipInjectors.entrySet()) {
               if (((Class)injectorInfo.getKey()).isAssignableFrom(currentGui)) {
                  extraToolTips.addAll((Collection)((Function)injectorInfo.getValue()).apply(info));
               }
            }
         }

         if (!toolTip.isEmpty() || !extraToolTips.isEmpty()) {
            List<String> curTooltip = event.getToolTip();
            if (showDetails) {
               curTooltip.add(TextFormatting.DARK_PURPLE + (new TextComponentTranslation("reskillable.misc.requirements", new Object[0])).getUnformattedComponentText());
               curTooltip.addAll(toolTip);
            } else {
               curTooltip.add(TextFormatting.DARK_PURPLE + (new TextComponentTranslation("reskillable.misc.requirements_shift", new Object[0])).getUnformattedComponentText());
            }

            curTooltip.addAll(extraToolTips);
         }

      }
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onGuiOpen(GuiOpenEvent event) {
      if (enabled && !event.isCanceled()) {
         currentGui = event.getGui() == null ? null : event.getGui().getClass();
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void connect(ClientConnectedToServerEvent event) {
      enabled = true;
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void disconnect(ClientDisconnectionFromServerEvent event) {
      enabled = false;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onCacheInvalidated(CacheInvalidatedEvent event) {
      if (event.anyModified()) {
         resetLast();
      }

   }

   public static void addTooltipInjector(Class<? extends GuiScreen> gui, Function<ToolTipHandler.ToolTipInfo, List<String>> creator) {
      tooltipInjectors.put(gui, creator);
   }

   public static class ToolTipInfo {
      private boolean showDetails;
      private PlayerData data;
      private ItemStack item;

      public ToolTipInfo(boolean showDetails, PlayerData data, ItemStack item) {
         this.showDetails = showDetails;
         this.data = data;
         this.item = item;
      }

      public boolean showDetails() {
         return this.showDetails;
      }

      public PlayerData getData() {
         return this.data;
      }

      public ItemStack getItem() {
         return this.item;
      }
   }
}
