package vazkii.quark.vanity.client.emotes;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.vanity.feature.EmoteSystem;

@SideOnly(Side.CLIENT)
public class CustomEmoteDescriptor extends EmoteDescriptor {
   public CustomEmoteDescriptor(String name, String regName, int index) {
      super(CustomEmote.class, name, regName, index, getSprite(name), new CustomEmoteTemplate(name));
   }

   public static ResourceLocation getSprite(String name) {
      ResourceLocation customRes = new ResourceLocation("quark_custom", name);
      return EmoteSystem.resourcePack.hasResourceName(customRes.toString()) ? customRes : new ResourceLocation("quark", "textures/emotes/custom.png");
   }

   public String getTranslationKey() {
      return ((CustomEmoteTemplate)this.template).getName();
   }

   public String getLocalizedName() {
      return this.getTranslationKey();
   }
}
