package c4.champions.network;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.RankManager;
import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncAffix implements IMessage {
   private int entityId;
   private int tier;
   private Map<String, NBTTagCompound> affixData;
   private int num;
   private String name;

   public PacketSyncAffix() {
   }

   public PacketSyncAffix(int entityId, int tier, Map<String, NBTTagCompound> affixData, String name) {
      this.entityId = entityId;
      this.tier = tier;
      this.affixData = affixData;
      this.num = affixData.size();
      this.name = name;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.tier = buf.readInt();
      this.num = buf.readInt();
      Map<String, NBTTagCompound> data = Maps.newHashMap();

      for(int i = 0; i < this.num; ++i) {
         data.putIfAbsent(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readTag(buf));
      }

      this.affixData = data;
      this.name = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeInt(this.tier);
      buf.writeInt(this.num);

      for(Entry<String, NBTTagCompound> entry : this.affixData.entrySet()) {
         ByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         ByteBufUtils.writeTag(buf, (NBTTagCompound)entry.getValue());
      }

      ByteBufUtils.writeUTF8String(buf, this.name);
   }

   public static class PacketSyncHandler implements IMessageHandler<PacketSyncAffix, IMessage> {
      public PacketSyncHandler() {
      }

      public IMessage onMessage(PacketSyncAffix message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entity);
            if (chp != null) {
               chp.setRank(RankManager.getRankForTier(message.tier));
               chp.setAffixData(message.affixData);
               chp.setName(message.name);
            }

         });
         return null;
      }
   }
}
