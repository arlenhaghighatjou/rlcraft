package ivorius.ivtoolkit.tools;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public abstract class IvFMLIntercommHandler {
   private Logger logger;
   private String modOwnerID;
   private Object modInstance;

   protected IvFMLIntercommHandler(Logger logger, String modOwnerID, Object modInstance) {
      this.logger = logger;
      this.modOwnerID = modOwnerID;
      this.modInstance = modInstance;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public void setLogger(Logger logger) {
      this.logger = logger;
   }

   public String getModOwnerID() {
      return this.modOwnerID;
   }

   public void setModOwnerID(String modOwnerID) {
      this.modOwnerID = modOwnerID;
   }

   public Object getModInstance() {
      return this.modInstance;
   }

   public void setModInstance(Object modInstance) {
      this.modInstance = modInstance;
   }

   public void handleMessages(boolean server, boolean runtime) {
      UnmodifiableIterator var3 = FMLInterModComms.fetchRuntimeMessages(this.modInstance).iterator();

      while(var3.hasNext()) {
         IMCMessage message = (IMCMessage)var3.next();
         this.onIMCMessage(message, server, true);
      }

   }

   public void onIMCMessage(IMCMessage message, boolean server, boolean runtime) {
      try {
         boolean didHandle = this.handleMessage(message, server, runtime);
         if (!didHandle) {
            this.logger.warn("Could not handle message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'");
         }
      } catch (Exception var5) {
         this.logger.error("Exception on message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'");
         var5.printStackTrace();
      }

   }

   protected abstract boolean handleMessage(IMCMessage var1, boolean var2, boolean var3);

   protected boolean isMessage(String key, IMCMessage message, Class expectedType) {
      if (key.equals(message.key)) {
         if (message.getMessageType().isAssignableFrom(expectedType)) {
            return true;
         }

         this.faultyMessage(message, expectedType);
      }

      return false;
   }

   protected Entity getEntity(NBTTagCompound compound, boolean server) {
      return this.getEntity(compound, "worldID", "entityID", server);
   }

   protected Entity getEntity(NBTTagCompound compound, String worldKey, String entityKey, boolean server) {
      return !server ? Minecraft.getMinecraft().world.getEntityByID(compound.getInteger(entityKey)) : FMLCommonHandler.instance().getSidedDelegate().getServer().getWorld(compound.getInteger(worldKey)).getEntityByID(compound.getInteger(entityKey));
   }

   protected boolean sendReply(IMCMessage message, String value) {
      if (message.getSender() == null) {
         return false;
      } else {
         NBTTagCompound cmp = message.getNBTValue();
         FMLInterModComms.sendRuntimeMessage(this.modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
         return true;
      }
   }

   protected boolean sendReply(IMCMessage message, NBTTagCompound value) {
      if (message.getSender() == null) {
         return false;
      } else {
         NBTTagCompound cmp = message.getNBTValue();
         FMLInterModComms.sendRuntimeMessage(this.modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
         return true;
      }
   }

   protected boolean sendReply(IMCMessage message, ItemStack value) {
      if (message.getSender() == null) {
         this.logger.error("Message error! Could not reply to message with key '" + message.key + "' - No sender found");
         return false;
      } else {
         NBTTagCompound cmp = message.getNBTValue();
         FMLInterModComms.sendRuntimeMessage(this.modOwnerID, message.getSender(), cmp.getString("replyKey"), value);
         return true;
      }
   }

   private void faultyMessage(IMCMessage message, Class expectedType) {
      this.logger.error("Got message with key '" + message.key + "' of type '" + message.getMessageType().getName() + "'; Expected type: '" + expectedType.getName() + "'");
   }
}
