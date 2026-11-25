package org.orecruncher.dsurround.registry.item;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.footstep.FootstepsRegistry;

@EventBusSubscriber(
   modid = "dsurround",
   value = {Side.CLIENT}
)
public class SimpleArmorItemData extends SimpleItemData implements IArmorItemData {
   private static final Map<ItemClass, IAcoustic> ARMOR = new Reference2ObjectOpenHashMap();
   private static final Map<ItemClass, IAcoustic> FOOT = new Reference2ObjectOpenHashMap();

   @SubscribeEvent
   public static void registryReload(@Nonnull RegistryDataEvent.Reload event) {
      if (event.reg instanceof FootstepsRegistry) {
         ARMOR.clear();
         FOOT.clear();
         AcousticRegistry reg = RegistryManager.ACOUSTICS;
         ARMOR.put(ItemClass.LEATHER, reg.getAcoustic("armor_light"));
         ARMOR.put(ItemClass.CHAIN, reg.getAcoustic("armor_medium"));
         ARMOR.put(ItemClass.CRYSTAL, reg.getAcoustic("armor_crystal"));
         ARMOR.put(ItemClass.PLATE, reg.getAcoustic("armor_heavy"));
         FOOT.put(ItemClass.LEATHER, reg.getAcoustic("armor_light"));
         FOOT.put(ItemClass.CHAIN, reg.getAcoustic("medium_foot"));
         FOOT.put(ItemClass.CRYSTAL, reg.getAcoustic("crystal_foot"));
         FOOT.put(ItemClass.PLATE, reg.getAcoustic("heavy_foot"));
      }

   }

   public SimpleArmorItemData(@Nonnull ItemClass ic) {
      super(ic);
   }

   @Nullable
   public IAcoustic getArmorSound(@Nonnull ItemStack stack) {
      return (IAcoustic)ARMOR.get(this.itemClass);
   }

   @Nullable
   public IAcoustic getFootArmorSound(@Nonnull ItemStack stack) {
      return (IAcoustic)FOOT.get(this.itemClass);
   }
}
