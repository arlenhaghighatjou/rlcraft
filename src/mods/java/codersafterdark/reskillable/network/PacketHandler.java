package codersafterdark.reskillable.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper("reskillable");
   public static int ID;

   public PacketHandler() {
   }

   public static void preInit() {
      INSTANCE.registerMessage(new MessageDataSync(), MessageDataSync.class, MessageDataSync::new, ID++, Side.CLIENT);
      INSTANCE.registerMessage(new MessageDodge(), MessageDodge.class, MessageDodge::new, ID++, Side.SERVER);
      INSTANCE.registerMessage(new MessageLevelUp(), MessageLevelUp.class, MessageLevelUp::new, ID++, Side.SERVER);
      INSTANCE.registerMessage(new MessageLockedItem(), MessageLockedItem.class, MessageLockedItem::new, ID++, Side.CLIENT);
      INSTANCE.registerMessage(new MessageUnlockUnlockable(), MessageUnlockUnlockable.class, MessageUnlockUnlockable::new, ID++, Side.SERVER);
      INSTANCE.registerMessage(new InvalidateRequirementPacket(), InvalidateRequirementPacket.class, InvalidateRequirementPacket::new, ID++, Side.CLIENT);
      INSTANCE.registerMessage(new InvalidateRequirementPacket(), InvalidateRequirementPacket.class, InvalidateRequirementPacket::new, ID++, Side.SERVER);
      INSTANCE.registerMessage(new UnlockableToastPacket(), UnlockableToastPacket.class, UnlockableToastPacket::new, ID++, Side.CLIENT);
      INSTANCE.registerMessage(new SkillToastPacket(), SkillToastPacket.class, SkillToastPacket::new, ID++, Side.CLIENT);
   }
}
