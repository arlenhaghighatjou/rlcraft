package xzeroair.trinkets.events;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.attributes.MagicAttributes;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.configsync.PacketConfigSync;
import xzeroair.trinkets.util.TrinketsConfig;

public class OnWorldJoinHandler {
   public OnWorldJoinHandler() {
   }

   @SubscribeEvent
   public void attachAttributes(EntityConstructing event) {
      if (event.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)event.getEntity();
         AbstractAttributeMap map = entity.getAttributeMap();
         map.registerAttribute(RaceAttribute.ENTITY_RACE);
         map.registerAttribute(JumpAttribute.Jump);
         map.registerAttribute(JumpAttribute.stepHeight);
         map.registerAttribute(MagicAttributes.MAX_MANA);
         map.registerAttribute(MagicAttributes.regen);
         map.registerAttribute(MagicAttributes.regenCooldown);
         map.registerAttribute(MagicAttributes.affinity);
      }

   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      if (player instanceof EntityPlayerMP) {
         EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
         Trinkets.log.info("Syncing Config to " + playerMP.getName());
         Map<String, String> configMap = TrinketsConfig.writeConfigMap();
         NetworkHandler.sendTo(new PacketConfigSync(configMap), playerMP);
         Capabilities.getEntityProperties(playerMP, EntityProperties::onLogin);
         Capabilities.getMagicStats(playerMP, (cap) -> {
            cap.sendManaToPlayer(playerMP);
         });
      }

   }

   @SubscribeEvent
   public void onPlayerLogout(PlayerLoggedOutEvent event) {
      EntityPlayer player = event.player;
      if (player instanceof EntityPlayerMP) {
         EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
         Capabilities.getEntityProperties(playerMP, EntityProperties::onLogoff);
      }

   }

   @SubscribeEvent
   public void entityJoinWorld(EntityJoinWorldEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (TrinketsConfig.SERVER.misc.retrieveVIP) {
            Capabilities.getVipStatus(player, (cap) -> {
               cap.sendStatusToPlayer(player);
            });
         }

         Capabilities.getEntityProperties(player, (cap) -> {
            cap.sendInformationToPlayer(player);
         });
         Capabilities.getMagicStats(player, (cap) -> {
            cap.sendManaToPlayer(player);
         });
      } else {
         Capabilities.getEntityProperties(entity, (prop) -> {
            prop.setLogin(true);
         });
      }

   }

   @SubscribeEvent
   public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
   }
}
