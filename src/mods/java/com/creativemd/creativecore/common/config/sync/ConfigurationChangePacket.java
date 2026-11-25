package com.creativemd.creativecore.common.config.sync;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationChangePacket extends CreativeCorePacket {
   public String[] path;
   public JsonObject json;

   public ConfigurationChangePacket(ICreativeConfigHolder holder, JsonObject json) {
      this.path = holder.path();
      this.json = json;
   }

   public ConfigurationChangePacket(String[] path, JsonObject json) {
      this.path = path;
      this.json = json;
   }

   public ConfigurationChangePacket() {
   }

   public void writeBytes(ByteBuf buf) {
      writeString(buf, String.join(".", this.path));
      writeJson(buf, this.json);
   }

   public void readBytes(ByteBuf buf) {
      String text = readString(buf);
      this.path = text.isEmpty() ? new String[0] : text.split(".");
      this.json = readJson(buf);
   }

   public void executeClient(EntityPlayer player) {
   }

   public void executeServer(EntityPlayer player) {
      if (player.canUseCommand(2, "")) {
         CreativeConfigRegistry.ROOT.followPath(this.path).load(false, true, this.json, Side.SERVER);
         CreativeCore.configHandler.save(Side.SERVER);
         CreativeCore.configHandler.syncAll();
      } else {
         CreativeCore.configHandler.syncAll((EntityPlayerMP)player);
      }

   }
}
