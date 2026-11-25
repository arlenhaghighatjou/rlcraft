package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ChargeItem;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEquipmentPart extends ItemBase {
   public static int BASE_LEVELUP_EXPERIENCE = 500;
   public static ItemStack ITEMSTACK_TO_RENDER;
   public static Map<String, List<ItemEquipmentPart>> MOB_PART_DROPS = new HashMap();
   public List<EquipmentFeature> features = new ArrayList();
   public List<ElementInfo> elements = new ArrayList();
   public String slotType;
   public String dropMobId;
   public float dropChance = 1.0F;
   public int levelMin = 1;
   public int levelMax = 3;

   public ItemEquipmentPart(ModInfo groupInfo) {
      this.modInfo = groupInfo;
      this.setMaxStackSize(1);
      this.setCreativeTab(LycanitesMobs.equipmentPartsTab);
   }

   public void loadFromJSON(JsonObject json) {
      this.itemName = json.get("itemName").getAsString();
      this.slotType = json.get("slotType").getAsString();
      if (json.has("dropMobId")) {
         this.dropMobId = json.get("dropMobId").getAsString();
         if (!"".equals(this.dropMobId)) {
            if (!MOB_PART_DROPS.containsKey(this.dropMobId)) {
               MOB_PART_DROPS.put(this.dropMobId, new ArrayList());
            }

            ((List)MOB_PART_DROPS.get(this.dropMobId)).add(this);
         }
      }

      if (json.has("dropChance")) {
         this.dropChance = json.get("dropChance").getAsFloat();
      }

      if (json.has("levelMin")) {
         this.levelMin = json.get("levelMin").getAsInt();
      }

      if (json.has("levelMax")) {
         this.levelMax = json.get("levelMax").getAsInt();
      }

      List<String> elementNames = new ArrayList();
      if (json.has("elements")) {
         elementNames = JSONHelper.getJsonStrings(json.get("elements").getAsJsonArray());
      }

      this.elements.clear();

      for(String elementName : elementNames) {
         ElementInfo element = ElementManager.getInstance().getElement(elementName);
         if (element == null) {
            throw new RuntimeException("[Equipment] Unable to initialise Equipment Part " + this.getUnlocalizedName() + " as the element " + elementName + " cannot be found.");
         }

         this.elements.add(element);
      }

      if (json.has("features")) {
         JsonArray jsonArray = json.get("features").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject featureJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            if (!this.addFeature(EquipmentFeature.createFromJSON(featureJson))) {
               LycanitesMobs.logWarning("", "[Equipment] The feature " + featureJson.toString() + " was unable to be added, check the JSON format.");
            }
         }
      }

      this.sortFeatures();
      this.setRegistryName(this.modInfo.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
      AssetManager.addTexture(this.itemName, this.modInfo, "textures/equipment/" + this.itemName + ".png");
   }

   public String getItemStackDisplayName(ItemStack itemStack) {
      String displayName = LanguageManager.translate(this.getUnlocalizedName(itemStack) + ".name");
      displayName = displayName + " " + LanguageManager.translate("equipment.level") + " " + this.getLevel(itemStack);
      return displayName;
   }

   public void addInformation(ItemStack itemStack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
      super.addInformation(itemStack, world, tooltip, tooltipFlag);
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

      for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth(description, DESCRIPTION_WIDTH)) {
            if (formattedDescription instanceof String) {
               tooltip.add("§3" + formattedDescription);
            }
         }
      }

   }

   public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      return LanguageManager.translate("item.equipmentpart.description");
   }

   public List<String> getAdditionalDescriptions(ItemStack itemStack, World world, ITooltipFlag tooltipFlag) {
      List<String> descriptions = new ArrayList();
      int level = this.getLevel(itemStack);
      int experience = this.getExperience(itemStack);
      int experienceMax = this.getExperienceForNextLevel(itemStack);
      String baseFeature = LanguageManager.translate("equipment.slottype") + " " + LanguageManager.translate("equipment.slot." + this.slotType);
      baseFeature = baseFeature + "\n" + LanguageManager.translate("equipment.level") + " " + level + "/" + this.levelMax;
      if (level < this.levelMax) {
         baseFeature = baseFeature + "\n" + LanguageManager.translate("entity.experience") + ": " + experience + "/" + experienceMax;
      }

      descriptions.add("-------------------\n");
      descriptions.add(baseFeature);
      if (!this.elements.isEmpty()) {
         String elementFeature = LanguageManager.translate("equipment.element") + " " + this.getElementNames();
         descriptions.add(elementFeature);
      }

      descriptions.add("-------------------\n");

      for(EquipmentFeature feature : this.features) {
         String featureDescription = feature.getDescription(itemStack, level);
         if (featureDescription != null && !"".equals(featureDescription)) {
            descriptions.add(featureDescription);
         }
      }

      return descriptions;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      return super.getAttributeModifiers(slot, stack);
   }

   public int getMetadata(ItemStack stack) {
      ITEMSTACK_TO_RENDER = stack;
      return super.getMetadata(stack);
   }

   @SideOnly(Side.CLIENT)
   public FontRenderer getFontRenderer(ItemStack stack) {
      return LycanitesMobs.proxy.getFontRenderer();
   }

   public void randomizeLevel(World world, ItemStack itemStack) {
      int level = this.levelMax;
      if (this.levelMin < this.levelMax) {
         level = this.levelMin + world.rand.nextInt(this.levelMax - this.levelMin + 1);
      }

      this.setLevel(itemStack, level);
   }

   public boolean addFeature(EquipmentFeature feature) {
      if (feature == null) {
         LycanitesMobs.logWarning("", "[Equipment] Unable to add a null feature to " + this);
         return false;
      } else if (feature.featureType == null) {
         LycanitesMobs.logWarning("", "[Equipment] Feature type not set for part " + this);
         return false;
      } else {
         this.features.add(feature);
         return true;
      }
   }

   public void sortFeatures() {
      Comparator<EquipmentFeature> comparator = (o1, o2) -> {
         return o1.featureType.compareToIgnoreCase(o2.featureType);
      };
      this.features.sort(comparator);
   }

   public void setLevel(ItemStack itemStack, int level) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      nbt.setInteger("equipmentLevel", level);
      itemStack.setTagCompound(nbt);
   }

   public int getLevel(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      int level = 1;
      if (nbt.hasKey("equipmentLevel")) {
         level = nbt.getInteger("equipmentLevel");
      }

      return level;
   }

   public void setExperience(ItemStack itemStack, int experience) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      nbt.setInteger("equipmentExperience", experience);
      itemStack.setTagCompound(nbt);
   }

   public void addExperience(ItemStack itemStack, int experience) {
      int currentLevel = this.getLevel(itemStack);
      if (currentLevel >= this.levelMax) {
         this.setExperience(itemStack, 0);
      }

      int increasedExperience = this.getExperience(itemStack) + experience;
      int nextLevelExperience = this.getExperienceForNextLevel(itemStack);
      if (increasedExperience >= nextLevelExperience) {
         increasedExperience -= nextLevelExperience;
         this.setLevel(itemStack, currentLevel + 1);
      }

      this.setExperience(itemStack, increasedExperience);
   }

   public int getExperience(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      int experience = 0;
      if (nbt.hasKey("equipmentExperience")) {
         experience = nbt.getInteger("equipmentExperience");
      }

      return experience;
   }

   public int getExperienceForNextLevel(ItemStack itemStack) {
      return BASE_LEVELUP_EXPERIENCE + Math.round((float)(BASE_LEVELUP_EXPERIENCE * (this.getLevel(itemStack) - 1)) * 0.25F);
   }

   public boolean isLevelingChargeItem(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ChargeItem) {
         ChargeItem chargeItem = (ChargeItem)itemStack.getItem();

         for(ElementInfo elementInfo : this.elements) {
            if (chargeItem.getElements().contains(elementInfo)) {
               return true;
            }
         }
      }

      return false;
   }

   public int getExperienceFromChargeItem(ItemStack itemStack) {
      int experience = 0;
      if (itemStack.getItem() instanceof ChargeItem) {
         ChargeItem chargeItem = (ChargeItem)itemStack.getItem();

         for(ElementInfo elementInfo : this.elements) {
            if (chargeItem.getElements().contains(elementInfo)) {
               experience += ChargeItem.CHARGE_EXPERIENCE;
            }
         }
      }

      return experience;
   }

   public int getSharpness(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      int sharpness = ItemEquipment.SHARPNESS_MAX;
      if (nbt.hasKey("equipmentSharpness")) {
         sharpness = nbt.getInteger("equipmentSharpness");
      }

      return sharpness;
   }

   public void setSharpness(ItemStack itemStack, int sharpness) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      nbt.setInteger("equipmentSharpness", Math.max(Math.min(sharpness, ItemEquipment.SHARPNESS_MAX), 0));
      itemStack.setTagCompound(nbt);
   }

   public boolean addSharpness(ItemStack itemStack, int sharpness) {
      int currentSharpness = this.getSharpness(itemStack);
      if (currentSharpness >= ItemEquipment.SHARPNESS_MAX) {
         return false;
      } else {
         this.setSharpness(itemStack, this.getSharpness(itemStack) + sharpness);
         return true;
      }
   }

   public boolean removeSharpness(ItemStack itemStack, int sharpness) {
      int currentSharpness = this.getSharpness(itemStack);
      if (currentSharpness <= 0) {
         return false;
      } else {
         this.setSharpness(itemStack, this.getSharpness(itemStack) - sharpness);
         return true;
      }
   }

   public int getMana(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      int mana = ItemEquipment.MANA_MAX;
      if (nbt.hasKey("equipmentMana")) {
         mana = nbt.getInteger("equipmentMana");
      }

      return mana;
   }

   public void setMana(ItemStack itemStack, int mana) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      nbt.setInteger("equipmentMana", Math.max(Math.min(mana, ItemEquipment.MANA_MAX), 0));
      itemStack.setTagCompound(nbt);
   }

   public boolean addMana(ItemStack itemStack, int mana) {
      int currentMana = this.getMana(itemStack);
      if (currentMana >= ItemEquipment.MANA_MAX) {
         return false;
      } else {
         this.setMana(itemStack, this.getMana(itemStack) + mana);
         return true;
      }
   }

   public boolean removeMana(ItemStack itemStack, int mana) {
      int currentMana = this.getMana(itemStack);
      if (currentMana <= 0) {
         return false;
      } else {
         this.setMana(itemStack, this.getMana(itemStack) - mana);
         return true;
      }
   }

   public Vector4f getColor(ItemStack itemStack) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      float r = 1.0F;
      float g = 1.0F;
      float b = 1.0F;
      if (nbt.hasKey("equipmentColorR")) {
         r = nbt.getFloat("equipmentColorR");
      }

      if (nbt.hasKey("equipmentColorG")) {
         g = nbt.getFloat("equipmentColorG");
      }

      if (nbt.hasKey("equipmentColorB")) {
         b = nbt.getFloat("equipmentColorB");
      }

      return new Vector4f(r, g, b, 1.0F);
   }

   public void setColor(ItemStack itemStack, float red, float green, float blue) {
      NBTTagCompound nbt = this.getTagCompound(itemStack);
      nbt.setFloat("equipmentColorR", red);
      nbt.setFloat("equipmentColorG", green);
      nbt.setFloat("equipmentColorB", blue);
      itemStack.setTagCompound(nbt);
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(int level = 1; level <= this.levelMax; ++level) {
            ItemStack itemStack = new ItemStack(this, 1);
            this.setLevel(itemStack, level);
            items.add(itemStack);
         }

      }
   }

   public boolean hasElement(ElementInfo element) {
      return this.elements.contains(element);
   }

   public String getElementNames() {
      String elementNames = "";
      boolean firstElement = true;

      for(ElementInfo element : this.elements) {
         if (!firstElement) {
            elementNames = elementNames + ", ";
         }

         firstElement = false;
         elementNames = elementNames + element.getTitle();
      }

      return elementNames;
   }

   public ModelResourceLocation getModelResourceLocation() {
      return new ModelResourceLocation(new ResourceLocation("lycanitesmobs", "equipmentpart"), "inventory");
   }

   public ResourceLocation getTexture(ItemStack itemStack, String suffix) {
      if (AssetManager.getTexture(this.itemName + suffix) == null) {
         AssetManager.addTexture(this.itemName + suffix, this.modInfo, "textures/equipment/" + this.itemName.toLowerCase() + suffix + ".png");
      }

      return AssetManager.getTexture(this.itemName + suffix);
   }
}
