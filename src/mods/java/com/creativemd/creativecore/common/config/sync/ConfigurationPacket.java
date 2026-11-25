package com.creativemd.creativecore.common.config.sync;

import com.creativemd.creativecore.common.config.gui.SubGuiClientSync;
import com.creativemd.creativecore.common.config.gui.SubGuiConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.google.gson.JsonObject;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationPacket extends CreativeCorePacket {
   public String[] path;
   public JsonObject json;
   public boolean ignoreRestart;

   public ConfigurationPacket(ICreativeConfigHolder holder, boolean ignoreRestart) {
      this.path = holder.path();
      this.json = holder.save(false, ignoreRestart, Side.SERVER);
      this.ignoreRestart = ignoreRestart;
   }

   public ConfigurationPacket() {
   }

   public void writeBytes(ByteBuf buf) {
      writeString(buf, String.join(".", this.path));
      writeJson(buf, this.json);
      buf.writeBoolean(this.ignoreRestart);
   }

   public void readBytes(ByteBuf buf) {
      String text = readString(buf);
      this.path = text.isEmpty() ? new String[0] : text.split(".");
      this.json = readJson(buf);
      this.ignoreRestart = buf.readBoolean();
   }

   public void executeClient(EntityPlayer player) {
      ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(this.path);
      if (holder != null) {
         holder.load(true, this.ignoreRestart, this.json, Side.SERVER);
      }

      updateGui(player);
   }

   public void executeServer(EntityPlayer player) {
   }

   public static void updateGui(EntityPlayer player) {
      if (player != null && player.openContainer instanceof ContainerSub && (((ContainerSub)player.openContainer).gui.isOpen(SubGuiConfig.class) || ((ContainerSub)player.openContainer).gui.isOpen(SubGuiClientSync.class))) {
         for(SubGui layer : ((ContainerSub)player.openContainer).gui.getLayers()) {
            if (layer instanceof SubGuiConfig) {
               ((SubGuiConfig)layer).ROOT = new JsonObject();
               ((SubGuiConfig)layer).loadHolder(((SubGuiConfig)layer).holder);
            } else if (layer instanceof SubGuiClientSync) {
               ((SubGuiClientSync)layer).tree.reload();
               ((SubGuiClientSync)layer).load(((SubGuiClientSync)layer).currentView);
            }
         }
      }

   }
}
