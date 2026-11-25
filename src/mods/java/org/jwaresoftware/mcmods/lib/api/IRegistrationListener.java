package org.jwaresoftware.mcmods.lib.api;

import javax.annotation.Nonnull;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IRegistrationListener {
   <V extends IForgeRegistryEntry<V>> void notifyRegistered(@Nonnull IForgeRegistry<V> var1);
}
