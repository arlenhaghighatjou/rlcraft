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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.ModContainer.Disableable;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.registry.ItemStackHolderInjector;
import net.minecraftforge.fml.common.toposort.ModSorter;
import net.minecraftforge.fml.common.toposort.ModSortingException;
import net.minecraftforge.fml.common.toposort.ModSortingException.SortingExceptionData;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;

public class Loader
{
    public static final String MC_VERSION = ForgeVersion.mcVersion;

    private static Loader instance;
	private final List<java.util.function.Supplier<? extends ModContainer>> builtInMods = new java.util.ArrayList<>();

    private List<ModContainer> mods;
    private Map<String, ModContainer> namedMods;
    private VFile2 canonicalConfigDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;
    private ImmutableMap<String, String> fmlBrandingProperties;
    private VFile2 forcedModFile;
    private ProgressBar progressBar;

    public static Loader instance()
    {
        if (instance == null)
        {
            instance = new Loader();
        }

        return instance;
    }

    private Loader()
    {
        minecraft = new MinecraftDummyContainer(MC_VERSION);
        try (InputStream mcpModInputStream = EagRuntime.getResourceStream("/mcpmod.info"))
        {
            mcp = new MCPDummyContainer(MetadataCollection.from(mcpModInputStream, "MCP").getMetadataForId("mcp", ImmutableMap.of("name", "Minecraft Coder Pack", "version", ForgeVersion.mcpVersion)));
        }
        catch (java.io.IOException e)
        {
            throw new LoaderException(e);
        }
    }

    private void sortModList()
    {
        FMLLog.log.trace("Verifying mod requirements are satisfied");
        BiMap<String, ArtifactVersion> modVersions = HashBiMap.create();
        for (ModContainer mod : getActiveModList())
        {
            modVersions.put(mod.getModId(), mod.getProcessedVersion());
        }

        for (ModContainer mod : getActiveModList())
        {
            if (!mod.acceptableMinecraftVersionRange().containsVersion(minecraft.getProcessedVersion()))
            {
                FMLLog.log.fatal("The mod {} does not wish to run in Minecraft version {}. You will have to remove it to play.", mod.getModId(), getMCVersionString());
                throw new WrongMinecraftVersionException(mod, getMCVersionString());
            }
            Set<ArtifactVersion> allDeps = Sets.newHashSet();
            allDeps.addAll(mod.getDependants());
            allDeps.addAll(mod.getDependencies());
            allDeps.addAll(mod.getRequirements());

            MissingModsException missingModsException = new MissingModsException(mod.getModId(), mod.getName());
            for (ArtifactVersion acceptedVersion : allDeps)
            {
                boolean required = mod.getRequirements().contains(acceptedVersion);
                ArtifactVersion currentVersion = modVersions.get(acceptedVersion.getLabel());
                if ((required || currentVersion != null) && (currentVersion == null || !acceptedVersion.containsVersion(currentVersion)))
                {
                    missingModsException.addMissingMod(acceptedVersion, currentVersion, required);
                }
            }
            if (!missingModsException.getMissingModInfos().isEmpty())
            {
                FMLLog.log.fatal(missingModsException.toString());
                throw missingModsException;
            }
        }

        ModSorter sorter = new ModSorter(getActiveModList(), namedMods);
        try
        {
            FMLLog.log.trace("Sorting mods into an ordered list");
            List<ModContainer> sortedMods = sorter.sort();
            modController.getActiveModList().clear();
            modController.getActiveModList().addAll(sortedMods);
            mods.removeAll(sortedMods);
            sortedMods.addAll(mods);
            mods = sortedMods;
            FMLLog.log.trace("Mod sorting completed successfully");
        }
        catch (ModSortingException sortException)
        {
            FMLLog.log.fatal("A dependency cycle was detected in the input mod set so an ordering cannot be determined");
            SortingExceptionData<ModContainer> exceptionData = sortException.getExceptionData();
            FMLLog.log.fatal("The first mod in the cycle is {}", exceptionData.getFirstBadNode());
            FMLLog.log.fatal("The mod cycle involves");
            for (ModContainer mc : exceptionData.getVisitedNodes())
            {
                FMLLog.log.fatal("{} : before: {}, after: {}", mc.toString(), mc.getDependants(), mc.getDependencies());
            }
            throw sortException;
        }
    }

    private void identifyMods()
    {
        mods.add(minecraft);
        mods.add(new InjectedModContainer(mcp));
        mods.add(new InjectedModContainer(new FMLContainer()));
        mods.add(new InjectedModContainer(new ForgeModContainer()));
		for (java.util.function.Supplier<? extends ModContainer> factory : this.builtInMods) {
			ModContainer mod = java.util.Objects.requireNonNull(factory.get(), "Built-in mod container");
			if (mod.shouldLoadInEnvironment()) {
				mods.add(mod);
			}
		}
        namedMods = Maps.uniqueIndex(mods, ModContainer::getModId);
        FMLLog.log.info("Forge Mod Loader has identified {} mod{} to load", mods.size(), mods.size() != 1 ? "s" : "");
    }

