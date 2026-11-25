package vazkii.quark.base.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.network.NetworkMessage;
import vazkii.arl.network.NetworkMessage.Reader;
import vazkii.arl.network.NetworkMessage.Writer;
import vazkii.quark.base.network.message.MessageChangeConfig;
import vazkii.quark.base.network.message.MessageChangeHotbar;
import vazkii.quark.base.network.message.MessageDeleteItem;
import vazkii.quark.base.network.message.MessageDisableDropoffClient;
import vazkii.quark.base.network.message.MessageDismountSeat;
import vazkii.quark.base.network.message.MessageDoEmote;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.base.network.message.MessageFavoriteItem;
import vazkii.quark.base.network.message.MessageHandleBackpack;
import vazkii.quark.base.network.message.MessageItemUpdate;
import vazkii.quark.base.network.message.MessageLinkItem;
import vazkii.quark.base.network.message.MessageMatrixEnchanterOperation;
import vazkii.quark.base.network.message.MessageRequestEmote;
import vazkii.quark.base.network.message.MessageRequestPassengerChest;
import vazkii.quark.base.network.message.MessageRestock;
import vazkii.quark.base.network.message.MessageRotateArrows;
import vazkii.quark.base.network.message.MessageSetLockProfile;
import vazkii.quark.base.network.message.MessageSortInventory;
import vazkii.quark.base.network.message.MessageSpamlessChat;
import vazkii.quark.base.network.message.MessageSwapItems;
import vazkii.quark.base.network.message.MessageSyncBoatBanner;
import vazkii.quark.base.network.message.MessageSyncChain;
import vazkii.quark.base.network.message.MessageSyncColors;
import vazkii.quark.base.network.message.MessageTuneNoteBlock;
import vazkii.quark.base.network.message.MessageUpdateAfk;
import vazkii.quark.misc.feature.LockDirectionHotkey;

public class MessageRegister {
   public MessageRegister() {
   }

   public static void init() {
      NetworkHandler.register(MessageDoEmote.class, Side.CLIENT);
      NetworkHandler.register(MessageDropoff.class, Side.SERVER);
      NetworkHandler.register(MessageSwapItems.class, Side.SERVER);
      NetworkHandler.register(MessageRestock.class, Side.SERVER);
      NetworkHandler.register(MessageFavoriteItem.class, Side.SERVER);
      NetworkHandler.register(MessageDisableDropoffClient.class, Side.CLIENT);
      NetworkHandler.register(MessageLinkItem.class, Side.SERVER);
      NetworkHandler.register(MessageChangeConfig.class, Side.CLIENT);
      NetworkHandler.register(MessageDeleteItem.class, Side.SERVER);
      NetworkHandler.register(MessageSortInventory.class, Side.SERVER);
      NetworkHandler.register(MessageTuneNoteBlock.class, Side.SERVER);
      NetworkHandler.register(MessageSetLockProfile.class, Side.SERVER);
      NetworkHandler.register(MessageChangeHotbar.class, Side.SERVER);
      NetworkHandler.register(MessageUpdateAfk.class, Side.SERVER);
      NetworkHandler.register(MessageRequestPassengerChest.class, Side.SERVER);
      NetworkHandler.register(MessageHandleBackpack.class, Side.SERVER);
      NetworkHandler.register(MessageRequestEmote.class, Side.SERVER);
      NetworkHandler.register(MessageMatrixEnchanterOperation.class, Side.SERVER);
      NetworkHandler.register(MessageSyncBoatBanner.class, Side.CLIENT);
      NetworkHandler.register(MessageItemUpdate.class, Side.CLIENT);
      NetworkHandler.register(MessageSpamlessChat.class, Side.CLIENT);
      NetworkHandler.register(MessageRotateArrows.class, Side.SERVER);
      NetworkHandler.register(MessageDismountSeat.class, Side.SERVER);
      NetworkHandler.register(MessageSyncColors.class, Side.CLIENT);
      NetworkHandler.register(MessageSyncChain.class, Side.CLIENT);
      NetworkMessage.mapHandler(LockDirectionHotkey.LockProfile.class, LockDirectionHotkey.LockProfile::readProfile, LockDirectionHotkey.LockProfile::writeProfile);
      NetworkMessage.mapHandler(ITextComponent.class, MessageRegister::readComponent, MessageRegister::writeComponent);
      NetworkMessage.mapHandler(EaglercraftUUID.class, MessageRegister::readUUID, MessageRegister::writeUUID);
   }

   private static ITextComponent readComponent(ByteBuf buf) {
      try {
         return (new PacketBuffer(buf)).readTextComponent();
      } catch (IOException var2) {
         return new TextComponentString("");
      }
   }

   private static void writeComponent(ITextComponent comp, ByteBuf buf) {
      (new PacketBuffer(buf)).writeTextComponent(comp);
   }

   private static EaglercraftUUID readUUID(ByteBuf buf) {
      return buf.readBoolean() ? null : (new PacketBuffer(buf)).readUniqueId();
   }

   private static void writeUUID(EaglercraftUUID uuid, ByteBuf buf) {
      if (uuid == null) {
         buf.writeBoolean(true);
      } else {
         buf.writeBoolean(false);
         (new PacketBuffer(buf)).writeUniqueId(uuid);
      }

   }
}
