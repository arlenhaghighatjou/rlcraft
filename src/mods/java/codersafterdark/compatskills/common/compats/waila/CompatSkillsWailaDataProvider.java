package codersafterdark.compatskills.common.compats.waila;

import codersafterdark.compatskills.common.compats.minecraft.entity.entitydamageevent.EntityDamageKey;
import codersafterdark.compatskills.utils.CompatSkillsConfig;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;

public class CompatSkillsWailaDataProvider implements IWailaDataProvider, IWailaEntityProvider {
   public CompatSkillsWailaDataProvider() {
   }

   @Nonnull
   public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
      RequirementHolder holder = LevelLockHandler.getSkillLock(itemStack);
      this.addInfoToTooltips(accessor.getPlayer(), holder, config, currenttip);
      return currenttip;
   }

   @Nonnull
   public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
      RequirementHolder holder = LevelLockHandler.getLockByKey(new EntityDamageKey(entity));
      this.addInfoToTooltips(accessor.getPlayer(), holder, config, currenttip);
      return currenttip;
   }

   private void addInfoToTooltips(EntityPlayer player, RequirementHolder holder, IWailaConfigHandler config, List<String> currenttip) {
      if (config.getConfig("compatskills.requirements")) {
         if (CompatSkillsConfig.Configs.Hwyla.HwylaShifting) {
            if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
               if (holder.isRealLock()) {
                  TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.shift", new Object[0]);
                  currenttip.add(error.getUnformattedComponentText());
               }
            } else if (holder.isRealLock()) {
               PlayerData playerData = PlayerDataHandler.get(player);
               TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
               currenttip.add(error.getUnformattedComponentText());
               holder.getRequirements().stream().map((req) -> {
                  return (void)req.getToolTip(playerData);
               }).forEach(currenttip::add);
            }
         } else if (holder.isRealLock()) {
            PlayerData playerData = PlayerDataHandler.get(player);
            TextComponentTranslation error = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
            currenttip.add(error.getUnformattedComponentText());
            holder.getRequirements().stream().map((req) -> {
               return (void)req.getToolTip(playerData);
            }).forEach(currenttip::add);
         }
      }

   }
}
