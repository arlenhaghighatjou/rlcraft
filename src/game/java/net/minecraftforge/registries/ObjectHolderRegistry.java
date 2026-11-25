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

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

import com.google.common.collect.Lists;

/**
 * Internal registry for tracking object holder references
 */
public enum ObjectHolderRegistry
{
    INSTANCE;
    private List<ObjectHolderRef<?>> objectHolders = Lists.newArrayList();

    public <T extends IForgeRegistryEntry<T>> void register(Class<T> registryType, ResourceLocation id, Consumer<T> setter)
    {
        objectHolders.add(new ObjectHolderRef<T>(registryType, id, setter));
    }

    public void applyObjectHolders()
    {
        FMLLog.log.info("Applying holder lookups");
        for (int i = 0, l = objectHolders.size(); i < l; ++i)
        {
            objectHolders.get(i).apply();
        }
        FMLLog.log.info("Holder lookups applied");
    }

}
