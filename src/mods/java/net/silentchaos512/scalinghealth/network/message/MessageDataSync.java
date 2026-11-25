package net.silentchaos512.scalinghealth.network.message;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.network.Message;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class MessageDataSync extends Message {
   public NBTTagCompound tags;
   public String playerName;
   public int experienceLevel;

   public MessageDataSync() {
   }

   public MessageDataSync(SHPlayerDataHandler.PlayerData data, EntityPlayer player) {
      this.tags = new NBTTagCompound();
      data.writeToNBT(this.tags);
      this.playerName = player.getName();
      this.experienceLevel = player.experienceLevel;
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public IMessage handleMessage(MessageContext context) {
      ClientTicks.scheduleAction(() -> {
         EntityPlayer player = getPlayerByName(this.playerName);
         if (player != null) {
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
            if (data != null) {
               data.readFromNBT(this.tags);
               if (Config.Player.Health.allowModify) {
                  ModifierHandler.setMaxHealth(player, (double)data.getMaxHealth(), 0);
                  if (data.getHealth() > 0.0F) {
                     player.setHealth(data.getHealth());
                  }
               }
            }

            player.experienceLevel = this.experienceLevel;
         }

      });
      return null;
   }

   @Nullable
   private static EntityPlayer getPlayerByName(String name) {
      EntityPlayer localPlayer = ScalingHealth.proxy.getClientPlayer();
      if (localPlayer != null) {
         World world = localPlayer.world;
         if (world != null) {
            return world.getPlayerEntityByName(name);
         }
      }

      return null;
   }
}