	public void registerBuiltInMod(java.util.function.Supplier<? extends ModContainer> factory) {
		if (this.mods != null) {
			throw new IllegalStateException("Register built-in mods before Forge loading starts");
		}
		this.builtInMods.add(java.util.Objects.requireNonNull(factory, "Built-in mod factory"));
	}

    public List<ModContainer> getModList()
    {
        return instance().mods != null ? ImmutableList.copyOf(instance().mods) : ImmutableList.<ModContainer>of();
    }

    public void loadMods()
    {
        progressBar = ProgressManager.push("Loading", 7);
        progressBar.step("Constructing Mods");
        canonicalConfigDir = new VFile2("config");
        mods = Lists.newArrayList();
        namedMods = Maps.newHashMap();
        modController = new LoadController(this);
        modController.transition(LoaderState.LOADING, false);
        identifyMods();
        disableRequestedMods();
        modController.buildModList();
        sortModList();
        mods = ImmutableList.copyOf(mods);

        modController.transition(LoaderState.CONSTRUCTING, false);
        modController.distributeStateMessage(LoaderState.CONSTRUCTING, com.google.common.collect.ArrayListMultimap.create());
        progressBar.step("Initializing mods Phase 1");
        modController.transition(LoaderState.PREINITIALIZATION, false);
    }

    public void preinitializeMods()
    {
        if (!modController.isInState(LoaderState.PREINITIALIZATION))
        {
            FMLLog.log.warn("There were errors previously. Not beginning mod initialization phase");
            return;
        }
        GameData.fireCreateRegistryEvents();
        modController.distributeStateMessage(LoaderState.PREINITIALIZATION, canonicalConfigDir);
        GameData.fireRegistryEvents(rl -> !rl.equals(GameData.RECIPES));
        FMLCommonHandler.instance().fireSidedRegistryEvents();
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
        ItemStackHolderInjector.INSTANCE.inject();
        modController.transition(LoaderState.INITIALIZATION, false);
        progressBar.step("Initializing Minecraft Engine");
    }

    private void disableRequestedMods()
    {
        forcedModFile = new VFile2(canonicalConfigDir, "fmlModState.properties");
        if (!forcedModFile.exists())
        {
            return;
        }
        Properties forcedModListProperties = new Properties();
        try (Reader reader = new InputStreamReader(forcedModFile.getInputStream(), StandardCharsets.UTF_8))
        {
            forcedModListProperties.load(reader);
        }
        catch (Exception e)
        {
            FMLLog.log.info("An error occurred reading the fmlModState.properties file", e);
            return;
        }
        for (String modId : forcedModListProperties.stringPropertyNames())
        {
            ModContainer mod = namedMods.get(modId);
            if (mod != null)
            {
                boolean enabled = Boolean.parseBoolean(forcedModListProperties.getProperty(modId));
                FMLLog.log.info("Setting mod {} to enabled state {}", modId, enabled);
                mod.setEnabledState(enabled);
            }
        }
    }

    public static boolean isModLoaded(String modname)
    {
        ModContainer mod = instance().namedMods.get(modname);
        return mod != null && instance().modController.getModState(mod) != ModState.DISABLED;
    }

    public VFile2 getConfigDir()
    {
        return canonicalConfigDir;
    }

