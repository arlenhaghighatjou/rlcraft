package com.teamacronymcoders.base.materialsystem.parts;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.base.util.TextUtils;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class Part {
   private String name;
   private String shortUnlocalizedName;
   private String unlocalizedName;
   private String oreDictName;
   private String ownerId;
   private List<String> additionalOreDictNames;
   private PartType partType;
   private boolean overlay;

   Part(String name, String oreDict, PartType partType, String ownerId, List<String> additionalOreDictNames, boolean overlay) {
      this.name = name;
      this.shortUnlocalizedName = TextUtils.toSnakeCase(name);
      this.ownerId = "base";
      if (ownerId != null) {
         this.ownerId = ownerId;
      }

      this.unlocalizedName = this.ownerId + ".part." + this.shortUnlocalizedName;
      if (oreDict == null) {
         oreDict = name.substring(0, 1).toLowerCase(Locale.US) + name.substring(1);
      }

      this.oreDictName = TextUtils.removeSpecialCharacters(oreDict);
      this.additionalOreDictNames = additionalOreDictNames;
      this.partType = partType;
      this.overlay = overlay;
   }

   public String getName() {
      return this.name;
   }

   public String getShortUnlocalizedName() {
      return this.shortUnlocalizedName;
   }

   public String getUnlocalizedName() {
      return this.unlocalizedName;
   }

   public PartType getPartType() {
      return this.partType;
   }

   public String getPartTypeName() {
      return this.partType.getName();
   }

   public String getOreDictPrefix() {
      return this.oreDictName;
   }

   public String getOwnerId() {
      return this.ownerId;
   }

   public List<String> getAdditionalOreDictNames() {
      return (List)Optional.ofNullable(this.additionalOreDictNames).orElseGet(Lists::newArrayList);
   }

   public boolean hasOverlayTexture() {
      return this.overlay;
   }
}
