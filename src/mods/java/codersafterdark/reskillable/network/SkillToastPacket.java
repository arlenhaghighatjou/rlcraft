package codersafterdark.reskillable.network;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.toast.ToastHelper;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SkillToastPacket implements IMessage, IMessageHandler<SkillToastPacket, IMessage> {
   private ResourceLocation skillName;
   private int level;

   public SkillToastPacket() {
   }

   public SkillToastPacket(ResourceLocation skillName, int level) {
      this.skillName = skillName;
      this.level = level;
   }

   public void fromBytes(ByteBuf buf) {
      this.skillName = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
      this.level = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.skillName.toString());
      buf.writeInt(this.level);
   }

   public IMessage onMessage(SkillToastPacket message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         return this.handleMessage(message, ctx);
      });
      return null;
   }

   public IMessage handleMessage(SkillToastPacket message, MessageContext ctx) {
      ToastHelper.sendSkillToast((Skill)ReskillableRegistries.SKILLS.getValue(message.skillName), message.level);
      return null;
   }
}
