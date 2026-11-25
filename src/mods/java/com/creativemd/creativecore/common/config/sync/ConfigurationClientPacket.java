package com.creativemd.creativecore.common.config.sync;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.event.ConfigEventHandler;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.type.CheckTree;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigurationClientPacket extends CreativeCorePacket {
   public String[] path;
   public List<String> enabled;

   public ConfigurationClientPacket(ICreativeConfigHolder holder, CheckTree<ConfigKey> tree) {
      this.path = holder.path();
      this.enabled = this.buildClientFieldList(tree.root, new ArrayList());
   }

   public ConfigurationClientPacket(ICreativeConfigHolder holder) {
      this.path = holder.path();
      this.enabled = ConfigEventHandler.loadClientFieldList(holder);
   }

   public List<String> buildClientFieldList(CheckTree<ConfigKey>.CheckTreeEntry entry, List<String> list) {
      if (entry.isEnabled() && entry.content != null) {
         String path;
         if (entry.parent != null && entry.parent.content != null) {
            path = String.join(".", ((ICreativeConfigHolder)((ConfigKey)entry.parent.content).get()).path()) + ".";
         } else {
            path = "";
         }

         list.add(path + ((ConfigKey)entry.content).name);
         return list;
      } else {
         if (entry.children != null) {
            for(CheckTree<ConfigKey>.CheckTreeEntry child : entry.children) {
               this.buildClientFieldList(child, list);
            }
         }

         return list;
      }
   }

   public ConfigurationClientPacket() {
   }

   public void writeBytes(ByteBuf buf) {
      writeString(buf, String.join(".", this.path));
      buf.writeInt(this.enabled.size());

      for(int i = 0; i < this.enabled.size(); ++i) {
         writeString(buf, (String)this.enabled.get(i));
      }

   }

   public void readBytes(ByteBuf buf) {
      String text = readString(buf);
      this.path = text.isEmpty() ? new String[0] : text.split(".");
      int size = buf.readInt();
      this.enabled = new ArrayList(size);

      for(int i = 0; i < size; ++i) {
         this.enabled.add(readString(buf));
      }

   }

   public ICreativeConfigHolder execute() {
      ICreativeConfigHolder holder = CreativeConfigRegistry.ROOT.followPath(this.path);
      if (holder != null) {
         ConfigEventHandler.saveClientFieldList(holder, this.enabled);
      }

      return holder;
   }

   public void executeClient(EntityPlayer player) {
      this.execute();
      ConfigurationPacket.updateGui(player);
   }

   public void executeServer(EntityPlayer player) {
      PacketHandler.sendPacketToAllPlayers(new ConfigurationClientPacket(this.execute()));
      CreativeCore.configHandler.saveClientFields();
      CreativeCore.configHandler.save(Side.SERVER);
   }
}
