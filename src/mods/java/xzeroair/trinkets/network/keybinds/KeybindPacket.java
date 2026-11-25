package xzeroair.trinkets.network.keybinds;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.network.ThreadSafePacket;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;

public class KeybindPacket extends ThreadSafePacket {
   private int entityID;
   private boolean key;
   private boolean aux;
   private String ability;
   private int state;
   private int moveKey;

   public KeybindPacket() {
   }

   public KeybindPacket(EntityLivingBase entity, String ability, int moveKey, boolean key, boolean auxKey, int state) {
      this.entityID = entity.getEntityId();
      this.ability = ability;
      this.moveKey = moveKey;
      this.key = key;
      this.aux = auxKey;
      this.state = state;
   }

   public KeybindPacket(EntityLivingBase entity, String ability, boolean key, boolean auxKey, int state) {
      this(entity, ability, -1, key, auxKey, state);
   }

   public void toBytes(ByteBuf buffer) {
      buffer.writeInt(this.entityID);
      buffer.writeInt(this.state);
      buffer.writeInt(this.moveKey);
      buffer.writeBoolean(this.key);
      buffer.writeBoolean(this.aux);
      ByteBufUtils.writeUTF8String(buffer, this.ability);
   }

   public void fromBytes(ByteBuf buffer) {
      this.entityID = buffer.readInt();
      this.state = buffer.readInt();
      this.moveKey = buffer.readInt();
      this.key = buffer.readBoolean();
      this.aux = buffer.readBoolean();
      this.ability = ByteBufUtils.readUTF8String(buffer);
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      Entity entity = server.player.getEntityWorld().getEntityByID(this.entityID);
      Capabilities.getEntityProperties(entity, (prop) -> {
         try {
            IAbilityInterface kbAbility = prop.getAbilityHandler().getAbility(this.ability);
            if (kbAbility != null && kbAbility instanceof IKeyBindInterface) {
               IKeyBindInterface keybind = (IKeyBindInterface)kbAbility;
               if (this.key) {
                  if (this.state == 0) {
                     keybind.onKeyPress(entity, this.aux);
                  } else if (this.state == 1) {
                     keybind.onKeyDown(entity, this.aux);
                  }
               } else if (this.state == 2) {
                  keybind.onKeyRelease(entity, this.aux);
               }
            }
         } catch (Exception var5) {
            Trinkets.log.error("Trinkets had an Error with Ability:" + this.ability);
            var5.printStackTrace();
         }

      });
   }
}
