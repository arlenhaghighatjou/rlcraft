package xzeroair.trinkets.traits.abilities;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.handlers.TickHandler;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class Ability implements IAbilityInterface {
   private ResourceLocation regName;
   private String uuid;
   private String translationKey;
   private boolean removeAbility;
   protected Random random = Reference.random;
   protected TickHandler tickHandler = new TickHandler();
   protected boolean enabled;
   protected int value;
   protected AbilityHandler.AbilityHolder abilityHolder;

   public Ability() {
      this.setTranslationKey(this.getClass().getSimpleName());
      this.setRegistryName(this.getClass().getCanonicalName());
   }

   public Ability(String name) {
      this.setTranslationKey(name);
      this.setRegistryName(name);
   }

   public Ability(String modID, String name) {
      this.setTranslationKey(name);
      this.setRegistryName(modID, name);
   }

   public ResourceLocation getRegistryName() {
      return this.regName;
   }

   protected Ability setRegistryName(String name) {
      String[] astring = new String[]{"xat", name};
      int i = name.indexOf(58);
      if (i >= 0) {
         astring[1] = name.substring(i + 1, name.length());
         if (i > 1) {
            astring[0] = name.substring(0, i);
         }
      }

      String modID = astring[0] != null && astring.length != 0 ? astring[0].toLowerCase(Locale.ROOT) : "xat";
      String abilityname = astring[1].toLowerCase(Locale.ROOT);
      return this.setRegistryName(modID, abilityname);
   }

   protected Ability setRegistryName(ResourceLocation name) {
      if (this.regName != null) {
         throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + this.getRegistryName());
      } else {
         this.regName = name;
         return this;
      }
   }

   protected Ability setRegistryName(String modID, String name) {
      return this.setRegistryName(new ResourceLocation(modID, name));
   }

   public String getUUID() {
      return this.uuid;
   }

   protected Ability setUUID(String uuid) {
      this.uuid = uuid;
      return this;
   }

   protected Ability setTranslationKey(String translationKey) {
      this.translationKey = translationKey;
      return this;
   }

   public String getTranslationKey() {
      return "ability." + this.translationKey;
   }

   public String getDisplayName() {
      return (new TextComponentTranslation(this.getTranslationKey() + ".name", new Object[0])).getFormattedText();
   }

   public void getDescription(List<String> tooltips) {
      TranslationHelper helper = TranslationHelper.INSTANCE;

      for(int i = 1; i < 10; ++i) {
         String string = helper.getLangTranslation(this.getTranslationKey() + ".tooltip" + i);
         if (!helper.isStringEmpty(string)) {
            tooltips.add(string);
         }
      }

   }

   public boolean isCreativePlayer(Entity entity) {
      boolean flag = entity instanceof EntityPlayer && (((EntityPlayer)entity).isCreative() || ((EntityPlayer)entity).isSpectator());
      return flag;
   }

   public boolean isCreativeFlying(Entity entity) {
      boolean flag = entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying;
      return flag;
   }

   public boolean isSpectator(Entity entity) {
      boolean flag = entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator();
      return flag;
   }

   public boolean shouldRemove() {
      return this.removeAbility;
   }

   public Ability scheduleRemoval() {
      this.removeAbility = true;
      return this;
   }

   public Ability cacheAbilityHolder(AbilityHandler.AbilityHolder holder) {
      if (this.abilityHolder == null) {
         this.abilityHolder = holder;
      }

      return this;
   }

   public AbilityHandler.AbilityHolder getAbilityHolder() {
      return this.abilityHolder;
   }
}
