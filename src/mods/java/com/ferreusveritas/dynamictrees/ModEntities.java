package com.ferreusveritas.dynamictrees;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
   public static final String FALLING_TREE = "falling_tree";

   public ModEntities() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, EntityEntry.class, ModEntities::register);
   }

   public static void register(Register<EntityEntry> event) {
      int id = 0;
      EntityRegistry.registerModEntity(new ResourceLocation("dynamictrees", "falling_tree"), EntityFallingTree.class, "falling_tree", id++, "dynamictrees", 512, Integer.MAX_VALUE, true, EntityFallingTree::new);
   }
}
