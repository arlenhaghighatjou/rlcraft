package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.ModuleLoader;

@SideOnly(Side.CLIENT)
public class TemplateSourcedEmote extends EmoteBase {
   public TemplateSourcedEmote(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
      super(desc, player, model, armorModel, armorLegsModel);
      if (this.shouldLoadTimelineOnLaunch()) {
         Quark.LOG.debug("Loading emote " + desc.getTranslationKey());
         desc.template.readAndMakeTimeline(desc, player, model);
      }

   }

   boolean shouldLoadTimelineOnLaunch() {
      return ModuleLoader.DEBUG_MODE;
   }

   public Timeline getTimeline(EntityPlayer player, ModelBiped model) {
      return this.desc.template.getTimeline(this.desc, player, model);
   }

   public boolean usesBodyPart(int part) {
      return this.desc.template.usesBodyPart(part);
   }
}
