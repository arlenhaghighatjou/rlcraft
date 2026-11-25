package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.materials.CraftingRecipe;
import com.endertech.minecraft.forge.regs.INeedToRegister;
import com.endertech.minecraft.forge.world.ForgeWorld;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IForgeUnit extends INeedToRegister, IHaveConfig {
   static String getStatus(IForgeUnit unit) {
      if (unit != null) {
         if (unit.isEnabled()) {
            return unit.isReadyToRegister() ? "OK" : "NOT READY";
         } else {
            return "DISABLED";
         }
      } else {
         return "unit is NULL!";
      }
   }

   default UnitId createId(String modId, String name) {
      return UnitId.from(modId, name, this.getClassRegName());
   }

   static UnitConfig createConfig(ForgeMod mod, String folder, Class<?> clazz) {
      Path baseDir = mod.getConfig().getConfigDir().resolve(folder);
      String configName = getClassRegName(clazz);
      return new UnitConfig(baseDir, configName);
   }

   default <E extends Enum<E> & IForgeEnum> Map<Object, UnitVariant> createUnitVariants(Class<E> clazz) {
      Map<Object, UnitVariant> map = new HashMap();

      for(E en : (Enum[])clazz.getEnumConstants()) {
         UnitId id = this.getId().withMeta(en.ordinal());
         UnitVariant variant = UnitVariant.from(id, en);
         map.put(en, variant);
      }

      return map;
   }

   default void addCraftingRecipe(String pattern, int amount, String... components) {
      CraftingRecipe recipe = CraftingRecipe.CreateMain(this.getUnitCategory(), this.getConfig(), pattern, this.getStringId(), amount, components);
      this.addCraftingRecipe(recipe);
   }

   default void addCraftingRecipe(CraftingRecipe recipe) {
      if (this.isEnabled()) {
         this.getMod().getRegistrator().addCrafting(recipe);
      }

   }

   static String getClassRegName(Class<?> clazz) {
      return UnitId.dictToRegName(clazz.getSimpleName());
   }

   default String getClassRegName() {
      return getClassRegName(this.getClass());
   }

   default String getUnitCategory() {
      return this.getId().getDictName();
   }

   default boolean isServerSide(World world) {
      return ForgeWorld.isServerSide(world);
   }

   default boolean isClientSide(World world) {
      return ForgeWorld.isClientSide(world);
   }

   UnitId getId();

   default String getStringId() {
      return this.getId().toString();
   }

   ForgeMod getMod();

   boolean isEnabled();

   boolean isRegistered();

   void onInit();

   @SideOnly(Side.CLIENT)
   void onInitClient();

   Map<Object, UnitVariant> getUnitVariants();

   UnitVariant getDefaultVariant();

   default UnitVariant getVariant(Object key) {
      return (UnitVariant)this.getUnitVariants().getOrDefault(key, this.getDefaultVariant());
   }

   <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> var1);

   default void register() {
      this.getMod().getRegistrator().addUnit(this);
   }

   default void unregister() {
      this.getMod().getRegistrator().removeUnit(this);
   }

   default boolean displayOnCreativeTab() {
      return this.isEnabled();
   }

   public static enum Variants implements IForgeEnum {
      NORMAL,
      INVENTORY;

      private Variants() {
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }
   }
}
