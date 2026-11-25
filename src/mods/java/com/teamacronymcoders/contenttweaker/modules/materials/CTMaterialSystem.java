package com.teamacronymcoders.contenttweaker.modules.materials;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.materials.Material;
import com.teamacronymcoders.base.materialsystem.parts.Part;
import com.teamacronymcoders.base.materialsystem.parttype.PartDataPiece;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.materials.functions.IMaterialPartItemStackSupplier;
import com.teamacronymcoders.contenttweaker.modules.materials.functions.IRegisterMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.CTPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.CTMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.CTMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.CTMaterialBuilder;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.IMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.IMaterialBuilder;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.CTPart;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.CTPartBuilder;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.IPart;
import com.teamacronymcoders.contenttweaker.modules.materials.parts.IPartBuilder;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.CTCreatedPartType;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.CTPartType;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.IPartType;
import crafttweaker.annotations.ZenRegister;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.MaterialSystem")
public class CTMaterialSystem {
   public CTMaterialSystem() {
   }

   @ZenMethod
   public static IPartType createPartType(String name, IRegisterMaterialPart registerMaterialPart) {
      return new CTCreatedPartType(name, registerMaterialPart, (materialPart) -> {
         return null;
      });
   }

   @ZenMethod
   public static IPartType createPartType(String name, IRegisterMaterialPart registerMaterialPart, IMaterialPartItemStackSupplier supplier) {
      return new CTCreatedPartType(name, registerMaterialPart, supplier);
   }

   @ZenMethod
   public static IPartDataPiece createPartDataPiece(String name, boolean required) {
      return new CTPartDataPiece(new PartDataPiece(name, required));
   }

   @ZenMethod
   public static IPartType getPartType(String name) {
      return new CTPartType(MaterialSystem.getPartType(name));
   }

   @ZenMethod
   public static IMaterialBuilder getMaterialBuilder() {
      return new CTMaterialBuilder();
   }

   @ZenMethod
   public static IPartBuilder getPartBuilder() {
      return new CTPartBuilder();
   }

   @ZenMethod
   public static IPart getPart(String name) {
      return new CTPart(MaterialSystem.getPart(name));
   }

   @ZenMethod
   public static IMaterial getMaterial(String name) {
      return new CTMaterial(MaterialSystem.getMaterial(name));
   }

   @ZenMethod
   public static IMaterialPart getMaterialPart(String name) {
      return new CTMaterialPart(MaterialSystem.getMaterialPart(name));
   }

   @ZenMethod
   public static List<IMaterialPart> registerPartsForMaterial(Material material, String[] partNames) {
      List<IMaterialPart> materialParts = Lists.newArrayList();
      MaterialUser materialUser = ContentTweaker.instance.getMaterialUser();
      if (materialUser != null) {
         materialParts.addAll((Collection)materialUser.registerPartsForMaterial(material, partNames).stream().map(CTMaterialPart::new).collect(Collectors.toList()));
      }

      return materialParts;
   }

   @ZenMethod
   public static Map<String, IMaterialPart> getMaterialParts() {
      return (Map)MaterialSystem.getMaterialParts().entrySet().parallelStream().map((entry) -> {
         return new CTMaterialPart((MaterialPart)entry.getValue());
      }).collect(Collectors.toMap(CTMaterialPart::getName, Function.identity()));
   }

   @ZenMethod
   public static Map<String, IMaterial> getMaterials() {
      return (Map)MaterialSystem.getMaterials().entrySet().parallelStream().map((entry) -> {
         return new CTMaterial((Material)entry.getValue());
      }).collect(Collectors.toMap(CTMaterial::getName, Function.identity()));
   }

   @ZenMethod
   public static Map<String, IPart> getParts() {
      return (Map)MaterialSystem.getParts().entrySet().parallelStream().map((entry) -> {
         return new CTPart((Part)entry.getValue());
      }).collect(Collectors.toMap(CTPart::getName, Function.identity()));
   }

   @ZenMethod
   public static Map<String, IPartType> getPartType() {
      return (Map)MaterialSystem.getPartTypes().entrySet().parallelStream().map((entry) -> {
         return new CTPartType((PartType)entry.getValue());
      }).collect(Collectors.toMap(CTPartType::getName, Function.identity()));
   }

   @ZenMethod
   public static Map<String, IMaterialPart> getMaterialPartsByRegex(String regex) {
      return (Map)MaterialSystem.getMaterialParts().entrySet().parallelStream().filter((entry) -> {
         return Pattern.matches(regex, (CharSequence)entry.getKey());
      }).map((entry) -> {
         return new CTMaterialPart((MaterialPart)entry.getValue());
      }).collect(Collectors.toMap(CTMaterialPart::getName, Function.identity()));
   }
}
