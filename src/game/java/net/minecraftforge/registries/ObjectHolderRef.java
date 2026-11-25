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

import java.util.function.Consumer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Internal class used in tracking object holder references
 */
class ObjectHolderRef<T extends IForgeRegistryEntry<T>>
{
    private final ResourceLocation injectedObject;
    private final Consumer<T> setter;
    private final ForgeRegistry<T> registry;

    @SuppressWarnings("unchecked")
    ObjectHolderRef(Class<T> registryType, ResourceLocation injectedObject, Consumer<T> setter)
    {
        this.registry = (ForgeRegistry<T>)GameRegistry.findRegistry(registryType);
        this.injectedObject = injectedObject;
        this.setter = setter;

        if (this.registry == null)
        {
            throw new IllegalStateException(String.format("The object holder for %s cannot apply to a type that does not map to a registry. Ensure the registry was created during the RegistryEvent.NewRegistry event. (found : %s)", injectedObject, registryType.getName()));
        }
    }

    public void apply()
    {
        T thing;
        if (registry.containsKey(injectedObject) && !registry.isDummied(injectedObject))
        {
            thing = registry.getValue(injectedObject);
        }
        else
        {
            thing = null;
        }

        if (thing == null)
        {
            FMLLog.log.debug("Unable to lookup {}. This means the object wasn't registered. It's likely just mod options.", injectedObject);
            return;
        }
        setter.accept(thing);
    }
}
