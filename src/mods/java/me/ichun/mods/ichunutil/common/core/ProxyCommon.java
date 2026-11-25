package me.ichun.mods.ichunutil.common.core;

import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.packet.mod.PacketBlockEntityData;
import me.ichun.mods.ichunutil.common.packet.mod.PacketNewGrabbedEntityId;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatronInfo;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatrons;
import me.ichun.mods.ichunutil.common.packet.mod.PacketRequestBlockEntityData;
import me.ichun.mods.ichunutil.common.packet.mod.PacketSession;
import me.ichun.mods.ichunutil.common.packet.mod.PacketUserShouldShowUpdates;
import me.ichun.mods.ichunutil.common.thread.ThreadGetResources;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ProxyCommon {
   public ProxyCommon() {
   }

   public void preInit() {
      EventCalendar.checkDate();
      iChunUtil.eventHandlerServer = new EventHandlerServer();
      MinecraftForge.EVENT_BUS.register(iChunUtil.eventHandlerServer);
      EntityRegistry.registerModEntity(new ResourceLocation("ichunutil", "entity_block"), EntityBlock.class, "EntityBlock", 500, iChunUtil.instance, 160, 20, true);
      iChunUtil.channel = new PacketChannel("ichunutil", new Class[]{PacketSession.class, PacketPatronInfo.class, PacketPatrons.class, PacketUserShouldShowUpdates.class, PacketBlockEntityData.class, PacketNewGrabbedEntityId.class, PacketRequestBlockEntityData.class});
   }

   public void init() {
      OreDictionary.registerOre("blockCompactRawPorkchop", iChunUtil.blockCompactPorkchop);
      (new ThreadGetResources(FMLCommonHandler.instance().getSide())).start();
   }

   public void postInit() {
      iChunUtil.oreDictBlockCompactRawPorkchop = OreDictionary.getOres("blockCompactRawPorkchop");

      for(ConfigBase cfg : ConfigHandler.configs) {
         cfg.setup();
      }

      if (!iChunUtil.config.eulaAcknowledged.equalsIgnoreCase("true") && !iChunUtil.config.eulaAcknowledged.equalsIgnoreCase(this.getPlayerName())) {
         iChunUtil.LOGGER.info("=============================================================");
         iChunUtil.LOGGER.info(I18n.translateToLocal("ichunutil.eula.message"));
         iChunUtil.LOGGER.info(I18n.translateToLocal("ichunutil.eula.messageServer"));
         iChunUtil.LOGGER.info("=============================================================");
      }

   }

   public String getPlayerId() {
      return EntityHelper.uuidExample.toString().replaceAll("-", "");
   }

   public String getPlayerName() {
      return "Server";
   }

   public void setGameProfileLookupService() {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      EntityHelper.sessionService = server.getMinecraftSessionService();
      EntityHelper.profileCache = server.getPlayerProfileCache();
   }

   public void nudgeHand(float mag) {
   }

   public void adjustRotation(Entity entity, float yawChange, float pitchChange) {
      entity.prevRotationYaw += yawChange;
      entity.rotationYaw += yawChange;
      entity.prevRotationPitch += pitchChange;
      entity.rotationPitch += pitchChange;
      entity.prevRotationYaw %= 360.0F;

      for(entity.rotationYaw %= 360.0F; entity.prevRotationYaw < 0.0F; entity.prevRotationYaw += 360.0F) {
         ;
      }

      while(entity.rotationYaw < 0.0F) {
         entity.rotationYaw += 360.0F;
      }

      entity.prevRotationPitch %= 90.05F;
      entity.rotationPitch %= 90.05F;
   }

   public EntityPlayer getMcPlayer() {
      return null;
   }

   public void rebuildRecipeTable() {
   }

   @SideOnly(Side.CLIENT)
   public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing) {
      return bind;
   }

   @SideOnly(Side.CLIENT)
   public void registerMinecraftKeyBind(KeyBinding bind) {
   }
}
