package c4.corpsecomplex.common.modules.compatibility.reskillable;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.event.LevelUpEvent.Post;
import codersafterdark.reskillable.api.event.LevelUpEvent.Pre;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class ReskillableModule extends Submodule {
   static boolean resetSkills;

   public ReskillableModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      resetSkills = this.getBool("Reset Skills on Respawn", false, "Set to true to reset Reskillable skills on respawn", false);
   }

   public boolean hasEvents() {
      return true;
   }

   @SubscribeEvent
   public void onPlayerRespawn(PlayerRespawnEvent evt) {
      if (resetSkills && !evt.isEndConquered()) {
         EntityPlayerMP player = (EntityPlayerMP)evt.player;
         PlayerData data = PlayerDataHandler.get(player);

         for(PlayerSkillInfo skillInfo : data.getAllSkillInfo()) {
            int oldLevel = skillInfo.getLevel();
            if (!MinecraftForge.EVENT_BUS.post(new Pre(player, skillInfo.skill, 1, oldLevel))) {
               skillInfo.setLevel(1);
               skillInfo.respec();
               MinecraftForge.EVENT_BUS.post(new Post(player, skillInfo.skill, 1, oldLevel));
            }
         }

         data.saveAndSync();
      }

   }
}
