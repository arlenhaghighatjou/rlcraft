package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSession extends AbstractPacket {
   public ConfigBase config;
   public String modId;
   public HashMap<String, Object> vars;

   public PacketSession() {
   }

   public PacketSession(ConfigBase config) {
      this.config = config;
   }

   public void writeTo(ByteBuf buffer) {
      ByteBufUtils.writeUTF8String(buffer, this.config.getModId());

      for(Entry<Field, Object> e : this.config.session.entrySet()) {
         ByteBufUtils.writeUTF8String(buffer, ((Field)e.getKey()).getName());
         Object obj = e.getValue();
         Class clz = obj.getClass();
         if (!clz.equals(Integer.TYPE) && !clz.equals(Integer.class)) {
            if (clz.equals(int[].class)) {
               buffer.writeInt(1);
               int[] ints = (int[])obj;
               buffer.writeInt(ints.length);

               for(int i = 0; i < ints.length; ++i) {
                  buffer.writeInt(ints[i]);
               }
            } else if (clz.equals(NestedIntArray.class)) {
               buffer.writeInt(2);
               ByteBufUtils.writeUTF8String(buffer, ((NestedIntArray)obj).serialize());
            } else if (clz.equals(Colour.class)) {
               buffer.writeInt(3);
               ByteBufUtils.writeUTF8String(buffer, ((Colour)obj).serialize());
            } else if (clz.equals(String.class)) {
               buffer.writeInt(4);
               ByteBufUtils.writeUTF8String(buffer, (String)obj);
            } else {
               if (!clz.equals(String[].class)) {
                  throw new RuntimeException("Trying to send an unsupported field for a session: " + clz.getName());
               }

               buffer.writeInt(5);
               String[] strings = (String[])obj;
               buffer.writeInt(strings.length);

               for(int i = 0; i < strings.length; ++i) {
                  ByteBufUtils.writeUTF8String(buffer, strings[i]);
               }
            }
         } else {
            buffer.writeInt(0);
            buffer.writeInt(((Integer)obj).intValue());
         }
      }

      ByteBufUtils.writeUTF8String(buffer, "##endPacket");
   }

   public void readFrom(ByteBuf buffer) {
      this.modId = ByteBufUtils.readUTF8String(buffer);
      this.vars = new HashMap();

      for(String var = ByteBufUtils.readUTF8String(buffer); !var.equals("##endPacket"); var = ByteBufUtils.readUTF8String(buffer)) {
         int type = buffer.readInt();
         if (type == 0) {
            this.vars.put(var, Integer.valueOf(buffer.readInt()));
         } else if (type == 1) {
            int[] ints = new int[buffer.readInt()];

            for(int i = 0; i < ints.length; ++i) {
               ints[i] = buffer.readInt();
            }

            this.vars.put(var, ints);
         } else if (type == 2) {
            NestedIntArray ints = new NestedIntArray(new TreeMap());
            ints.deserialize(ByteBufUtils.readUTF8String(buffer), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.vars.put(var, ints);
         } else if (type == 3) {
            Colour clr = new Colour(0);
            clr.deserialize(ByteBufUtils.readUTF8String(buffer));
            this.vars.put(var, clr);
         } else if (type == 4) {
            this.vars.put(var, ByteBufUtils.readUTF8String(buffer));
         } else if (type == 5) {
            String[] ints = new String[buffer.readInt()];

            for(int i = 0; i < ints.length; ++i) {
               ints[i] = ByteBufUtils.readUTF8String(buffer);
            }

            this.vars.put(var, ints);
         }
      }

   }

   public void execute(Side side, EntityPlayer player) {
      for(ConfigBase conf : ConfigHandler.configs) {
         if (conf.getModId().equals(this.modId)) {
            for(Field field : conf.sessionProp) {
               try {
                  field.setAccessible(true);
                  if (this.vars.containsKey(field.getName())) {
                     Object ori = field.get(conf);
                     field.set(conf, this.vars.get(field.getName()));
                     conf.onSessionChange(field, ori);
                  }
               } catch (Exception var8) {
                  ;
               }
            }

            conf.onReceiveSession();
            break;
         }
      }

   }

   public Side receivingSide() {
      return Side.CLIENT;
   }

   public boolean requiresMainThread() {
      return false;
   }
}
