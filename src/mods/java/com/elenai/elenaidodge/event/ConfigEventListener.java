package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.capability.joined.IJoined;
import com.elenai.elenaidodge.capability.joined.JoinedProvider;
import com.elenai.elenaidodge.capability.regen.IRegen;
import com.elenai.elenaidodge.capability.regen.RegenProvider;
import com.elenai.elenaidodge.capability.weight.IWeight;
import com.elenai.elenaidodge.capability.weight.WeightProvider;
import com.elenai.elenaidodge.network.PacketHandler;
import com.elenai.elenaidodge.network.message.CPatronMessage;
import com.elenai.elenaidodge.network.message.CUpdateRegenMessage;
import com.elenai.elenaidodge.network.message.CUpdateWeightMessage;
import com.elenai.elenaidodge.util.PatronRewardHandler;
import com.elenai.elenaidodge.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigEventListener {
   public ConfigEventListener() {
   }

   @SubscribeEvent
   public void onConfigChange(OnConfigChangedEvent event) {
      if (event.getModID().equals("elenaidodge")) {
         ConfigManager.sync("elenaidodge", Type.INSTANCE);
      }

      if (event.isWorldRunning()) {
         Utils.updateClientConfig();
      }

   }

   @SubscribeEvent
   public void playerJoinsWorld(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityPlayerMP && !event.getEntity().world.isRemote) {
         PacketHandler.instance.sendTo(new CPatronMessage(PatronRewardHandler.getTier((EntityPlayerMP)event.getEntity())), (EntityPlayerMP)event.getEntity());
         Utils.updateClientConfig((EntityPlayerMP)event.getEntity());
         IWeight w = (IWeight)event.getEntity().getCapability(WeightProvider.WEIGHT_CAP, (EnumFacing)null);
         PacketHandler.instance.sendTo(new CUpdateWeightMessage(w.getWeight()), (EntityPlayerMP)event.getEntity());
         IRegen r = (IRegen)event.getEntity().getCapability(RegenProvider.REGEN_CAP, (EnumFacing)null);
         PacketHandler.instance.sendTo(new CUpdateRegenMessage(r.getRegen()), (EntityPlayerMP)event.getEntity());
         IJoined j = (IJoined)event.getEntity().getCapability(JoinedProvider.JOINED_CAP, (EnumFacing)null);
         if (!j.hasJoined()) {
            Utils.initPlayer((EntityPlayer)event.getEntity());
            j.join();
         }
      }

   }
}
