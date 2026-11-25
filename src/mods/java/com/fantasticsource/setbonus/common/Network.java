package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.server.ServerBonus;
import com.fantasticsource.setbonus.server.ServerData;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.HashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
   public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("setbonus");
   private static int discriminator = 0;

   public Network() {
   }

   public static void init() {
      WRAPPER.registerMessage(Network.ConfigPacketHandler.class, Network.ConfigPacket.class, discriminator++, Side.CLIENT);
      WRAPPER.registerMessage(Network.DiscoverBonusPacketHandler.class, Network.DiscoverBonusPacket.class, discriminator++, Side.CLIENT);
   }

   public static void updateConfig(EntityPlayerMP player) {
      WRAPPER.sendTo(new Network.ConfigPacket(player), player);
      ServerBonus.updateBonuses(player, true);
   }

   public static class ConfigPacket implements IMessage {
      public HashSet<String> equipment;
      public HashSet<String> sets;
      public HashSet<String> bonuses;
      public HashSet<String> attributeMods;
      public HashSet<String> potions;
      private EntityPlayerMP player;

      public ConfigPacket() {
         this.equipment = new HashSet();
         this.sets = new HashSet();
         this.bonuses = new HashSet();
         this.attributeMods = new HashSet();
         this.potions = new HashSet();
      }

      private ConfigPacket(EntityPlayerMP player) {
         this.equipment = new HashSet();
         this.sets = new HashSet();
         this.bonuses = new HashSet();
         this.attributeMods = new HashSet();
         this.potions = new HashSet();
         this.player = player;
      }

      public void toBytes(ByteBuf buf) {
         for(ServerBonus bonus : ServerData.bonuses.values()) {
            if (bonus.discoveryMode == Bonus.MODE_GLOBALLY_KNOWN || bonus.discoveryMode == Bonus.MODE_DISCOVERABLE && bonus.getBonusInstance(this.player).discovered) {
               this.bonuses.add(bonus.parsedString);

               for(ABonusRequirement bonusRequirement : bonus.bonusRequirements) {
                  if (bonusRequirement instanceof SetRequirement) {
                     Set set = ((SetRequirement)bonusRequirement).set;
                     this.sets.add(set.parsedString);

                     for(String equipName : set.involvedEquips.keySet()) {
                        this.equipment.add(((Equip)ServerData.equipment.get(equipName)).parsedString);
                     }
                  }
               }

               for(ABonusElement element : bonus.bonusElements) {
                  if (element instanceof ModifierBonus) {
                     this.attributeMods.add(element.parsedString);
                  } else if (element instanceof PotionBonus) {
                     this.potions.add(element.parsedString);
                  }
               }
            }
         }

         buf.writeInt(this.equipment.size());

         for(String string : this.equipment) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.sets.size());

         for(String string : this.sets) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.bonuses.size());

         for(String string : this.bonuses) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.attributeMods.size());

         for(String string : this.attributeMods) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.potions.size());

         for(String string : this.potions) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

      }

      public void fromBytes(ByteBuf buf) {
         for(int i = buf.readInt(); i > 0; --i) {
            this.equipment.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.sets.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.bonuses.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.attributeMods.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.potions.add(ByteBufUtils.readUTF8String(buf));
         }

      }
   }

   public static class ConfigPacketHandler implements IMessageHandler<Network.ConfigPacket, IMessage> {
      public ConfigPacketHandler() {
      }

      public IMessage onMessage(Network.ConfigPacket packet, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               ClientData.update(packet);
            });
         }

         return null;
      }
   }

   public static class DiscoverBonusPacket implements IMessage {
      public HashSet<String> equipment = new HashSet();
      public HashSet<String> sets = new HashSet();
      public String bonusString;
      public HashSet<String> attributeMods = new HashSet();
      public HashSet<String> potions = new HashSet();
      private ServerBonus bonus;

      public DiscoverBonusPacket() {
      }

      public DiscoverBonusPacket(ServerBonus bonus) {
         this.bonus = bonus;
      }

      public void toBytes(ByteBuf buf) {
         ByteBufUtils.writeUTF8String(buf, this.bonus.parsedString);

         for(ABonusRequirement bonusRequirement : this.bonus.bonusRequirements) {
            if (bonusRequirement instanceof SetRequirement) {
               Set set = ((SetRequirement)bonusRequirement).set;
               this.sets.add(set.parsedString);

               for(String equipName : set.involvedEquips.keySet()) {
                  this.equipment.add(((Equip)ServerData.equipment.get(equipName)).parsedString);
               }
            }
         }

         for(ABonusElement element : this.bonus.bonusElements) {
            if (element instanceof ModifierBonus) {
               this.attributeMods.add(element.parsedString);
            } else if (element instanceof PotionBonus) {
               this.potions.add(element.parsedString);
            }
         }

         buf.writeInt(this.equipment.size());

         for(String string : this.equipment) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.sets.size());

         for(String string : this.sets) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.attributeMods.size());

         for(String string : this.attributeMods) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

         buf.writeInt(this.potions.size());

         for(String string : this.potions) {
            ByteBufUtils.writeUTF8String(buf, string);
         }

      }

      public void fromBytes(ByteBuf buf) {
         this.bonusString = ByteBufUtils.readUTF8String(buf);

         for(int i = buf.readInt(); i > 0; --i) {
            this.equipment.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.sets.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.attributeMods.add(ByteBufUtils.readUTF8String(buf));
         }

         for(int i = buf.readInt(); i > 0; --i) {
            this.potions.add(ByteBufUtils.readUTF8String(buf));
         }

      }
   }

   public static class DiscoverBonusPacketHandler implements IMessageHandler<Network.DiscoverBonusPacket, IMessage> {
      public DiscoverBonusPacketHandler() {
      }

      public IMessage onMessage(Network.DiscoverBonusPacket packet, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               ClientData.update(packet);
            });
         }

         return null;
      }
   }
}
