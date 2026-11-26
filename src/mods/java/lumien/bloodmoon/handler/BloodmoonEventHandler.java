package lumien.bloodmoon.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import lumien.bloodmoon.Bloodmoon;
import lumien.bloodmoon.client.ClientBloodmoonHandler;
import lumien.bloodmoon.config.BloodmoonConfig;
import lumien.bloodmoon.server.BloodmoonHandler;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BloodmoonEventHandler {
   public BloodmoonEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(Load.class, this::loadWorld);
      bus.register(LivingDropsEvent.class, this::livingDrops);
      bus.register(LivingUpdateEvent.class, this::livingUpdate);
      bus.register(PlayerSleepInBedEvent.class, this::sleepInBed);
      bus.register(OnConfigChangedEvent.class, this::onConfigChange);
      bus.register(FogColors.class, this::fogColor);
      bus.register(EntityJoinWorldEvent.class, this::playerJoinedWorld);
      bus.register(WorldTickEvent.class, this::endWorldTick);
   }

   public void loadWorld(Load event) {
      if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
         BloodmoonHandler.INSTANCE = (BloodmoonHandler)event.getWorld().getMapStorage().getOrLoadData(BloodmoonHandler.class, "Bloodmoon");
         if (BloodmoonHandler.INSTANCE == null) {
            BloodmoonHandler.INSTANCE = new BloodmoonHandler();
            BloodmoonHandler.INSTANCE.markDirty();
         }

         event.getWorld().getMapStorage().setData("Bloodmoon", BloodmoonHandler.INSTANCE);
         BloodmoonHandler.INSTANCE.updateClients();
      }

   }

   public void livingDrops(LivingDropsEvent event) {
      if (!event.getEntityLiving().world.isRemote && event.getSource() == DamageSource.OUT_OF_WORLD && event.getEntityLiving().getEntityData().getBoolean("bloodmoonSpawned")) {
         event.setCanceled(true);
      }

   }

   public void livingUpdate(LivingUpdateEvent event) {
      if (BloodmoonConfig.GENERAL.VANISH && BloodmoonHandler.INSTANCE != null && event.getEntityLiving().dimension == 0 && !event.getEntityLiving().world.isRemote && !BloodmoonHandler.INSTANCE.isBloodmoonActive() && event.getEntityLiving().world.getTotalWorldTime() % 20L == 0L && Math.random() <= 0.20000000298023224D && event.getEntityLiving().getEntityData().getBoolean("bloodmoonSpawned")) {
         event.getEntityLiving().onKillCommand();
      }

   }

   public void sleepInBed(PlayerSleepInBedEvent event) {
      if (BloodmoonHandler.INSTANCE != null && BloodmoonConfig.GENERAL.NO_SLEEP && Bloodmoon.proxy.isBloodmoon()) {
         event.setResult(SleepResult.OTHER_PROBLEM);
         event.getEntityPlayer().sendMessage((new TextComponentTranslation("text.bloodmoon.nosleep", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)));
      }

   }

   public void onConfigChange(OnConfigChangedEvent event) {
      ConfigManager.sync("bloodmoon", Type.INSTANCE);
   }

   @SideOnly(Side.CLIENT)
   public void fogColor(FogColors event) {
      if (BloodmoonConfig.APPEARANCE.BLACK_FOG && ClientBloodmoonHandler.INSTANCE.isBloodmoonActive()) {
         event.setRed(Math.max(event.getRed() - ClientBloodmoonHandler.INSTANCE.fogRemove, 0.0F));
         event.setGreen(Math.max(event.getGreen() - ClientBloodmoonHandler.INSTANCE.fogRemove, 0.0F));
         event.setBlue(Math.max(event.getBlue() - ClientBloodmoonHandler.INSTANCE.fogRemove, 0.0F));
      }

   }

   public void playerJoinedWorld(EntityJoinWorldEvent event) {
      if (BloodmoonHandler.INSTANCE != null && !event.getWorld().isRemote) {
         BloodmoonHandler.INSTANCE.playerJoinedWorld(event);
      }

   }

   public void endWorldTick(WorldTickEvent event) {
      if (BloodmoonHandler.INSTANCE != null) {
         BloodmoonHandler.INSTANCE.endWorldTick(event);
      }

   }
}
