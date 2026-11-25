package com.teamacronymcoders.contenttweaker.modules.chickens;

import com.setycz.chickens.registry.ChickensRegistry;
import com.setycz.chickens.registry.ChickensRegistryItem;
import com.teamacronymcoders.contenttweaker.api.IRepresentation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ModOnly("chickens")
@ZenClass("mods.contenttweaker.Chicken")
public class ChickenRepresentation implements IRepresentation<ChickensRegistryItem> {
   @ZenProperty
   public String name;
   @ZenProperty
   public IItemStack layItem;
   @ZenProperty
   public IItemStack dropItem;
   @ZenProperty
   public CTColor backgroundColor;
   @ZenProperty
   public CTColor foregroundColor;
   @ZenProperty
   public CTResourceLocation textureLocation;
   @ZenProperty
   public String spawnType = null;
   @ZenProperty
   public float layCoefficient = 1.0F;
   @ZenProperty
   public CTResourceLocation parentOne;
   @ZenProperty
   public CTResourceLocation parentTwo;

   public ChickenRepresentation(String name, CTColor color, IItemStack iItemStack) {
      this.name = name;
      this.backgroundColor = color;
      this.foregroundColor = color;
      this.layItem = iItemStack;
      this.textureLocation = CTResourceLocation.create("contenttweaker:textures/entities/" + name + ".png");
   }

   public String getName() {
      return this.name;
   }

   public String getTypeName() {
      return "Chicken";
   }

   @ZenMethod
   public void register() {
      ChickenFactory.CHICKEN_REPRESENTATIONS.add(this);
   }

   public ChickensRegistryItem getInternal() {
      return ChickensRegistry.getByResourceLocation(new ResourceLocation("contenttweaker", this.name));
   }
}
