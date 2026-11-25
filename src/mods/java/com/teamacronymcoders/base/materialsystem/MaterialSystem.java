package com.teamacronymcoders.base.materialsystem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.creativetabs.CreativeTabCarousel;
import com.teamacronymcoders.base.materialsystem.capabilities.MaterialPartCapability;
import com.teamacronymcoders.base.materialsystem.compat.MaterialCompatLoader;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.materialparts.MissingMaterialPart;
import com.teamacronymcoders.base.materialsystem.materials.Material;
import com.teamacronymcoders.base.materialsystem.materials.MaterialBuilder;
import com.teamacronymcoders.base.materialsystem.parts.GatherPartsEvent;
import com.teamacronymcoders.base.materialsystem.parts.Part;
import com.teamacronymcoders.base.materialsystem.parts.PartBuilder;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.util.TextUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

public class MaterialSystem {
   public static MissingMaterialPart MISSING_MATERIAL_PART;
   public static CreativeTabCarousel materialCreativeTab;
   private static Map<String, Part> partMap = Maps.newHashMap();
   private static Map<String, Material> materialMap = Maps.newHashMap();
   private static Map<String, PartType> partTypeMap = Maps.newHashMap();
   private static Map<String, MaterialUser> users = Maps.newHashMap();
   private static Map<String, MaterialPart> materialPartMap = Maps.newHashMap();
   public static List<MaterialBuilder> materialsNotBuilt = Lists.newArrayList();
   public static List<PartBuilder> partsNotBuilt = Lists.newArrayList();
   private static boolean isSetup = false;

   public MaterialSystem() {
   }

   public static void setup(MaterialUser user, ASMDataTable dataTable) {
      if (!isSetup) {
         MaterialCompatLoader materialCompatLoader = new MaterialCompatLoader();
         materialCompatLoader.loadCompat(dataTable);
         materialCreativeTab = new CreativeTabCarousel("materials.base");
         ConfigRegistry configRegistry = (ConfigRegistry)Base.instance.getRegistry(ConfigRegistry.class, "CONFIG");
         configRegistry.addNewConfigFile(configRegistry.getTacFolder(), "material_system");

         try {
            MISSING_MATERIAL_PART = new MissingMaterialPart();
         } catch (MaterialException var5) {
            Base.instance.getLogger().fatal("Failed to Create Missing Material Part, THIS IS BAD");
         }

         materialCompatLoader.doCompat();
         GatherPartsEvent gatherPartsEvent = new GatherPartsEvent();
         MinecraftForge.EVENT_BUS.post(gatherPartsEvent);
         gatherPartsEvent.getPartList().forEach(MaterialSystem::registerPart);
         gatherPartsEvent.getPartTypeList().forEach(MaterialSystem::registerPartType);
         MaterialPartCapability.register();
         isSetup = true;
      }

      users.put(user.getId(), user);
   }

   public static void registerPart(Part part) {
      partMap.put(TextUtils.toSnakeCase(part.getName()), part);
   }

   public static void registerPartType(PartType partType) {
      partTypeMap.put(partType.getName().toLowerCase(Locale.US), partType);
   }

   public static void registerMaterial(Material material) {
      materialMap.put(material.getName(), material);
   }

   public static Part getPart(String name) {
      return (Part)partMap.get(name.toLowerCase(Locale.US));
   }

   public static PartType getPartType(String name) {
      return (PartType)partTypeMap.get(name.toLowerCase(Locale.US));
   }

   public static Material getMaterial(String name) {
      return (Material)materialMap.get(name);
   }

   public static MaterialUser getUser(String name) {
      return (MaterialUser)users.get(name);
   }

   public static void registerMaterialPart(MaterialPart materialPart) {
      if (!materialPartMap.containsKey(materialPart.getUnlocalizedName())) {
         materialPartMap.put(materialPart.getUnlocalizedName(), materialPart);
      }

   }

   public static MaterialPart getMaterialPart(String name) {
      return (MaterialPart)Optional.ofNullable(materialPartMap.get(name.toLowerCase(Locale.US))).orElse(MISSING_MATERIAL_PART);
   }

   public static void removeMaterialPart(String name) {
      materialPartMap.remove(name);
   }

   public static void removeMaterialPart(MaterialPart materialPart) {
      materialPartMap.remove(materialPart.getUnlocalizedName());
   }

   public static boolean hasMaterialPart(MaterialPart materialPart) {
      return materialPartMap.containsKey(materialPart.getUnlocalizedName());
   }

   public static Map<String, MaterialPart> getMaterialParts() {
      return ImmutableMap.copyOf(materialPartMap);
   }

   public static Map<String, Part> getParts() {
      return ImmutableMap.copyOf(partMap);
   }

   public static Map<String, PartType> getPartTypes() {
      return ImmutableMap.copyOf(partTypeMap);
   }

   public static Map<String, Material> getMaterials() {
      return ImmutableMap.copyOf(materialMap);
   }
}
