package xzeroair.trinkets.traits.abilities.interfaces;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.traits.AbilityHandler;

public interface IAbilityInterface {
   ResourceLocation getRegistryName();

   String getDisplayName();

   String getTranslationKey();

   String getUUID();

   @SideOnly(Side.CLIENT)
   void getDescription(List<String> var1);

   default void onAbilityAdded(EntityLivingBase entity) {
   }

   default void onAbilityRemoved(EntityLivingBase entity) {
   }

   default boolean shouldRemove() {
      return false;
   }

   default IAbilityInterface scheduleRemoval() {
      return this;
   }

   default AbilityHandler.AbilityHolder getAbilityHolder() {
      return null;
   }

   default IAbilityInterface cacheAbilityHolder(AbilityHandler.AbilityHolder holder) {
      return this;
   }

   default void loadStorage(NBTTagCompound compound) {
   }

   default NBTTagCompound saveStorage(NBTTagCompound compound) {
      return compound;
   }
}
