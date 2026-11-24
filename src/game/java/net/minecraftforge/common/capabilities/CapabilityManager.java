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

package net.minecraftforge.common.capabilities;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import java.util.function.Consumer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public enum CapabilityManager
{
    INSTANCE;

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The Interface to be registered
     * @param storage A default implementation of the storage handler.
     * @param implementation A default implementation of the interface.
     * @deprecated Use the overload that takes a factory instead of a class.
     *  You can easily do this by passing a constructor reference
     *  (MyImpl::new instead of MyImpl.class). TODO remove in 1.13.
     */
    @Deprecated
    public <T> void register(Class<T> type, Capability.IStorage<T> storage, final Class<? extends T> implementation)
    {
        Preconditions.checkArgument(implementation != null, "Attempted to register a capability with no default implementation");
        register(type, storage, () ->
        {
            try {
                return implementation.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Registers a capability to be consumed by others.
     * APIs who define the capability should call this.
     * To retrieve the Capability instance, use the @CapabilityInject annotation.
     *
     * @param type The Interface to be registered
     * @param storage A default implementation of the storage handler.
     * @param factory A Factory that will produce new instances of the default implementation.
     */
    public <T> void register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory)
    {
        Preconditions.checkArgument(type    != null, "Attempted to register a capability with invalid type");
        Preconditions.checkArgument(storage != null, "Attempted to register a capability with no storage implementation");
        Preconditions.checkArgument(factory != null, "Attempted to register a capability with no default implementation factory");
        String realName = type.getName().intern();
        Preconditions.checkState(!providers.containsKey(realName), "Can not register a capability implementation multiple times: %s", realName);

        Capability<T> cap = new Capability<T>(realName, storage, factory);
        providers.put(realName, cap);

        List<Consumer<Capability<?>>> list = callbacks.remove(realName);
        if (list != null)
        {
            for (Consumer<Capability<?>> callback : list)
            {
                callback.accept(cap);
            }
        }
    }

	private final IdentityHashMap<String, Capability<?>> providers = Maps.newIdentityHashMap();
	private final IdentityHashMap<String, List<Consumer<Capability<?>>>> callbacks = Maps.newIdentityHashMap();

	@SuppressWarnings("unchecked")
	public <T> void onRegistered(Class<T> type, Consumer<Capability<T>> callback)
	{
		String name = type.getName().intern();
		Capability<?> capability = providers.get(name);
		if (capability != null)
		{
			callback.accept((Capability<T>)capability);
		}
		else
		{
			callbacks.computeIfAbsent(name, key -> Lists.newArrayList())
					.add(value -> callback.accept((Capability<T>)value));
		}
	}
}