    public String getCrashInformation()
    {
        if (modController == null)
        {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        Joiner.on(' ').skipNulls().appendTo(ret, FMLCommonHandler.instance().getBrandings(false));
        modController.printModStates(ret);
        return ret.toString();
    }

    public String getFMLVersionString()
    {
        return ForgeVersion.majorVersion + "." + ForgeVersion.minorVersion + "." + ForgeVersion.revisionVersion + "." + ForgeVersion.buildVersion;
    }

    public Map<String,ModContainer> getIndexedModList()
    {
        return namedMods != null ? ImmutableMap.copyOf(namedMods) : ImmutableMap.of();
    }

    public void initializeMods()
    {
        progressBar.step("Initializing mods Phase 2");
        CraftingHelper.loadRecipes(false);
        modController.distributeStateMessage(LoaderState.INITIALIZATION);
        progressBar.step("Initializing mods Phase 3");
        modController.transition(LoaderState.POSTINITIALIZATION, false);
        modController.propogateStateMessage(new FMLInterModComms.IMCEvent());
        ItemStackHolderInjector.INSTANCE.inject();
        modController.distributeStateMessage(LoaderState.POSTINITIALIZATION);
        progressBar.step("Finishing up");
        modController.transition(LoaderState.AVAILABLE, false);
        modController.distributeStateMessage(LoaderState.AVAILABLE);
        GameData.freezeData();
        FMLLog.log.info("Forge Mod Loader has successfully loaded {} mod{}", mods.size(), mods.size() == 1 ? "" : "s");
        progressBar.step("Completing Minecraft initialization");
    }

    public ICrashCallable getCallableCrashInformation()
    {
        return new ICrashCallable() {
            @Override
            public String call() throws Exception
            {
                return getCrashInformation();
            }

            @Override
            public String getLabel()
            {
                return "FML";
            }
        };
    }

    public List<ModContainer> getActiveModList()
    {
        return modController != null ? modController.getActiveModList() : ImmutableList.<ModContainer>of();
    }

    public ModState getModState(ModContainer selectedMod)
    {
        return modController.getModState(selectedMod);
    }

    public String getMCVersionString()
    {
        return "Minecraft " + MC_VERSION;
    }

    public boolean serverStarting(Object server)
    {
        modController.distributeStateMessage(LoaderState.SERVER_STARTING, server);
        modController.transition(LoaderState.SERVER_STARTING, false);
        return true;
    }

    public void serverStarted()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STARTED);
        modController.transition(LoaderState.SERVER_STARTED, false);
    }

    public void serverStopping()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STOPPING);
        modController.transition(LoaderState.SERVER_STOPPING, false);
    }

    public BiMap<ModContainer, Object> getModObjectList()
    {
        return modController.getModObjectList();
    }

    public BiMap<Object, ModContainer> getReversedModObjectList()
    {
        return getModObjectList().inverse();
    }

    @Nullable
    public ModContainer activeModContainer()
    {
        return modController != null ? modController.activeContainer() : null;
    }

    public boolean isInState(LoaderState state)
    {
        return modController.isInState(state);
    }

    public MinecraftDummyContainer getMinecraftModContainer()
    {
        return minecraft;
    }

    public boolean hasReachedState(LoaderState state)
    {
        return modController != null ? modController.hasReachedState(state) : false;
    }

    public String getMCPVersionString()
    {
        return "MCP " + ForgeVersion.mcpVersion;
    }

    public void serverStopped()
    {
        modController.distributeStateMessage(LoaderState.SERVER_STOPPED);
        modController.transition(LoaderState.SERVER_STOPPED, true);
        modController.transition(LoaderState.AVAILABLE, true);
    }

    public boolean serverAboutToStart(Object server)
    {
        modController.distributeStateMessage(LoaderState.SERVER_ABOUT_TO_START, server);
        modController.transition(LoaderState.SERVER_ABOUT_TO_START, false);
        return true;
    }

    public Map<String,String> getFMLBrandingProperties()
    {
        if (fmlBrandingProperties == null)
        {
            Properties loaded = new Properties();
            try (InputStream stream = EagRuntime.getResourceStream("/fmlbranding.properties"))
            {
                if (stream != null)
                {
                    loaded.load(stream);
                }
            }
            catch (Exception e)
            {
            }
            fmlBrandingProperties = Maps.fromProperties(loaded);
        }
        return fmlBrandingProperties;
    }

    public Map<String,String> getCustomModProperties(String modId)
    {
        return getIndexedModList().get(modId).getCustomModProperties();
    }

    boolean checkRemoteModList(Map<String, String> modList, Side side)
    {
        Set<String> difference = Sets.newLinkedHashSet(Sets.difference(namedMods.keySet(), modList.keySet()));
        for (Iterator<String> iterator = difference.iterator(); iterator.hasNext();)
        {
            if (modController.getModState(namedMods.get(iterator.next())) == ModState.DISABLED)
            {
                iterator.remove();
            }
        }

        if (difference.size() > 0)
            FMLLog.log.info("Attempting connection with missing mods {} at {}", difference, side);
        return true;
    }

    public void fireRemapEvent(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, boolean isFreezing)
    {
        if (modController!=null)
        {
            modController.propogateStateMessage(new FMLModIdMappingEvent(remaps, isFreezing));
        }
    }

    public void runtimeDisableMod(String modId)
    {
        ModContainer mc = namedMods.get(modId);
        Disableable disableable = mc.canBeDisabled();
        if (disableable == Disableable.NEVER)
        {
            FMLLog.log.info("Cannot disable mod {} - it is never allowed to be disabled", modId);
            return;
        }

        if (disableable == Disableable.DEPENDENCIES)
        {
            FMLLog.log.info("Cannot disable mod {} - there are dependent mods that require its presence", modId);
            return;
        }

        if (disableable == Disableable.YES)
        {
            FMLLog.log.info("Runtime disabling mod {}", modId);
            modController.disableMod(mc);
            List<ModContainer> localmods = Lists.newArrayList(mods);
            localmods.remove(mc);
            mods = ImmutableList.copyOf(localmods);
        }

        Properties props = new Properties();
        try
        {
            if (forcedModFile.exists())
            {
                try (Reader reader = new InputStreamReader(forcedModFile.getInputStream(), StandardCharsets.UTF_8))
                {
                    props.load(reader);
                }
            }
            props.put(modId, "false");
            try (Writer writer = new OutputStreamWriter(forcedModFile.getOutputStream(), StandardCharsets.UTF_8))
            {
                props.store(writer, null);
            }
        }
        catch (Exception e)
        {
            FMLLog.log.info("An error occurred writing the fml mod states file, your disabled change won't persist", e);
        }
    }

    public void loadingComplete()
    {
        ProgressManager.pop(progressBar);
        progressBar = null;
    }

    public final LoaderState getLoaderState()
    {
        return modController != null ? modController.getState() : LoaderState.NOINIT;
    }

    public void setActiveModContainer(@Nullable ModContainer container)
    {
        this.modController.forceActiveContainer(container);
    }
}
