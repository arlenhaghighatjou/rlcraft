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

package net.minecraftforge.fml.common.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class EventBus implements IEventExceptionHandler
{
    private static int maxID = 0;

    private HashMap<Object, ArrayList<IEventListener>> listeners = new HashMap<Object, ArrayList<IEventListener>>();
    private Map<Object,ModContainer> listenerOwners = new HashMap<>();
    private final int busID = maxID++;
    private IEventExceptionHandler exceptionHandler;
    private boolean shutdown;

    public EventBus()
    {
        ListenerList.resize(busID + 1);
        exceptionHandler = this;
    }

    public EventBus(@Nonnull IEventExceptionHandler handler)
    {
        this();
        Preconditions.checkNotNull(handler, "EventBus exception handler can not be null");
        exceptionHandler = handler;
    }

    public <T extends Event> void register(Class<T> eventType, Consumer<T> handler)
    {
        register(eventType, EventPriority.NORMAL, false, null, handler);
    }

    public <T extends Event> void register(Class<T> eventType, EventPriority priority, Consumer<T> handler)
    {
        register(eventType, priority, false, null, handler);
    }

    public <T extends Event> void register(Class<T> eventType, EventPriority priority, boolean receiveCanceled, Consumer<T> handler)
    {
        register(eventType, priority, receiveCanceled, null, handler);
    }

    public <T extends GenericEvent<?>> void registerGeneric(Class<T> eventType, Class<?> genericType, Consumer<T> handler)
    {
        register(eventType, EventPriority.NORMAL, false, genericType, handler);
    }

    public <T extends GenericEvent<?>> void registerGeneric(Class<T> eventType, Class<?> genericType, EventPriority priority, boolean receiveCanceled, Consumer<T> handler)
    {
        register(eventType, priority, receiveCanceled, genericType, handler);
    }

    private <T extends Event> void register(Class<T> eventType, EventPriority priority, boolean receiveCanceled, Class<?> genericType, Consumer<T> handler)
    {
        ModContainer owner = Loader.instance().activeModContainer();
        if (owner == null)
        {
            owner = Loader.instance().getMinecraftModContainer();
        }
        listenerOwners.put(handler, owner);

        IEventListener listener = new ConsumerEventListener<T>(handler, receiveCanceled, genericType, owner, IContextSetter.class.isAssignableFrom(eventType));
        ListenerList.forClass(eventType).register(busID, priority, listener);

        ArrayList<IEventListener> others = listeners.get(handler);
        if (others == null)
        {
            others = new ArrayList<>();
            listeners.put(handler, others);
        }
        others.add(listener);
    }

    public void unregister(Object handler)
    {
        ArrayList<IEventListener> list = listeners.remove(handler);
        if(list == null)
            return;
        listenerOwners.remove(handler);
        for (int i = 0, l = list.size(); i < l; ++i)
        {
            ListenerList.unregisterAll(busID, list.get(i));
        }
    }

    public boolean post(Event event)
    {
        if (shutdown) return false;

        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        int index = 0;
        try
        {
            for (; index < listeners.length; index++)
            {
                listeners[index].invoke(event);
            }
        }
        catch (Throwable throwable)
        {
            exceptionHandler.handleException(this, event, listeners, index, throwable);
            Throwables.throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
        return event.isCancelable() && event.isCanceled();
    }

    public void shutdown()
    {
        FMLLog.log.warn("EventBus {} shutting down - future events will not be posted.", busID);
        shutdown = true;
    }

    @Override
    public void handleException(EventBus bus, Event event, IEventListener[] listeners, int index, Throwable throwable)
    {
        FMLLog.log.error("Exception caught during firing event {}:", event, throwable);
        FMLLog.log.error("Index: {} Listeners:", index);
        for (int x = 0; x < listeners.length; x++)
        {
            FMLLog.log.error("{}: {}", x, listeners[x]);
        }
    }

    private static class ConsumerEventListener<T extends Event> implements IEventListener
    {
        private final Consumer<T> handler;
        private final boolean receiveCanceled;
        private final Class<?> genericType;
        private final ModContainer owner;
        private final boolean setContext;

        private ConsumerEventListener(Consumer<T> handler, boolean receiveCanceled, Class<?> genericType, ModContainer owner, boolean setContext)
        {
            this.handler = handler;
            this.receiveCanceled = receiveCanceled;
            this.genericType = genericType;
            this.owner = owner;
            this.setContext = setContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void invoke(Event event)
        {
            if (!receiveCanceled && event.isCancelable() && event.isCanceled())
            {
                return;
            }
            if (genericType != null && genericType != ((IGenericEvent<?>)event).getGenericType())
            {
                return;
            }
            if (setContext)
            {
                ModContainer old = Loader.instance().activeModContainer();
                Loader.instance().setActiveModContainer(owner);
                ((IContextSetter)event).setModContainer(owner);
                handler.accept((T)event);
                Loader.instance().setActiveModContainer(old);
            }
            else
            {
                handler.accept((T)event);
            }
        }

        @Override
        public String toString()
        {
            return "Listener{" + owner + ":" + handler + "}";
        }
    }
}
