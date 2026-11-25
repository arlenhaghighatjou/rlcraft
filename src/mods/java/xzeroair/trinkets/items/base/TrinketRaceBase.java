package xzeroair.trinkets.items.base;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.races.IRaceProvider;
import xzeroair.trinkets.races.dwarf.config.DwarfConfig;
import xzeroair.trinkets.races.elf.config.ElfConfig;
import xzeroair.trinkets.races.fairy.config.FairyConfig;
import xzeroair.trinkets.races.titan.config.TitanConfig;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.shared.TransformationRingConfig;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class TrinketRaceBase extends AccessoryBase implements IRaceProvider {
   public TransformationRingConfig serverConfig;
   protected EntityRace race;

   public TrinketRaceBase(String name, EntityRace race, TransformationRingConfig config) {
      super(name);
      this.race = race;
      this.setUUID("892cfd1f-25c5-44a0-9154-f3b630538c82");
      this.serverConfig = config;
   }

   public String[] getAttributeConfig() {
      return this.race.getRaceAttributes().getAttributes();
   }

   public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {
   }

   protected void initAttributes(String[] attributeConfig, EntityLivingBase entity) {
   }

   protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
      TranslationHelper helper = TranslationHelper.INSTANCE;
      EntityRace r = this.getRace();
      if (r != null) {
         TranslationHelper.KeyEntry size = new TranslationHelper.OptionEntry("rsize", r.getRaceHeight() + "%");
         if (r.equals(EntityRaces.fairy)) {
            FairyConfig config = TrinketsConfig.SERVER.races.fairy;
            TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "creativeflight", config.creative_flight);
            return helper.formatAddVariables(translation, size, key1);
         } else if (r.equals(EntityRaces.dwarf)) {
            DwarfConfig config = TrinketsConfig.SERVER.races.dwarf;
            TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "fortune", config.fortune);
            TranslationHelper.KeyEntry key2 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "skilledminer", config.skilled_miner);
            TranslationHelper.KeyEntry key3 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "staticminer", config.static_mining);
            return helper.formatAddVariables(translation, size, key1, key2, key3);
         } else if (r.equals(EntityRaces.elf)) {
            ElfConfig config = TrinketsConfig.SERVER.races.elf;
            TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "chargeshot", config.charge_shot);
            return helper.formatAddVariables(translation, size, key1);
         } else if (r.equals(EntityRaces.dragon)) {
            TranslationHelper.KeyEntry key1 = new TranslationHelper.KeyBindEntry("denvkb", ModKeyBindings.DRAGONS_EYE_ABILITY.getDisplayName());
            TranslationHelper.KeyEntry key2 = new TranslationHelper.KeyBindEntry("breathkb", ModKeyBindings.RACE_ABILITY.getDisplayName());
            return helper.formatAddVariables(translation, size, key1, key2);
         } else if (r.equals(EntityRaces.titan)) {
            TitanConfig config = TrinketsConfig.SERVER.races.titan;
            TranslationHelper.KeyEntry key1 = new TranslationHelper.LangEntry(this.getUnlocalizedName(stack), "heavy", config.sink);
            return helper.formatAddVariables(translation, size, key1);
         } else {
            return helper.formatAddVariables(translation, size);
         }
      } else {
         return helper.formatAddVariables(translation);
      }
   }

   public EntityRace getRace() {
      return this.race;
   }

   public boolean ItemEnabled() {
      return this.serverConfig.enabled;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }
}
