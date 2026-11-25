package com.teamacronymcoders.contenttweaker.modules.materials.parts;

import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.IMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materials.IMaterial;
import com.teamacronymcoders.contenttweaker.modules.materials.parttypes.IPartType;
import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.contenttweaker.Part")
public interface IPart {
   @ZenMethod
   String getName();

   @ZenMethod
   String getUnlocalizedName();

   @ZenMethod
   String getShortUnlocalizedName();

   @ZenMethod
   IPartType getPartType();

   @ZenMethod
   String getPartTypeName();

   @ZenMethod
   String getOreDictPrefix();

   @ZenMethod
   boolean hasOverlay();

   @ZenMethod
   List<IPartDataPiece> getData();

   @ZenMethod
   IMaterialPart registerToMaterial(IMaterial var1);

   @ZenMethod
   List<IMaterialPart> registerToMaterials(IMaterial[] var1);
}
