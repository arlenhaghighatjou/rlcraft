package codersafterdark.reskillable.network;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.base.ExperienceHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageLevelUp implements IMessage, IMessageHandler<MessageLevelUp, IMessage> {
   public ResourceLocation skillName;

   public MessageLevelUp() {
   }

   public MessageLevelUp(ResourceLocation skillName) {
      this.skillName = skillName;
   }

   public void fromBytes(ByteBuf buf) {
      this.skillName = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.skillName.toString());
   }

   public IMessage onMessage(MessageLevelUp message, MessageContext ctx) {
      FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
         this.handleMessage(message, ctx);
      });
      return null;
   }

   public IMessage handleMessage(MessageLevelUp message, MessageContext context) {
      EntityPlayer player = context.getServerHandler().player;
      Skill skill = (Skill)ReskillableRegistries.SKILLS.getValue(message.skillName);
      PlayerData data = PlayerDataHandler.get(player);
      PlayerSkillInfo info = data.getSkillInfo(skill);
      if (!info.isCapped()) {
         int cost = info.getLevelUpCost();
         if (player.experienceLevel >= cost || player.isCreative()) {
            int oldLevel = info.getLevel();
            if (!MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Pre(player, skill, oldLevel + 1, oldLevel))) {
               if (!player.isCreative()) {
                  ExperienceHelper.drainPlayerXP(player, ExperienceHelper.getExperienceForLevel(cost));
               }

               info.levelUp();
               data.saveAndSync();
               MinecraftForge.EVENT_BUS.post(new LevelUpEvent.Post(player, skill, info.getLevel(), oldLevel));
            }
         }
      }

      return null;
   }
}
