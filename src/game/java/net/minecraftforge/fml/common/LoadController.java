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

package net.minecraftforge.fml.common;

import java.util.List;
import net.minecraftforge.common.util.TextTable;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.lax1dude.eaglercraft.v1_8.HString;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import javax.annotation.Nullable;

public class LoadController
{
    private Loader loader;
    private LoaderState state;
    private Multimap<String, ModState> modStates = MultimapBuilder.hashKeys().enumSetValues(ModState.class).build();
    private List<ModContainer> activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;

    public LoadController(Loader loader)
    {
        this.loader = loader;
        state = LoaderState.NOINIT;
    }

    void disableMod(ModContainer mod)
    {
        String modId = mod.getModId();
        mod.handleModStateEvent(new FMLModDisabledEvent());
        modStates.put(modId, ModState.DISABLED);
        modObjectList.remove(mod);
        activeModList.remove(mod);
    }

    public void buildModList()
    {
        for (ModContainer mod : loader.getModList())
        {
            if (mod.registerBus(this))
            {
                activeModList.add(mod);
                modStates.put(mod.getModId(), ModState.LOADED);
            }
            else
            {
                FMLLog.log.warn("Mod {} has been disabled through configuration", mod.getModId());
                modStates.put(mod.getModId(), ModState.UNLOADED);
                modStates.put(mod.getModId(), ModState.DISABLED);
            }
        }
    }

    public void distributeStateMessage(LoaderState state, Object... eventData)
    {
        if (state.hasEvent())
        {
            propogateStateMessage(state.getEvent(eventData));
        }
    }

    public void transition(LoaderState desiredState, boolean forceState)
    {
        if (FMLCommonHandler.instance().isDisplayCloseRequested())
        {
            FMLLog.log.info("The game window is being closed by the player, exiting.");
            FMLCommonHandler.instance().exitJava(0, false);
        }
        LoaderState oldState = state;
        state = state.transition(false);
        if (state != desiredState)
        {
            if (!forceState)
            {
                throw new LoaderException(HString.format("A fatal error occurred during the state transition from %s to %s. State became %s instead. Loading cannot continue.", oldState, desiredState, state));
            }
            else
            {
                FMLLog.log.info("The state engine was in incorrect state {} and forced into state {}. Errors may have been discarded.", state, desiredState);
                forceState(desiredState);
            }
        }
    }

    @Nullable
    public ModContainer activeContainer()
    {
        return activeContainer;
    }

    void forceActiveContainer(@Nullable ModContainer container)
    {
        activeContainer = container;
    }

    public void propogateStateMessage(FMLEvent stateEvent)
    {
        if (stateEvent instanceof FMLPreInitializationEvent)
        {
            modObjectList = buildModObjectList();
        }
        ProgressBar bar = ProgressManager.push(stateEvent.description(), activeModList.size(), true);
        for (int i = 0, l = activeModList.size(); i < l; ++i)
        {
            ModContainer mc = activeModList.get(i);
            bar.step(mc.getName());
            sendEventToModContainer(stateEvent, mc);
        }
        ProgressManager.pop(bar);
    }

    private void sendEventToModContainer(FMLEvent stateEvent, ModContainer mc)
    {
        String modId = mc.getModId();
        for (ArtifactVersion av : mc.getDependencies())
        {
            String label = av.getLabel();
            if (label != null && mc.getRequirements().contains(av) && modStates.containsEntry(label, ModState.ERRORED))
            {
                FMLLog.log.error("Skipping event {} and marking errored mod {} since required dependency {} has errored", stateEvent.getEventType(), modId, label);
                modStates.put(modId, ModState.ERRORED);
                return;
            }
        }
        activeContainer = mc;
        stateEvent.applyModContainer(mc);
        FMLLog.log.trace("Sending event {} to mod {}", stateEvent.getEventType(), modId);
        try
        {
            mc.handleModStateEvent(stateEvent);
        }
        catch (Throwable t)
        {
            errorOccurred(mc, t);
        }
        FMLLog.log.trace("Sent event {} to mod {}", stateEvent.getEventType(), modId);
        activeContainer = null;
        if (stateEvent instanceof FMLStateEvent)
        {
            modStates.put(modId, ((FMLStateEvent) stateEvent).getModState());
        }
    }

    public ImmutableBiMap<ModContainer, Object> buildModObjectList()
    {
        ImmutableBiMap.Builder<ModContainer, Object> builder = ImmutableBiMap.builder();
        for (int i = 0, l = activeModList.size(); i < l; ++i)
        {
            ModContainer mc = activeModList.get(i);
            Object mod = mc.getMod();
            if (!mc.isImmutable() && mod != null)
            {
                builder.put(mc, mod);
            }
            if (mod == null && !mc.isImmutable() && state != LoaderState.CONSTRUCTING)
            {
                this.errorOccurred(mc, new RuntimeException(HString.format("There is a severe problem with %s (%s) - it appears not to have constructed correctly", mc.getName(), mc.getModId())));
            }
        }
        return builder.build();
    }

    public void errorOccurred(ModContainer modContainer, Throwable exception)
    {
        String modId = modContainer.getModId();
        modStates.put(modId, ModState.ERRORED);
        if (exception instanceof LoaderException)
        {
            throw (LoaderException) exception;
        }
        throw new LoaderExceptionModCrash(HString.format("Caught exception from %s (%s)", modContainer.getName(), modId), exception);
    }

    public void printModStates(StringBuilder ret)
    {
        ret.append("\n\tStates:");
        for (ModState state : ModState.values())
            ret.append(" '").append(state.getMarker()).append("' = ").append(state.toString());

        TextTable table = new TextTable(Lists.newArrayList(
            TextTable.column("State"),
            TextTable.column("ID"),
            TextTable.column("Version"))
        );
        for (ModContainer mc : loader.getModList())
        {
            StringBuilder markers = new StringBuilder();
            for (ModState s : modStates.get(mc.getModId()))
            {
                markers.append(s.getMarker());
            }
            table.add(markers.toString(), mc.getModId(), mc.getVersion());
        }

        ret.append("\n");
        ret.append("\n\t");
        table.append(ret, "\n\t");
        ret.append("\n");
    }

    public List<ModContainer> getActiveModList()
    {
        return activeModList;
    }

    public ModState getModState(ModContainer selectedMod)
    {
        return Iterables.getLast(modStates.get(selectedMod.getModId()), ModState.AVAILABLE);
    }

    public BiMap<ModContainer, Object> getModObjectList()
    {
        if (modObjectList == null)
        {
            FMLLog.log.fatal("Detected an attempt by a mod {} to perform game activity during mod construction. This is a serious programming error.", activeContainer);
            return buildModObjectList();
        }
        return ImmutableBiMap.copyOf(modObjectList);
    }

    public boolean isInState(LoaderState state)
    {
        return this.state == state;
    }

    boolean hasReachedState(LoaderState state)
    {
        return this.state.ordinal() >= state.ordinal() && this.state != LoaderState.ERRORED;
    }

    void forceState(LoaderState newState)
    {
        this.state = newState;
    }

    LoaderState getState()
    {
        return state;
    }
}
