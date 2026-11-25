package lumberwizard.anvilpatch.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import lumberwizard.anvilpatch.AnvilPatch;
import lumberwizard.anvilpatch.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSync implements IMessage {
   private int levelCap;
   private ModConfig.EnumCostIncreaseSetting costIncreaseSetting;
   private boolean breakEnchantLevelCap;
   private float breakChance;

   public PacketConfigSync() {
   }

   public PacketConfigSync(int levelCap, ModConfig.EnumCostIncreaseSetting costIncreaseSetting, boolean breakEnchantLevelCap, float breakChance) {
      this.levelCap = levelCap;
      this.costIncreaseSetting = costIncreaseSetting;
      this.breakEnchantLevelCap = breakEnchantLevelCap;
      this.breakChance = breakChance;
   }

   public void fromBytes(ByteBuf buf) {
      this.levelCap = buf.readInt();
      this.costIncreaseSetting = ModConfig.EnumCostIncreaseSetting.values()[buf.readInt()];
      this.breakEnchantLevelCap = buf.readBoolean();
      this.breakChance = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.levelCap);
      buf.writeInt(this.costIncreaseSetting.ordinal());
      buf.writeBoolean(this.breakEnchantLevelCap);
      buf.writeFloat(this.breakChance);
   }

   public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            ModConfig.setValuesOverridden(true);
            ModConfig.setSyncedValues(message.levelCap, message.costIncreaseSetting, message.breakEnchantLevelCap, message.breakChance);
            AnvilPatch.logger.info("Synced configs from server");
         });
         return null;
      }
   }
}
