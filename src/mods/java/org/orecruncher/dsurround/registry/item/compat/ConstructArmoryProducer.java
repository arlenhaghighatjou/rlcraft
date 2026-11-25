package org.orecruncher.dsurround.registry.item.compat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.item.IItemData;
import org.orecruncher.dsurround.registry.item.ItemClass;
import org.orecruncher.dsurround.registry.item.SimpleItemData;

@EventBusSubscriber(
   modid = "dsurround",
   value = {Side.CLIENT}
)
public class ConstructArmoryProducer implements IItemDataProducer {
   static final Map<String, IAcoustic> ARMOR = new Object2ObjectOpenHashMap();
   static final Map<String, IAcoustic> FOOT = new Object2ObjectOpenHashMap();
   private Class<?> helmet;
   private Class<?> chestplate;
   private Class<?> leggings;
   private Class<?> boots;

   @SubscribeEvent
   public static void registryReload(@Nonnull RegistryDataEvent.Reload event) {
      if (event.reg instanceof AcousticRegistry) {
         ARMOR.clear();
         FOOT.clear();
         AcousticRegistry reg = RegistryManager.ACOUSTICS;
         IAcoustic a = reg.getAcoustic("armor_slimey");
         ARMOR.put("slimey_green_armor", a);
         FOOT.put("slimey_green_armor", a);
         ARMOR.put("slimey_blue_armor", a);
         FOOT.put("slimey_blue_armor", a);
      }

   }

   public ConstructArmoryProducer() {
      try {
         this.helmet = Class.forName("c4.conarm.common.items.armor.Helmet");
         this.chestplate = Class.forName("c4.conarm.common.items.armor.Chestplate");
         this.leggings = Class.forName("c4.conarm.common.items.armor.Leggings");
         this.boots = Class.forName("c4.conarm.common.items.armor.Boots");
      } catch (Throwable var2) {
         ModBase.log().error("Unable to initialize Construct Armory producer!", var2);
         this.helmet = null;
         this.chestplate = null;
         this.leggings = null;
         this.boots = null;
      }

      MinecraftForge.EVENT_BUS.register(ConstructArmoryProducer.class);
   }

   protected boolean isValid() {
      return this.helmet != null;
   }

   @Nullable
   public IItemData create(@Nonnull Item item, @Nonnull ItemClass ic) {
      if (this.isValid()) {
         if (this.helmet.isInstance(item)) {
            return (IItemData)SimpleItemData.CACHE.get(ic);
         }

         if (this.chestplate.isInstance(item)) {
            return new ConstructArmoryItemData(ic);
         }

         if (this.leggings.isInstance(item)) {
            return new ConstructArmoryItemData(ic);
         }

         if (this.boots.isInstance(item)) {
            return new ConstructArmoryItemData(ic);
         }
      }

      return null;
   }
}
