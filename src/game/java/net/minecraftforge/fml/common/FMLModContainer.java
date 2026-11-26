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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class FMLModContainer<T> implements ModContainer
{
    private final String modId;
    private final String folder;
    private final String annotationName;
    private final String annotationVersion;
    private final String annotationDependencies;
    private final String acceptedMinecraftVersions;
    private final Supplier<T> factory;
    private final BiConsumer<T, FMLEvent> handler;
    private T modInstance;
    private ModMetadata modMetadata;
    private String internalVersion;
    private boolean overridesMetadata = true;
    private boolean enabled = true;
    private boolean clientSideOnly;
    private boolean serverSideOnly;
    private boolean canBeDeactivated;
    private String acceptableRemoteVersions;
    private Supplier<? extends IModGuiFactory> guiFactory;
    private IModGuiFactory guiFactoryInstance;
    private Map<String, String> customModProperties = EMPTY_PROPERTIES;
    private Disableable disableability = Disableable.RESTART;
    private VersionRange minecraftAccepted;
    private DefaultArtifactVersion processedVersion;
    private LoadController controller;
    private IResourcePack resourcePack;

    public FMLModContainer(String modId, String name, String version, String dependencies, String acceptedMinecraftVersions, String folder, Supplier<T> factory, BiConsumer<T, FMLEvent> handler)
    {
        this.modId = modId;
        this.annotationName = name;
        this.annotationVersion = version;
        this.annotationDependencies = dependencies;
        this.acceptedMinecraftVersions = acceptedMinecraftVersions;
        this.folder = folder;
        this.factory = factory;
        this.handler = handler;
        sanityCheckModId();
    }

    public FMLModContainer<T> clientSideOnly()
    {
        this.clientSideOnly = true;
        return this;
    }

    public FMLModContainer<T> serverSideOnly()
    {
        this.serverSideOnly = true;
        return this;
    }

    public FMLModContainer<T> useMetadata(boolean useMetadata)
    {
        this.overridesMetadata = !useMetadata;
        return this;
    }

    public FMLModContainer<T> canBeDeactivated()
    {
        this.canBeDeactivated = true;
        return this;
    }

    public FMLModContainer<T> acceptableRemoteVersions(String acceptableRemoteVersions)
    {
        this.acceptableRemoteVersions = acceptableRemoteVersions;
        return this;
    }

    public FMLModContainer<T> guiFactory(Supplier<? extends IModGuiFactory> guiFactory)
    {
        this.guiFactory = guiFactory;
        return this;
    }

    public FMLModContainer<T> customProperties(Map<String, String> customModProperties)
    {
        this.customModProperties = ImmutableMap.copyOf(customModProperties);
        return this;
    }

    private void sanityCheckModId()
    {
        if (Strings.isNullOrEmpty(modId))
        {
            throw new IllegalArgumentException("The modId is null or empty");
        }
        if (modId.length() > 64)
        {
            throw new IllegalArgumentException(String.format("The modId %s is longer than the maximum of 64 characters.", modId));
        }
        if (!modId.equals(modId.toLowerCase(java.util.Locale.ENGLISH)))
        {
            throw new IllegalArgumentException(String.format("The modId %s must be all lowercase.", modId));
        }
    }

    @Override
    public String getModId()
    {
        return modId;
    }

    @Override
    public String getName()
    {
        return modMetadata.name;
    }

    @Override
    public String getVersion()
    {
        return internalVersion;
    }

    @Override
    public ModMetadata getMetadata()
    {
        return modMetadata;
    }

    public void bindMetadata()
    {
        try (InputStream info = EagRuntime.getResourceStream("/" + folder + "/mcmod.info"))
        {
            bindMetadata(MetadataCollection.from(info, folder));
        }
        catch (java.io.IOException e)
        {
            throw new LoaderException(e);
        }
    }

    @Override
    public void bindMetadata(MetadataCollection mc)
    {
        java.util.Map<String, Object> extra = new java.util.HashMap<>();
        extra.put("name", annotationName);
        extra.put("version", annotationVersion);
        modMetadata = mc.getMetadataForId(modId, extra);

        if (overridesMetadata || !modMetadata.useDependencyInformation)
        {
            DependencyParser dependencyParser = new DependencyParser(modId, FMLCommonHandler.instance().getSide());
            DependencyParser.DependencyInfo info = dependencyParser.parseDependencies(annotationDependencies);
            modMetadata.requiredMods = info.requirements;
            modMetadata.dependencies = info.dependencies;
            modMetadata.dependants = info.dependants;
            FMLLog.log.trace("Parsed dependency info for {}: Requirements: {} After:{} Before:{}", modId, info.requirements, info.dependencies, info.dependants);
        }
        else
        {
            FMLLog.log.trace("Using mcmod dependency info for {}: {} {} {}", modId, modMetadata.requiredMods, modMetadata.dependencies, modMetadata.dependants);
        }
        if (Strings.isNullOrEmpty(modMetadata.name))
        {
            FMLLog.log.info("Mod {} is missing the required element 'name'. Substituting {}", modId, modId);
            modMetadata.name = modId;
        }
        internalVersion = annotationVersion;
        if (Strings.isNullOrEmpty(internalVersion) && !Strings.isNullOrEmpty(modMetadata.version))
        {
            FMLLog.log.warn("Mod {} is missing the required element 'version'. Falling back to metadata version {}", modId, modMetadata.version);
            internalVersion = modMetadata.version;
        }
        if (Strings.isNullOrEmpty(internalVersion))
        {
            FMLLog.log.warn("Mod {} is missing the required element 'version' and no fallback can be found. Substituting '1.0'.", modId);
            modMetadata.version = internalVersion = "1.0";
        }

        String mcVersionString = acceptedMinecraftVersions;
        if ("[1.12]".equals(mcVersionString) || "[1.12.1]".equals(mcVersionString) || "[1.12,1.12.1]".equals(mcVersionString))
        {
            mcVersionString = "[1.12,1.12.2]";
        }
        if (!Strings.isNullOrEmpty(mcVersionString))
        {
            minecraftAccepted = VersionParser.parseRange(mcVersionString);
        }
        else
        {
            minecraftAccepted = Loader.instance().getMinecraftModContainer().getStaticVersionRange();
        }
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        return modMetadata.requiredMods;
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return modMetadata.dependencies;
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        return modMetadata.dependants;
    }

    @Override
    public String getSortingRules()
    {
        return ((overridesMetadata || !modMetadata.useDependencyInformation) ? Strings.nullToEmpty(annotationDependencies) : modMetadata.printableSortingRules());
    }

    @Override
    public boolean matches(Object mod)
    {
        return mod == modInstance;
    }

    @Override
    public Object getMod()
    {
        return modInstance;
    }

    @Override
    public boolean registerBus(LoadController controller)
    {
        if (this.enabled)
        {
            FMLLog.log.debug("Enabling mod {}", modId);
            this.controller = controller;
            return true;
        }
        return false;
    }

    @Override
    public void handleModStateEvent(FMLEvent event)
    {
        if (event instanceof FMLConstructionEvent)
        {
            constructMod((FMLConstructionEvent)event);
        }
        try
        {
            handler.accept(modInstance, event);
            if (event instanceof FMLConstructionEvent)
            {
                ConfigManager.sync(modId, Config.Type.INSTANCE);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
        }
    }

    private void constructMod(FMLConstructionEvent event)
    {
        modInstance = factory.get();
        boolean hasReverseDepends = !event.getReverseDependencies().get(modId).isEmpty();
        if (canBeDeactivated)
        {
            disableability = hasReverseDepends ? Disableable.DEPENDENCIES : Disableable.YES;
        }
        else
        {
            disableability = hasReverseDepends ? Disableable.DEPENDENCIES : Disableable.RESTART;
        }
        NetworkRegistry.INSTANCE.register(this, acceptableRemoteVersions);
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        if (processedVersion == null)
        {
            processedVersion = new DefaultArtifactVersion(modId, getVersion());
        }
        return processedVersion;
    }

    @Override
    public boolean isImmutable()
    {
        return false;
    }

    @Override
    public String getDisplayVersion()
    {
        return modMetadata.version;
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return minecraftAccepted;
    }

    @Override
    public String toString()
    {
        return "FMLMod:" + modId + "{" + getVersion() + "}";
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return customModProperties;
    }

    @Override
    public IResourcePack createResourcePack()
    {
        if (resourcePack == null)
        {
            resourcePack = new FMLFolderResourcePack(this, folder);
        }
        return resourcePack;
    }

    @Override
    public String getAssetsPrefix()
    {
        return folder + "/assets/";
    }

    @Override
    public Map<String, String> getSharedModDescriptor()
    {
        Map<String, String> descriptor = com.google.common.collect.Maps.newHashMap();
        descriptor.put("modsystem", "FML");
        descriptor.put("id", modId);
        descriptor.put("version", getDisplayVersion());
        descriptor.put("name", getName());
        descriptor.put("url", modMetadata.url);
        descriptor.put("authors", modMetadata.getAuthorList());
        descriptor.put("description", modMetadata.description);
        return descriptor;
    }

    @Override
    public Disableable canBeDisabled()
    {
        return disableability;
    }

    @Override
    public IModGuiFactory getGuiFactory()
    {
        if (guiFactoryInstance == null && guiFactory != null)
        {
            guiFactoryInstance = guiFactory.get();
        }
        return guiFactoryInstance;
    }

    @Override
    public boolean shouldLoadInEnvironment()
    {
        if (clientSideOnly && serverSideOnly)
        {
            throw new RuntimeException("Mod annotation claims to be both client and server side only!");
        }

        Side side = FMLCommonHandler.instance().getSide();

        if (clientSideOnly && side != Side.CLIENT)
        {
            FMLLog.log.info("Disabling mod {} it is client side only.", modId);
            return false;
        }

        if (serverSideOnly && side != Side.SERVER)
        {
            FMLLog.log.info("Disabling mod {} it is server side only.", modId);
            return false;
        }

        return true;
    }
}
