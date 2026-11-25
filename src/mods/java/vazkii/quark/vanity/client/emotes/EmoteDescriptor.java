package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EmoteDescriptor {
   public static final ResourceLocation TIER_1 = new ResourceLocation("quark", "textures/emotes/patreon_t1.png");
   public static final ResourceLocation TIER_2 = new ResourceLocation("quark", "textures/emotes/patreon_t2.png");
   public static final ResourceLocation TIER_3 = new ResourceLocation("quark", "textures/emotes/patreon_t3.png");
   public static final ResourceLocation TIER_4 = new ResourceLocation("quark", "textures/emotes/patreon_t4.png");
   public static final ResourceLocation TIER_GOD = new ResourceLocation("quark", "textures/emotes/patreon_t99.png");
   public final Class<? extends EmoteBase> clazz;
   public final int index;
   public final String name;
   public final String regName;
   public final ResourceLocation texture;
   public final EmoteTemplate template;
   private int tier;

   public EmoteDescriptor(Class<? extends EmoteBase> clazz, String name, String regName, int index) {
      this(clazz, name, regName, index, new ResourceLocation("quark", "textures/emotes/" + name + ".png"), new EmoteTemplate(name + ".emote"));
   }

   public EmoteDescriptor(Class<? extends EmoteBase> clazz, String name, String regName, int index, ResourceLocation texture, EmoteTemplate template) {
      this.clazz = clazz;
      this.index = index;
      this.name = name;
      this.regName = regName;
      this.texture = texture;
      this.template = template;
      this.tier = template.tier;
   }

   public void updateTier(EmoteTemplate template) {
      this.tier = template.tier;
   }

   public String getTranslationKey() {
      return "quark.emote." + this.name;
   }

   @SideOnly(Side.CLIENT)
   public String getLocalizedName() {
      return I18n.format(this.getTranslationKey(), new Object[0]);
   }

   public String getRegistryName() {
      return this.regName;
   }

   public int getTier() {
      return this.tier;
   }

   public ResourceLocation getTierTexture() {
      if (this.tier >= 99) {
         return TIER_GOD;
      } else if (this.tier >= 4) {
         return TIER_4;
      } else if (this.tier >= 3) {
         return TIER_3;
      } else if (this.tier >= 2) {
         return TIER_2;
      } else {
         return this.tier >= 1 ? TIER_1 : null;
      }
   }

   public EmoteBase instantiate(EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegModel) {
      try {
         return (EmoteBase)this.clazz.getConstructor(EmoteDescriptor.class, EntityPlayer.class, ModelBiped.class, ModelBiped.class, ModelBiped.class).newInstance(this, player, model, armorModel, armorLegModel);
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }
}
