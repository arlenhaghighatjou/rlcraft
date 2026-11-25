package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.misc.feature.LockDirectionHotkey;

public class MessageSetLockProfile extends NetworkMessage<MessageSetLockProfile> {
   public LockDirectionHotkey.LockProfile profile;

   public MessageSetLockProfile() {
   }

   public MessageSetLockProfile(LockDirectionHotkey.LockProfile profile) {
      this.profile = profile;
   }

   public IMessage handleMessage(MessageContext context) {
      LockDirectionHotkey.setProfile(context.getServerHandler().player, this.profile);
      return null;
   }
}
