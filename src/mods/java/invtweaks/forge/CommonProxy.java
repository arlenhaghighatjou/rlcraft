package invtweaks.forge;

import invtweaks.api.IItemTreeListener;
import invtweaks.api.InvTweaksAPI;
import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import invtweaks.integration.ItemListSorter;
import invtweaks.network.ITMessageToMessageCodec;
import invtweaks.network.ITPacketHandlerServer;
import invtweaks.network.packets.ITPacketLogin;
import io.netty.channel.ChannelHandler;
import java.util.EnumMap;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommonProxy implements InvTweaksAPI {
   protected static EnumMap<Side, FMLEmbeddedChannel> invtweaksChannel;
   @Nullable
   private static MinecraftServer server;

   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
   }

   public void init(FMLInitializationEvent e) {
      invtweaksChannel = NetworkRegistry.INSTANCE.newChannel("InventoryTweaks", new ChannelHandler[]{new ITMessageToMessageCodec()});
      ((FMLEmbeddedChannel)invtweaksChannel.get(Side.SERVER)).pipeline().addAfter("ITMessageToMessageCodec#0", "InvTweaks Handler Server", new ITPacketHandlerServer());
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void postInit(FMLPostInitializationEvent e) {
      ItemListSorter.LinkJEITComparator();
   }

   public void serverAboutToStart(@NotNull FMLServerAboutToStartEvent e) {
      server = e.getServer();
   }

   public void serverStopped(FMLServerStoppedEvent e) {
      server = null;
   }

   public void setServerAssistEnabled(boolean enabled) {
   }

   public void setServerHasInvTweaks(boolean hasInvTweaks) {
   }

   @SideOnly(Side.CLIENT)
   public void slotClick(PlayerControllerMP playerController, int windowId, int slot, int data, ClickType action, EntityPlayer player) {
   }

   public void sortComplete() {
   }

   public void addOnLoadListener(IItemTreeListener listener) {
   }

   public boolean removeOnLoadListener(IItemTreeListener listener) {
      return false;
   }

   public void setSortKeyEnabled(boolean enabled) {
   }

   public void setTextboxMode(boolean enabled) {
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j) {
      return 0;
   }

   public int compareItems(@NotNull ItemStack i, @NotNull ItemStack j, boolean onlyTreeSort) {
      return 0;
   }

   public void sort(ContainerSection section, SortingMethod method) {
   }

   @SubscribeEvent
   public void onPlayerLoggedIn(@NotNull PlayerLoggedInEvent e) {
      FMLEmbeddedChannel channel = (FMLEmbeddedChannel)invtweaksChannel.get(Side.SERVER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(e.player);
      channel.writeOutbound(new Object[]{new ITPacketLogin()});
   }

   public void addServerScheduledTask(@NotNull Runnable task) {
      server.addScheduledTask(task);
   }

   public void addClientScheduledTask(Runnable task) {
   }
}
