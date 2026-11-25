package codersafterdark.reskillable.api;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.skill.SkillConfig;
import codersafterdark.reskillable.api.unlockable.UnlockableConfig;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public interface IModAccess {
   SkillConfig getSkillConfig(ResourceLocation var1);

   UnlockableConfig getUnlockableConfig(ResourceLocation var1, int var2, int var3, int var4, String[] var5);

   void syncPlayerData(EntityPlayer var1, PlayerData var2);

   AdvancementProgress getAdvancementProgress(EntityPlayer var1, Advancement var2);

   void log(Level var1, String var2);
}
