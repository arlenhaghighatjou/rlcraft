/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public interface IForgeRegistryEntry<V>
{
    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     *
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     *
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     *
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    V setRegistryName(ResourceLocation name);

    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    @Nullable
    ResourceLocation getRegistryName();

    Class<V> getRegistryType();

    // Default implementation, modders who make extra items SHOULD extend this instead of Object.
    // So, all fields in interfaces are forced static, so even with Java8 people must still extend this.
    @SuppressWarnings({ "serial", "unchecked" })
    public static class Impl<T  extends IForgeRegistryEntry<T>> implements IForgeRegistryEntry<T>
    {
        private static final List<Class<?>> CUSTOM_TYPES = new ArrayList<>();

        static void registerType(Class<?> type)
        {
            if (!CUSTOM_TYPES.contains(type))
            {
                CUSTOM_TYPES.add(type);
            }
        }

        private final Class<T> registryType = resolveRegistryType();
        public final IRegistryDelegate<T> delegate = new RegistryDelegate<T>((T)this, registryType);
        ResourceLocation registryName = null;

        private Class<T> resolveRegistryType()
        {
            if (this instanceof net.minecraft.block.Block) return (Class<T>)net.minecraft.block.Block.class;
            if (this instanceof net.minecraft.item.Item) return (Class<T>)net.minecraft.item.Item.class;
            if (this instanceof net.minecraft.potion.Potion) return (Class<T>)net.minecraft.potion.Potion.class;
            if (this instanceof net.minecraft.world.biome.Biome) return (Class<T>)net.minecraft.world.biome.Biome.class;
            if (this instanceof net.minecraft.util.SoundEvent) return (Class<T>)net.minecraft.util.SoundEvent.class;
            if (this instanceof net.minecraft.potion.PotionType) return (Class<T>)net.minecraft.potion.PotionType.class;
            if (this instanceof net.minecraft.enchantment.Enchantment) return (Class<T>)net.minecraft.enchantment.Enchantment.class;
            if (this instanceof net.minecraft.item.crafting.IRecipe) return (Class<T>)net.minecraft.item.crafting.IRecipe.class;
            if (this instanceof net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession) return (Class<T>)net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession.class;
            if (this instanceof net.minecraftforge.registries.DataSerializerEntry) return (Class<T>)net.minecraftforge.registries.DataSerializerEntry.class;
            if (this instanceof net.minecraftforge.fml.common.registry.EntityEntry) return (Class<T>)net.minecraftforge.fml.common.registry.EntityEntry.class;
            for (int i = 0, l = CUSTOM_TYPES.size(); i < l; ++i)
            {
                Class<?> type = CUSTOM_TYPES.get(i);
                if (type.isInstance(this)) return (Class<T>)type;
            }
            throw new IllegalStateException("Unknown Forge registry entry type: " + getClass().getName());
        }

        public final T setRegistryName(String name)
        {
            if (getRegistryName() != null)
                throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

            this.registryName = GameData.checkPrefix(name, true);
            return (T)this;
        }

        //Helper functions
        @Override
        public final T setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }
        public final T setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }
        @Override
        @Nullable
        public final ResourceLocation getRegistryName()
        {
            if (delegate.name() != null) return delegate.name();
            return registryName != null ? registryName : null;
        }

        @Override
        public final Class<T> getRegistryType() { return registryType; };
    }
}
