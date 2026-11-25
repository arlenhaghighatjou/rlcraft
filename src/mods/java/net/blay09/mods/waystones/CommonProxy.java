package net.blay09.mods.waystones;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.register(MinecraftForge.EVENT_BUS);
   }

   public void register(EventBus bus) {
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLoggedIn);
      bus.register(PlayerChangedDimensionEvent.class, this::onPlayerChangedDimension);
      bus.register(PlayerRespawnEvent.class, this::onPlayerRespawn);
   }

   public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      GlobalWaystones globalWaystones = GlobalWaystones.get(event.player.world);
      PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(event.player);
      List<WaystoneEntry> validWaystones = Lists.newArrayList();

      for(WaystoneEntry waystone : waystoneData.getWaystones()) {
         if (!waystone.isGlobal() || globalWaystones.getGlobalWaystone(waystone.getName()) != null) {
            validWaystones.add(waystone);
         }
      }

      for(WaystoneEntry waystone : globalWaystones.getGlobalWaystones()) {
         if (!validWaystones.contains(waystone)) {
            validWaystones.add(waystone);
         }
      }

      PlayerWaystoneHelper.store(event.player, (WaystoneEntry[])validWaystones.toArray(new WaystoneEntry[validWaystones.size()]), waystoneData.getLastFreeWarp(), waystoneData.getLastWarpStoneUse());
      NetworkHandler.channel.sendTo(new MessageConfig(), (EntityPlayerMP)event.player);
      WaystoneManager.sendPlayerWaystones(event.player);
   }

   public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      WaystoneManager.sendPlayerWaystones(event.player);
   }

   public void onPlayerRespawn(PlayerRespawnEvent event) {
      WaystoneManager.sendPlayerWaystones(event.player);
   }

   public void openWaystoneSelection(EntityPlayer player, WarpMode mode, EnumHand hand, @Nullable WaystoneEntry fromWaystone) {
   }

   public void openWaystoneSettings(EntityPlayer player, WaystoneEntry waystone, boolean fromSelectionGui) {
   }

   public void playSound(SoundEvent soundEvent, BlockPos pos, float pitch) {
   }

   public boolean isVivecraftInstalled() {
      return false;
   }
}
