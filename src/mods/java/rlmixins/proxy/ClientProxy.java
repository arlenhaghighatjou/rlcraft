package rlmixins.proxy;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import rlmixins.entity.flare.EntityFlareNonAlbedo;
import rlmixins.entity.flare.MovingSoundFlare;
import rlmixins.handlers.ForgeConfigHandler;
import rlmixins.handlers.ModRegistry;
import rlmixins.models.ModelLivingArmor;
import rlmixins.models.ModelScarliteArmor;
import rlmixins.models.ModelSentientArmor;
import rlmixins.models.ModelSteelArmor;
import rlmixins.wrapper.ParasitesWrapper;

public class ClientProxy extends CommonProxy {
   private static final ModelLivingArmor livingArmor = new ModelLivingArmor(1.0F);
   private static final ModelLivingArmor livingArmorLegs = new ModelLivingArmor(0.5F);
   private static final ModelSentientArmor sentientArmor = new ModelSentientArmor(1.0F);
   private static final ModelSentientArmor sentientArmorLegs = new ModelSentientArmor(0.5F);
   private static final ModelSteelArmor steelArmor = new ModelSteelArmor(1.0F);
   private static final ModelSteelArmor steelArmorLegs = new ModelSteelArmor(0.5F);
   private static final ModelScarliteArmor scarliteArmor = new ModelScarliteArmor(1.0F);
   private static final ModelScarliteArmor scarliteArmorLegs = new ModelScarliteArmor(0.5F);
   public static final Map<Item, ModelBiped> livingArmorModels = new HashMap();
   public static final Map<Item, ModelBiped> sentientArmorModels = new HashMap();
   public static final Map<Item, ModelBiped> steelArmorModels = new HashMap();
   public static final Map<Item, ModelBiped> scarliteArmorModels = new HashMap();

   public ClientProxy() {
   }

   public void preInit() {
      if (ForgeConfigHandler.server.registerSteelArmor) {
         steelArmorModels.put(ModRegistry.steelHelmet, steelArmor);
         steelArmorModels.put(ModRegistry.steelChestplate, steelArmor);
         steelArmorModels.put(ModRegistry.steelLeggings, steelArmorLegs);
         steelArmorModels.put(ModRegistry.steelBoots, steelArmor);
      }

      if (ForgeConfigHandler.server.registerScarliteArmor) {
         scarliteArmorModels.put(ModRegistry.scarliteHelmet, scarliteArmor);
         scarliteArmorModels.put(ModRegistry.scarliteChestplate, scarliteArmor);
         scarliteArmorModels.put(ModRegistry.scarliteLeggings, scarliteArmorLegs);
         scarliteArmorModels.put(ModRegistry.scarliteBoots, scarliteArmor);
      }

   }

   public void initParasiteModels() {
      livingArmorModels.put(ParasitesWrapper.getLivingHelmet(), livingArmor);
      livingArmorModels.put(ParasitesWrapper.getLivingChestplate(), livingArmor);
      livingArmorModels.put(ParasitesWrapper.getLivingLeggings(), livingArmorLegs);
      livingArmorModels.put(ParasitesWrapper.getLivingBoots(), livingArmor);
      sentientArmorModels.put(ParasitesWrapper.getSentientHelmet(), sentientArmor);
      sentientArmorModels.put(ParasitesWrapper.getSentientChestplate(), sentientArmor);
      sentientArmorModels.put(ParasitesWrapper.getSentientLeggings(), sentientArmorLegs);
      sentientArmorModels.put(ParasitesWrapper.getSentientBoots(), sentientArmor);
   }

   public void playSoundFlare(EntityFlareNonAlbedo flare) {
      MovingSoundFlare soundFlare = new MovingSoundFlare(flare);
      Minecraft.getMinecraft().getSoundHandler().playSound(soundFlare);
   }

   public Map<Item, ModelBiped> getLivingArmor() {
      if (livingArmorModels.isEmpty()) {
         this.initParasiteModels();
      }

      return livingArmorModels;
   }

   public Map<Item, ModelBiped> getSentientArmor() {
      if (sentientArmorModels.isEmpty()) {
         this.initParasiteModels();
      }

      return sentientArmorModels;
   }

   public Map<Item, ModelBiped> getSteelArmor() {
      return steelArmorModels;
   }

   public Map<Item, ModelBiped> getScarliteArmor() {
      return scarliteArmorModels;
   }
}
