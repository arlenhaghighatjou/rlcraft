package xzeroair.trinkets.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.util.compat.baubles.BaublesHelper;

public class SyncItemDataPacket extends ThreadSafePacket {
   NBTTagCompound tag;
   int slot;
   int handler;
   int entityID;
   ItemStack item;
   boolean syncStacks;
   boolean equipped;

   public SyncItemDataPacket() {
   }

   public SyncItemDataPacket(EntityLivingBase player, ItemStack stack, NBTTagCompound tag, int slot, TrinketHelper.SlotInformation.ItemHandlerType handler, boolean syncSlot, boolean equipped) {
      this(player, stack, tag, slot, handler.getId(), syncSlot, equipped);
   }

   public SyncItemDataPacket(EntityLivingBase player, ItemStack stack, NBTTagCompound tag, int slot, int handler, boolean syncSlot, boolean equipped) {
      this.slot = slot;
      this.handler = handler;
      if (tag == null) {
         this.tag = new NBTTagCompound();
      } else {
         this.tag = tag;
      }

      this.syncStacks = syncSlot;
      this.equipped = equipped;
      if (syncSlot) {
         this.item = stack;
      }

      this.entityID = player.getEntityId();
   }

   public SyncItemDataPacket(EntityLivingBase player, ItemStack stack, NBTTagCompound tag, int slot, int handler) {
      this(player, stack, tag, slot, handler, false, true);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.slot);
      buf.writeInt(this.handler);
      buf.writeInt(this.entityID);
      buf.writeBoolean(this.syncStacks);
      buf.writeBoolean(this.equipped);
      ByteBufUtils.writeTag(buf, this.tag);
      if (this.syncStacks) {
         ByteBufUtils.writeItemStack(buf, this.item);
      }

   }

   public void fromBytes(ByteBuf buf) {
      this.slot = buf.readInt();
      this.handler = buf.readInt();
      this.entityID = buf.readInt();
      this.syncStacks = buf.readBoolean();
      this.equipped = buf.readBoolean();
      this.tag = ByteBufUtils.readTag(buf);
      if (this.syncStacks) {
         this.item = ByteBufUtils.readItemStack(buf);
      }

   }

   private void handlePacket(EntityLivingBase entity, Side side) {
      try {
         TrinketHelper.SlotInformation.ItemHandlerType typeHandler = TrinketHelper.SlotInformation.ItemHandlerType.byID(this.handler);
         if (typeHandler.equals(TrinketHelper.SlotInformation.ItemHandlerType.TRINKETS)) {
            try {
               this.handleTrinkets(entity, side);
            } catch (Exception var6) {
               var6.printStackTrace();
            }
         } else if (typeHandler.equals(TrinketHelper.SlotInformation.ItemHandlerType.BAUBLES)) {
            try {
               this.handleBaubles(entity, side);
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   private void handleTrinkets(EntityLivingBase entity, Side side) {
      TrinketHelper.getTrinketHandler(entity, (trinkets) -> {
         if (this.equipped) {
            if (this.syncStacks && this.item != null && !this.item.isEmpty()) {
               trinkets.setStackInSlot(this.slot, this.item);
            }

            ItemStack stack = trinkets.getStackInSlot(this.slot);
            Capabilities.getTrinketProperties(stack, (properties) -> {
               if (side.isClient()) {
                  properties.itemEquipped(stack, entity);
                  properties.loadFromNBT(this.tag);
               } else {
                  properties.scheduleResync();
               }

            });
         } else if (this.syncStacks) {
            trinkets.setStackInSlot(this.slot, ItemStack.EMPTY);
         }

      });
   }

   private void handleBaubles(EntityLivingBase entity, Side side) {
      BaublesHelper.getBaublesHandler(entity, (baubles) -> {
         if (this.equipped) {
            if (this.syncStacks && this.item != null && !this.item.isEmpty()) {
               baubles.setStackInSlot(this.slot, this.item);
            }

            ItemStack stack = baubles.getStackInSlot(this.slot);
            Capabilities.getTrinketProperties(stack, (properties) -> {
               if (side.isClient()) {
                  properties.itemEquipped(stack, entity);
                  properties.loadFromNBT(this.tag);
               } else {
                  properties.scheduleResync();
               }

            });
         } else if (this.syncStacks) {
            baubles.setStackInSlot(this.slot, ItemStack.EMPTY);
         }

      });
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
      World world = clientPlayer.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityID);
      if (entity instanceof EntityLivingBase) {
         this.handlePacket((EntityLivingBase)entity, Side.CLIENT);
      }

   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      EntityPlayerMP serverPlayer = server.player;
      WorldServer world = serverPlayer.getServerWorld();
      Entity entity = world.getEntityByID(this.entityID);
      if (entity instanceof EntityLivingBase) {
         this.handlePacket((EntityLivingBase)entity, Side.SERVER);
      }

   }
}
