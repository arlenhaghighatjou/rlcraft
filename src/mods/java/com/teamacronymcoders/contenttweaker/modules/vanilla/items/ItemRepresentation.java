package com.teamacronymcoders.contenttweaker.modules.vanilla.items;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.api.IRepresentation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemColorSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemDestroySpeed;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemDestroyedBlock;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemGetContainerItem;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemRightClick;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemUpdate;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemUse;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemUseFinish;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.ILocalizedNameSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.creativetab.MCCreativeTab;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.contenttweaker.Item")
public class ItemRepresentation implements IRepresentation<Item> {
   @ZenProperty
   public String unlocalizedName;
   @ZenProperty
   public int maxStackSize = 64;
   @ZenProperty
   public String rarity = EnumRarity.COMMON.toString();
   @ZenProperty
   public ICreativeTab creativeTab = new MCCreativeTab(CreativeTabs.MISC);
   @ZenProperty
   public float smeltingExperience = -1.0F;
   @ZenProperty
   public String toolClass = "";
   @ZenProperty
   public int toolLevel = -1;
   @ZenProperty
   public boolean beaconPayment = false;
   @ZenProperty
   public IItemRightClick itemRightClick = null;
   @ZenProperty
   public String itemUseAction = EnumAction.NONE.toString();
   @ZenProperty
   public boolean glowing;
   @ZenProperty
   public IItemUse onItemUse = null;
   @ZenProperty
   public int maxDamage = -1;
   @ZenProperty
   public IItemDestroySpeed itemDestroySpeed = null;
   @ZenProperty
   public IItemDestroyedBlock itemDestroyedBlock = null;
   @ZenProperty
   public IItemGetContainerItem itemGetContainerItem = null;
   @ZenProperty
   public IItemColorSupplier itemColorSupplier = (itemStack, tint) -> {
      return CTColor.fromInt(-1);
   };
   @ZenProperty
   public CTResourceLocation textureLocation;
   @ZenProperty
   public ILocalizedNameSupplier localizedNameSupplier;
   @ZenProperty
   public IItemUpdate onItemUpdate = null;
   @ZenProperty
   public IItemUseFinish onItemUseFinish = null;

   public ItemRepresentation() {
   }

   @ZenMethod
   public String getUnlocalizedName() {
      return this.unlocalizedName;
   }

   @ZenMethod
   public void setUnlocalizedName(String unlocalizedName) {
      this.unlocalizedName = unlocalizedName;
   }

   @ZenMethod
   public int getMaxStackSize() {
      return this.maxStackSize;
   }

   @ZenMethod
   public void setMaxStackSize(int maxStackSize) {
      this.maxStackSize = maxStackSize;
   }

   @ZenMethod
   public String getRarity() {
      return this.rarity;
   }

   @ZenMethod
   public void setRarity(String rarity) {
      this.rarity = rarity;
   }

   @ZenMethod
   public ICreativeTab getCreativeTab() {
      return this.creativeTab;
   }

   @ZenMethod
   public void setCreativeTab(ICreativeTab creativeTab) {
      this.creativeTab = creativeTab;
   }

   @ZenMethod
   public float getSmeltingExperience() {
      return this.smeltingExperience;
   }

   @ZenMethod
   public void setSmeltingExperience(float smeltingExperience) {
      this.smeltingExperience = smeltingExperience;
   }

   @ZenMethod
   public String getToolClass() {
      return this.toolClass;
   }

   @ZenMethod
   public void setToolClass(String toolClass) {
      this.toolClass = toolClass;
   }

   @ZenMethod
   public int getToolLevel() {
      return this.toolLevel;
   }

   @ZenMethod
   public void setToolLevel(int toolLevel) {
      this.toolLevel = toolLevel;
   }

   @ZenMethod
   public boolean isBeaconPayment() {
      return this.beaconPayment;
   }

   @ZenMethod
   public void setBeaconPayment(boolean beaconPayment) {
      this.beaconPayment = beaconPayment;
   }

   @ZenMethod
   public IItemRightClick getItemRightClick() {
      return this.itemRightClick;
   }

   @ZenMethod
   public void setItemRightClick(IItemRightClick itemRightClick) {
      this.itemRightClick = itemRightClick;
   }

   @ZenMethod
   public String getItemUseAction() {
      return this.itemUseAction;
   }

   @ZenMethod
   public void setItemUseAction(String itemUseAction) {
      this.itemUseAction = itemUseAction;
   }

   @ZenMethod
   public boolean isGlowing() {
      return this.glowing;
   }

   @ZenMethod
   public void setGlowing(boolean glowing) {
      this.glowing = glowing;
   }

   @ZenMethod
   public IItemUse getOnItemUse() {
      return this.onItemUse;
   }

   @ZenMethod
   public void setOnItemUse(IItemUse itemUse) {
      this.onItemUse = itemUse;
   }

   @ZenMethod
   public int getMaxDamage() {
      return this.maxDamage;
   }

   @ZenMethod
   public void setMaxDamage(int maxDamage) {
      this.maxDamage = maxDamage;
   }

   @ZenMethod
   public IItemDestroySpeed getItemDestroySpeed() {
      return this.itemDestroySpeed;
   }

   @ZenMethod
   public void setItemDestroySpeed(IItemDestroySpeed itemDestroySpeed) {
      this.itemDestroySpeed = itemDestroySpeed;
   }

   @ZenMethod
   public IItemDestroyedBlock getItemDestroyedBlock() {
      return this.itemDestroyedBlock;
   }

   @ZenMethod
   public void setItemDestroyedBlock(IItemDestroyedBlock itemDestroyedBlock) {
      this.itemDestroyedBlock = itemDestroyedBlock;
   }

   @ZenMethod
   public IItemGetContainerItem getItemGetContainerItem() {
      return this.itemGetContainerItem;
   }

   @ZenMethod
   public void setItemGetContainerItem(IItemGetContainerItem itemGetContainerItem) {
      this.itemGetContainerItem = itemGetContainerItem;
   }

   @ZenMethod
   public IItemColorSupplier getItemColorSupplier() {
      return this.itemColorSupplier;
   }

   @ZenMethod
   public void setItemColorSupplier(IItemColorSupplier itemColorSupplier) {
      this.itemColorSupplier = itemColorSupplier;
   }

   @ZenMethod
   public CTResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   @ZenMethod
   public void setTextureLocation(CTResourceLocation resourceLocation) {
      this.textureLocation = resourceLocation;
   }

   @ZenMethod
   public ILocalizedNameSupplier getLocalizedNameSupplier() {
      return this.localizedNameSupplier;
   }

   @ZenMethod
   public void setLocalizedNameSupplier(ILocalizedNameSupplier localizedNameSupplier) {
      this.localizedNameSupplier = localizedNameSupplier;
   }

   @ZenMethod
   public IItemUseFinish getOnItemUseFinish() {
      return this.onItemUseFinish;
   }

   @ZenMethod
   public void setOnItemUseFinish(IItemUseFinish onItemUseFinish) {
      this.onItemUseFinish = onItemUseFinish;
   }

   public String getName() {
      return this.getUnlocalizedName();
   }

   public String getTypeName() {
      return "Item";
   }

   @ZenMethod
   public void register() {
      ((ItemRegistry)ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM")).register(new ItemContent(this));
   }

   public Item getInternal() {
      return (Item)((ItemRegistry)ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM")).get(new ResourceLocation("contenttweaker", this.getUnlocalizedName()));
   }
}
