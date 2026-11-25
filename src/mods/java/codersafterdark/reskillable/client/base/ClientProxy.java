package codersafterdark.reskillable.client.base;

import codersafterdark.reskillable.base.CommonProxy;
import codersafterdark.reskillable.base.ToolTipHandler;
import codersafterdark.reskillable.client.gui.handler.InventoryTabHandler;
import codersafterdark.reskillable.client.gui.handler.KeyBindings;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      KeyBindings.init();
      MinecraftForge.EVENT_BUS.register(ClientTickHandler.class);
      MinecraftForge.EVENT_BUS.register(InventoryTabHandler.class);
      MinecraftForge.EVENT_BUS.register(HUDHandler.class);
      MinecraftForge.EVENT_BUS.register(ToolTipHandler.class);
      MinecraftForge.EVENT_BUS.register(this.getClass());
      MinecraftForge.EVENT_BUS.register(new KeyBindings());
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   @SubscribeEvent
   public static void connect(ClientConnectedToServerEvent event) {
      new AdvancementManager((File)null);
   }

   @Nullable
   public AdvancementProgress getPlayerAdvancementProgress(EntityPlayer entityPlayer, Advancement advancement) {
      return (AdvancementProgress)Optional.ofNullable(Minecraft.getMinecraft().getConnection()).map(NetHandlerPlayClient::getAdvancementManager).map((advancementManager) -> {
         return advancementManager.advancementToProgress;
      }).map((advancementAdvancementProgressMap) -> {
         return (AdvancementProgress)advancementAdvancementProgressMap.get(advancement);
      }).orElseGet(AdvancementProgress::new);
   }

   public String getLocalizedString(String string) {
      return I18n.format(string, new Object[0]);
   }
}
