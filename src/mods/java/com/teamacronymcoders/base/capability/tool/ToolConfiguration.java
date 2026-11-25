package com.teamacronymcoders.base.capability.tool;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.modules.tool.CapabilityProviderTool;
import com.teamacronymcoders.base.registrysystem.config.ConfigEntry;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ToolConfiguration {
   private static final ResourceLocation NAME = new ResourceLocation("base", "tool");
   private static String[] defaults = new String[]{"immersiveengineering:tool:0", "thermalfoundation:wrench:*", "techreborn:wrench:*", "actuallyadditions:item_laser_wrench:*", "hammercore:wrench:*", "hammercore:iwrench:*", "mekanism:configurator:*", "calculator:wrench:*", "ic2:wrench:*", "appliedenergistics2:certus_quartz_wrench:*", "appliedenergistics2:nether_quartz_wrench:*", "enderio:item_yeta_wrench:*", "botania:twigwand:*", "essentials:wrench:*", "factorytech:wrench:*"};
   private static Map<ResourceLocation, List<Integer>> actual;

   public ToolConfiguration() {
   }

   public static void configureTool() {
      ConfigRegistry configRegistry = (ConfigRegistry)Base.instance.getRegistry(ConfigRegistry.class, "CONFIG");
      ConfigEntry configEntry = new ConfigEntry("general", "Other Tools", Type.STRING, Arrays.toString(defaults), "Config entry for adding other items to count as tools. Format is \"modid:item_name:meta\" * is for any meta", true, true);
      configRegistry.addEntry(configEntry);
      List<Pair<ResourceLocation, Integer>> entries = (List)Arrays.stream(configEntry.getStringArray()).map((string) -> {
         return string.split(":");
      }).peek((array) -> {
         if (array.length != 3) {
            Base.instance.getLogger().warning("Value " + Arrays.toString(array) + " is not a valid format");
         }

      }).filter((array) -> {
         return array.length != 3;
      }).map((array) -> {
         ResourceLocation registerName = new ResourceLocation(array[0], array[1]);
         return "*".equals(array[2]) ? Pair.of(registerName, Integer.valueOf(-1)) : Pair.of(registerName, Integer.valueOf(Integer.parseInt(array[2])));
      }).collect(Collectors.toList());
      actual = Maps.newHashMap();

      for(Pair<ResourceLocation, Integer> entry : entries) {
         if (!actual.containsKey(entry.getKey())) {
            actual.put(entry.getKey(), Lists.newArrayList());
         }

         if (((Integer)entry.getValue()).intValue() != -1) {
            ((List)actual.get(entry.getKey())).add(entry.getValue());
         }
      }

   }

   @SubscribeEvent
   public static void checkTool(AttachCapabilitiesEvent<ItemStack> attachCapabilitiesEvent) {
      ItemStack itemStack = (ItemStack)attachCapabilitiesEvent.getObject();
      Optional<List<Integer>> optionalMetas = Optional.ofNullable(actual.get(itemStack.getItem().getRegistryName()));
      if (optionalMetas.isPresent()) {
         List<Integer> metas = (List)optionalMetas.get();
         if (metas.isEmpty() || metas.contains(Integer.valueOf(itemStack.getMetadata()))) {
            attachCapabilitiesEvent.addCapability(NAME, new CapabilityProviderTool());
         }
      }

   }
}
