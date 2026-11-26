package codersafterdark.reskillable.client.base;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.base.CommonProxy;
import codersafterdark.reskillable.base.ToolTipHandler;
import codersafterdark.reskillable.client.gui.handler.InventoryTabHandler;
import codersafterdark.reskillable.client.gui.handler.KeyBindings;
import com.arlenh7.eaglercraft.forge.WorldTextFile;
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
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      KeyBindings.init();
      ClientTickHandler.register(MinecraftForge.EVENT_BUS);
      InventoryTabHandler.register(MinecraftForge.EVENT_BUS);
      HUDHandler.register(MinecraftForge.EVENT_BUS);
      ToolTipHandler.register(MinecraftForge.EVENT_BUS);
      ClientProxy.register(MinecraftForge.EVENT_BUS);
      (new KeyBindings()).register(MinecraftForge.EVENT_BUS);
   }

   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public static void register(EventBus bus) {
      bus.register(ClientConnectedToServerEvent.class, ClientProxy::connect);
   }

   public static void connect(ClientConnectedToServerEvent event) {
      new AdvancementManager((WorldTextFile)null);
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
