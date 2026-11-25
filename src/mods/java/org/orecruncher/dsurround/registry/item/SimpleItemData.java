package org.orecruncher.dsurround.registry.item;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.SoundEffect;

@SideOnly(Side.CLIENT)
public class SimpleItemData implements IItemData {
   public static final Reference2ObjectOpenHashMap<ItemClass, SimpleItemData> CACHE = new Reference2ObjectOpenHashMap();
   protected final ItemClass itemClass;

   public SimpleItemData(@Nonnull ItemClass ic) {
      this.itemClass = ic;
   }

   @Nonnull
   public ItemClass getItemClass() {
      return this.itemClass;
   }

   @Nullable
   public SoundEffect getEquipSound(@Nonnull ItemStack stack) {
      return this.itemClass.getEquipSound();
   }

   @Nullable
   public SoundEffect getSwingSound(@Nonnull ItemStack stack) {
      return this.itemClass.getSwingSound();
   }

   @Nullable
   public SoundEffect getUseSound(@Nonnull ItemStack stack) {
      return this.itemClass.getUseSound();
   }

   static {
      for(ItemClass ic : ItemClass.values()) {
         if (ic.isArmor()) {
            CACHE.put(ic, new SimpleArmorItemData(ic));
         } else {
            CACHE.put(ic, new SimpleItemData(ic));
         }
      }

   }
}
