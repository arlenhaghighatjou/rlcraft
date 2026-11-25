package codersafterdark.compatskills.common.compats.theoneprobe;

import codersafterdark.compatskills.common.compats.minecraft.entity.entitydamageevent.EntityDamageKey;
import codersafterdark.compatskills.utils.CompatSkillsConfig;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class CompatSkillsTOPSupport implements Function<ITheOneProbe, Void> {
   public static ITheOneProbe probe;

   public CompatSkillsTOPSupport() {
   }

   @Nullable
   public Void apply(ITheOneProbe iTheOneProbe) {
      probe = iTheOneProbe;
      probe.registerProvider(new IProbeInfoProvider() {
         public String getID() {
            return "compatskills-block";
         }

         public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
            ItemStack stack = data.getPickBlock();
            RequirementHolder holder = LevelLockHandler.getSkillLock(stack);
            CompatSkillsTOPSupport.this.addInfoToProbe(probeInfo, player, holder);
         }
      });
      probe.registerEntityProvider(new IProbeInfoEntityProvider() {
         public String getID() {
            return "compatskills-entity";
         }

         public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
            RequirementHolder holder = LevelLockHandler.getLockByKey(new EntityDamageKey(entity));
            CompatSkillsTOPSupport.this.addInfoToProbe(probeInfo, player, holder);
         }
      });
      return null;
   }

   private void addInfoToProbe(IProbeInfo probeInfo, EntityPlayer player, RequirementHolder holder) {
      if (CompatSkillsConfig.Configs.TOP.TOPShifting) {
         if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
            if (holder.isRealLock()) {
               TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.Hwyla.Shift", new Object[0]);
               probeInfo.text(error.getUnformattedComponentText());
            }
         } else if (holder.isRealLock()) {
            PlayerData playerData = PlayerDataHandler.get(player);
            TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
            probeInfo.text(TextFormatting.GRAY + error.getUnformattedComponentText());
            holder.getRequirements().stream().map((req) -> {
               return req.getToolTip(playerData);
            }).forEach(probeInfo::text);
         }
      } else if (holder.isRealLock()) {
         PlayerData playerData = PlayerDataHandler.get(player);
         TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
         probeInfo.text(TextFormatting.GRAY + error.getUnformattedComponentText());
         holder.getRequirements().stream().map((req) -> {
            return req.getToolTip(playerData);
         }).forEach(probeInfo::text);
      }

   }
}
