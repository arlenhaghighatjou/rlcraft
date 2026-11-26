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

import baubles.client.gui.BaublesGuiFactory;
import baubles.common.Baubles;
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

import shadows.placebo.Placebo;
import vazkii.arl.AutoRegLib;
import vazkii.neat.GuiFactory;
import vazkii.neat.Neat;

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

    private List<ModContainer> mods;
    private Map<String, ModContainer> namedMods;
    private VFile2 canonicalConfigDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;
    private ImmutableMap<String, String> fmlBrandingProperties;
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
        mods.add(new FMLModContainer<>("portaldupebegone", "PortalDupeBegone", "1.0.0", "", "", "portaldupebegone", fonnymunkey.portaldupebegone.PortalDupeBegone::new, fonnymunkey.portaldupebegone.PortalDupeBegone::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("baubles", "Baubles", "1.5.2", "required-after:forge@[14.21.0.2348,);", "[1.12]", "baubles", Baubles::new, Baubles::handleModStateEvent).guiFactory(BaublesGuiFactory::new));
        mods.add(new FMLModContainer<>("autoreglib", "AutoRegLib", "1.3-32", "required-after:forge@[14.21.1.2387,)", "[1.12.2]", "autoreglib", AutoRegLib::new, AutoRegLib::handleModStateEvent));
        mods.add(new FMLModContainer<>("placebo", "Placebo", "1.6.0", "", "[1.12.2]", "placebo", Placebo::new, Placebo::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("neat", "Neat", "1.4-17", "", "[1.12.2]", "neat", Neat::new, Neat::handleModStateEvent).clientSideOnly().guiFactory(GuiFactory::new));
        mods.add(new FMLModContainer<>("craftablechainmail", "", "", "", "[1.12.2]", "craftablechainmail", moddy10.craftablechainmail.CraftableChainmail::new, moddy10.craftablechainmail.CraftableChainmail::handleModStateEvent));
        mods.add(new FMLModContainer<>("shieldbreak", "ShieldBreak", "1.1.3", "", "", "shieldbreak", shieldbreak.core.ShieldBreak::new, shieldbreak.core.ShieldBreak::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("anvilpatch", "Anvil Patch - Lawful", "1.1.1", "", "", "anvilpatch", lumberwizard.anvilpatch.AnvilPatch::new, lumberwizard.anvilpatch.AnvilPatch::handleModStateEvent));
        mods.add(new FMLModContainer<>("collisiondamage", "CollisionDamage", "1.2.2", "", "", "collisiondamage", collisiondamage.core.CollisionDamage::new, collisiondamage.core.CollisionDamage::handleModStateEvent));
        mods.add(new FMLModContainer<>("bedbreakbegone", "BedBreakBegone", "1.0.2", "", "", "bedbreakbegone", bedbreakbegone.core.BedBreakBegone::new, bedbreakbegone.core.BedBreakBegone::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("framevoidpatch", "Frame Void Patch", "1.0.0", "", "", "framevoidpatch", framevoidpatch.core.FrameVoidPatch::new, framevoidpatch.core.FrameVoidPatch::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("disenchanter", "Disenchanter", "1.8", "", "", "disenchanter", de.impelon.disenchanter.DisenchanterMain::new, de.impelon.disenchanter.DisenchanterMain::handleModStateEvent));
        mods.add(new FMLModContainer<>("pigstep", "Pigstep", "1.12", "", "[1.12.2]", "pigstep", com.daposeidonguy.pigstep.PigstepMod::new, com.daposeidonguy.pigstep.PigstepMod::handleModStateEvent));
        mods.add(new FMLModContainer<>("globalgamerules", "Global GameRules", "2.2", "", "[1.12.2]", "globalgamerules", se.gory_moon.globalgamerules.GlobalGR::new, se.gory_moon.globalgamerules.GlobalGR::handleModStateEvent).acceptableRemoteVersions("*").guiFactory(se.gory_moon.globalgamerules.config.GGRGuiFactory::new));
        mods.add(new FMLModContainer<>("astikorcarts", "astikorcarts", "1.12.2-0.1.2.8", "", "[1.12,1.13)", "astikorcarts", de.mennomax.astikorcarts.AstikorCarts::new, de.mennomax.astikorcarts.AstikorCarts::handleModStateEvent));
        mods.add(new FMLModContainer<>("xpfromharvest", "XP From Harvest", "1.2.0", "", "", "xpfromharvest", com.realgecko.xpfromharvest.XPFromHarvest::new, com.realgecko.xpfromharvest.XPFromHarvest::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("realistictorches", "Realistic Torches", "2.1.2", "", "[1.12.2]", "realistictorches", com.chaosthedude.realistictorches.RealisticTorches::new, com.chaosthedude.realistictorches.RealisticTorches::handleModStateEvent));
        mods.add(new FMLModContainer<>("xpbook", "XP Tome", "v1.1.6", "", "[1.12.2]", "xptome-1.12.2-v1.1.6", bl4ckscor3.mod.xptome.XPTome::new, bl4ckscor3.mod.xptome.XPTome::handleModStateEvent));
        mods.add(new FMLModContainer<>("sit", "Sit", "v1.3", "", "[1.12.2]", "sit-1.12.2-v1.3", bl4ckscor3.mod.sit.Sit::new, bl4ckscor3.mod.sit.Sit::handleModStateEvent));
        mods.add(new FMLModContainer<>("friendlyendermite", "Friendly Endermite", "1.0", "", "[1.12.2]", "friendlyendermite", com.creativemd.friendlyendermite.FriendlyEndermite::new, com.creativemd.friendlyendermite.FriendlyEndermite::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("helpfixer", "HelpFixer", "", "", "[1.12.1]", "helpfixer", com.matthewprenger.helpfixer.HelpFixer::new, com.matthewprenger.helpfixer.HelpFixer::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("foodexpansion", "Food Expansion", "1.3", "", "[1.12.2]", "fe", lellson.foodexpansion.FoodExpansion::new, lellson.foodexpansion.FoodExpansion::handleModStateEvent));
        mods.add(new FMLModContainer<>("attributefix", "Attribute Fix", "1.0.12", "", "", "attributefix", net.darkhax.attributefix.AttributeFix::new, net.darkhax.attributefix.AttributeFix::handleModStateEvent));
        mods.add(new FMLModContainer<>("enchdesc", "Enchantment Descriptions", "1.1.20", "", "[1.12.2]", "enchdesc", net.darkhax.enchdesc.EnchantmentDescriptions::new, net.darkhax.enchdesc.EnchantmentDescriptions::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("roughtweaks", "Rough Tweaks", "1.0", "", "[1.12.2]", "roughtweaks", lellson.roughTweaks.RoughTweaks::new, lellson.roughTweaks.RoughTweaks::handleModStateEvent));
        mods.add(new FMLModContainer<>("alcatrazcore", "Alcatraz Core", "1.0.4", com.alcatrazescapee.alcatrazcore.AlcatrazCore.DEPENDENCIES, "[1.12.2]", "alcatrazcore", com.alcatrazescapee.alcatrazcore.AlcatrazCore::new, com.alcatrazescapee.alcatrazcore.AlcatrazCore::handleModStateEvent).useMetadata(true));
        mods.add(new FMLModContainer<>("notreepunching", "No Tree Punching", "2.0.21", com.alcatrazescapee.notreepunching.NoTreePunching.DEPENDENCIES, "[1.12.2]", "notreepunching", com.alcatrazescapee.notreepunching.NoTreePunching::new, com.alcatrazescapee.notreepunching.NoTreePunching::handleModStateEvent).useMetadata(true));
        mods.add(new FMLModContainer<>("toastcontrol", "Toast Control", "1.8.1", shadows.toaster.ToastControl.DEPS, "[1.12.2]", "toastcontrol", shadows.toaster.ToastControl::new, shadows.toaster.ToastControl::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("tumbleweed", "Tumbleweed", "1.12-0.4.7", "", "[1.11,1.12.2]", "tumbleweed", net.konwboy.tumbleweed.Tumbleweed::new, net.konwboy.tumbleweed.Tumbleweed::handleModStateEvent).useMetadata(true));
        mods.add(new FMLModContainer<>("blockoverlayfix", "", "", "", "", "blockoverlayfix", meldexun.blockoverlayfix.BlockOverlayFix::new, meldexun.blockoverlayfix.BlockOverlayFix::handleModStateEvent));
        mods.add(new FMLModContainer<>("potionfingers", "Potion Fingers", "r1.0-8", vazkii.potionfingers.PotionFingers.DEPENDENCIES, "[1.12.2]", "potionfingers", vazkii.potionfingers.PotionFingers::new, vazkii.potionfingers.PotionFingers::handleModStateEvent));
        mods.add(new FMLModContainer<>("chunkanimator", "Chunk Animator", "1.12.2-1.2", "", "", "chunkanimator", lumien.chunkanimator.ChunkAnimator::new, lumien.chunkanimator.ChunkAnimator::handleModStateEvent).clientSideOnly().guiFactory(lumien.chunkanimator.config.ChunkAnimatorGuiFactory::new));
        mods.add(new FMLModContainer<>("resourceloader", "Resource Loader", "1.5.3", "", "[1.12,1.13)", "resourceloader", lumien.resourceloader.ResourceLoader::new, lumien.resourceloader.ResourceLoader::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("bnbgaminglib", "BNBGamingLib", "2.17.6", "after:bnbgamingcore", "[1.12.2]", "bnbgaminglib", com.bloodnbonesgaming.lib.BNBGamingLib::new, com.bloodnbonesgaming.lib.BNBGamingLib::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("custommainmenu", "Custom Main Menu", "2.0.9.1", "", "[1.12.2]", "custommainmenu", lumien.custommainmenu.CustomMainMenu::new, lumien.custommainmenu.CustomMainMenu::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("trumpetskeleton", "Trumpet Skeleton", "1.12-1.0.2.1", "required-after:forge@[14.21.1.2387,)", "[1.12,1.13)", "trumpetskeleton", com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton::new, com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton::handleModStateEvent));
        mods.add(new FMLModContainer<>("playerbosses", "", "", "", "[1.12.2]", "examplemod", com.lothrazar.playerbosses.ModBosses::new, com.lothrazar.playerbosses.ModBosses::handleModStateEvent));
        mods.add(new FMLModContainer<>("legendarytooltips", "Legendary Tooltips", "1.1.9", "", "[1.12.2]", "legendarytooltips", com.anthonyhilyard.legendarytooltips.LegendaryTooltips::new, com.anthonyhilyard.legendarytooltips.LegendaryTooltips::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("controlling", "Controlling", "3.0.10", "", "[1.12.2]", "controlling", us.getfluxed.controlsearch.Control::new, us.getfluxed.controlsearch.Control::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("testdummy", "MmmMmmMmmMmm", "1.12", "", "[1.12,1.13)", "testdummy", boni.dummy.TestDummyMod::new, boni.dummy.TestDummyMod::handleModStateEvent));
        mods.add(new FMLModContainer<>("mcwbridges", "Mcwbridges", "1.0.6", "", "", "mcwbridges", com.mcwbridges.kikoz.Mcwbridges::new, com.mcwbridges.kikoz.Mcwbridges::handleModStateEvent));
        mods.add(new FMLModContainer<>("spawnercontrol", "Mob Spawner Control", "1.6.3b", "required-after:forge@[14.23.2.2596,);", "[1.12, 1.13)", "spawnercontrol", ladysnake.spawnercontrol.SpawnerControl::new, ladysnake.spawnercontrol.SpawnerControl::handleModStateEvent).acceptableRemoteVersions("*").guiFactory(ladysnake.spawnercontrol.client.MSCConfigFactory::new));
        mods.add(new FMLModContainer<>("surge", "Surge", "2.0.79", "", "[1.12.2]", "surge", net.darkhax.surge.Surge::new, net.darkhax.surge.Surge::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("tnt_utilities", "TNTUtils", "1.2.3", "", "[1.12,1.13)", "tnt_utilities", ljfa.tntutils.TNTUtils::new, ljfa.tntutils.TNTUtils::handleModStateEvent).acceptableRemoteVersions("*").guiFactory(ljfa.tntutils.gui.TntuGuiFactory::new));
        mods.add(new FMLModContainer<>("waystones", "Waystones", "", "", "[1.12]", "waystones", net.blay09.mods.waystones.Waystones::new, net.blay09.mods.waystones.Waystones::handleModStateEvent));
        mods.add(new FMLModContainer<>("sereneseasons", "Serene Seasons", "1.2.18", "required-after:forge@[14.23.5.2768,)", "[1.12.2]", "sereneseasons", sereneseasons.core.SereneSeasons::new, sereneseasons.core.SereneSeasons::handleModStateEvent));
        mods.add(new FMLModContainer<>("toolbelt", "toolbelt", "1.9.14", "after:baubles;after:jei@[4.6,)", "[1.12.0,1.13.0)", "toolbelt", gigaherz.toolbelt.ToolBelt::new, gigaherz.toolbelt.ToolBelt::handleModStateEvent).guiFactory(gigaherz.toolbelt.client.config.ConfigGuiFactory::new));
        mods.add(new FMLModContainer<>("coralreef", "CoralReef", "2.0", "", "[1.12, 1.13)", "coralreef", primetoxinz.coralreef.CoralReef::new, primetoxinz.coralreef.CoralReef::handleModStateEvent));
        mods.add(new FMLModContainer<>("multimine", "Multi Mine", "1.6.2", "", "", "AS_MultiMine", atomicstryker.multimine.common.MultiMine::new, atomicstryker.multimine.common.MultiMine::handleModStateEvent));
        mods.add(new FMLModContainer<>("battletowers", "Battle Towers", "1.6.5", "", "", "battletowers", atomicstryker.battletowers.common.AS_BattleTowersCore::new, atomicstryker.battletowers.common.AS_BattleTowersCore::handleModStateEvent));
        mods.add(new FMLModContainer<>("infernalmobs", "Infernal Mobs", "1.7.6", "", "", "InfernalMobs", atomicstryker.infernalmobs.common.InfernalMobsCore::new, atomicstryker.infernalmobs.common.InfernalMobsCore::handleModStateEvent));
        mods.add(new FMLModContainer<>("bloodmoon", "Bloodmoon", "1.5.3", "", "[1.12,1.13)", "bloodmoon", lumien.bloodmoon.Bloodmoon::new, lumien.bloodmoon.Bloodmoon::handleModStateEvent));
        mods.add(new FMLModContainer<>("advanced-fishing", "Advanced-Fishing", "1.3.3", "", "[1.12.2]", "advanced-fishing", nightkosh.advanced_fishing.ModAdvancedFishing::new, nightkosh.advanced_fishing.ModAdvancedFishing::handleModStateEvent));
        mods.add(new FMLModContainer<>("callablehorses", "Callable Horses", "1.1.1", "required-after:forge@[13.20.1.2386,);", "[1.12.2,1.13)", "callablehorses", tschipp.callablehorses.CallableHorses::new, tschipp.callablehorses.CallableHorses::handleModStateEvent).guiFactory(tschipp.callablehorses.client.gui.GuiFactoryCallableHorses::new));
        mods.add(new FMLModContainer<>("levelup2", "Level Up! Reloaded", "${version}", "", "[1.12]", "levelup2", levelup2.LevelUp2::new, levelup2.LevelUp2::handleModStateEvent));
        mods.add(new FMLModContainer<>("comforts", "Comforts", "1.4.1.3", "required-after:forge@[14.23.5.2776,);after:morpheus;after:toughasnails", "[1.12, 1.13)", "comforts", c4.comforts.Comforts::new, c4.comforts.Comforts::handleModStateEvent));
        mods.add(new FMLModContainer<>("aquaculture", "Aquaculture", "1.6.8", "", "[1.12.2]", "aquaculture", com.teammetallurgy.aquaculture.Aquaculture::new, com.teammetallurgy.aquaculture.Aquaculture::handleModStateEvent).guiFactory(com.teammetallurgy.aquaculture.client.GuiFactory::new));
        mods.add(new FMLModContainer<>("grapplemod", "grapplemod", "1.12.2-v12.3", "", "", "grapplemod", com.yyon.grapplinghook.grapplemod::new, com.yyon.grapplinghook.grapplemod::handleModStateEvent));
        mods.add(new FMLModContainer<>("betterbiomeblend", "betterbiomeblend", "1.0", "", "[1.12.2]", "betterbiomeblend", fionathemortal.betterbiomeblend.BetterBiomeBlend::new, fionathemortal.betterbiomeblend.BetterBiomeBlend::handleModStateEvent).clientSideOnly());
        mods.add(new FMLModContainer<>("potioncore", "Potion Core", "1.9_for_1.12.2", "required-after:forge@[14.23.0.2524,);", "[1.12.2]", "potioncore", com.tmtravlr.potioncore.PotionCore::new, com.tmtravlr.potioncore.PotionCore::handleModStateEvent));
        mods.add(new FMLModContainer<>("qualitytools", "Quality Tools", "1.0.7_for_1.12.2", "required-after:potioncore;after:baubles", "[1.12.2]", "qualitytools", com.tmtravlr.qualitytools.QualityToolsMod::new, com.tmtravlr.qualitytools.QualityToolsMod::handleModStateEvent));
        mods.add(new FMLModContainer<>("bettercaves", "YUNG's Better Caves", "1.12.2", "", "[1.12.2]", "bettercaves", com.yungnickyoung.minecraft.bettercaves.BetterCaves::new, com.yungnickyoung.minecraft.bettercaves.BetterCaves::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("spartanhudbaubles", "Spartan HUD: Baubles", "1.0.0 Indev 1", "required-after:baubles", "[1.12.2]", "spartanhudbaubles", com.oblivioussp.spartanhudbaubles.ModSpartanHUDBaubles::new, com.oblivioussp.spartanhudbaubles.ModSpartanHUDBaubles::handleModStateEvent).clientSideOnly().guiFactory(com.oblivioussp.spartanhudbaubles.client.gui.GuiFactorySHB::new));
        mods.add(new FMLModContainer<>("fishingmadebetter", "Fishing Made Better", "2.2.6", "after:advanced-fishing;after:aquaculture;after:levelup2", "", "fishingmadebetter", net.theawesomegem.fishingmadebetter.Primary::new, net.theawesomegem.fishingmadebetter.Primary::handleModStateEvent));
        mods.add(new FMLModContainer<>("dldungeonsjbg", "Doomlike Dungeons", "1.14.12", "", "[1.12]", "dldungeonsjbg", jaredbgreat.dldungeons.DoomlikeDungeons::new, jaredbgreat.dldungeons.DoomlikeDungeons::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("reskillable", "Reskillable", "1.12.2-1.13.0", "", "[1.12.2]", "reskillable", codersafterdark.reskillable.Reskillable::new, codersafterdark.reskillable.Reskillable::handleModStateEvent).guiFactory(codersafterdark.reskillable.client.gui.GuiFactory::new));
        mods.add(new FMLModContainer<>("silentlib", "Silent Lib", "3.0.13", "required-after:forge@[14.23.3.2669,);", "[1.12.2]", "silentlib", net.silentchaos512.lib.SilentLib::new, net.silentchaos512.lib.SilentLib::handleModStateEvent));
        mods.add(new FMLModContainer<>("scalinghealth", "Scaling Health", "1.3.37", "required-after:forge@[14.23.5.2779,);required-after:silentlib@[3.0.13,);after:morpheus", "[1.12.2]", "scalinghealth", net.silentchaos512.scalinghealth.ScalingHealth::new, net.silentchaos512.scalinghealth.ScalingHealth::handleModStateEvent).guiFactory(net.silentchaos512.scalinghealth.gui.GuiFactoryScalingHealth::new));
        mods.add(new FMLModContainer<>("switchbow", "switchbow", "1.6.8", "", "[1.12.2]", "switchbow", de.Whitedraco.switchbow.Initial::new, de.Whitedraco.switchbow.Initial::handleModStateEvent));
        mods.add(new FMLModContainer<>("lostcities", "The Lost Cities", "2.0.22", "after:forge@[13.19.0.2176,)", "[1.12,1.13)", "lostcities", mcjty.lostcities.LostCities::new, mcjty.lostcities.LostCities::handleModStateEvent).acceptableRemoteVersions("*"));
        mods.add(new FMLModContainer<>("traverse", "Traverse", "1.6.0", "", "1.12.2", "traverse", prospector.traverse.core.TraverseMod::new, prospector.traverse.core.TraverseMod::handleModStateEvent));
        mods.add(new FMLModContainer<>("carrots", "Carrots Lib", "1.0.0b1", "required-after:forge@[14.23.4.2705,)", "[1.12.2,)", "carrots", org.jwaresoftware.mcmods.lib.CarrotsLib::new, org.jwaresoftware.mcmods.lib.CarrotsLib::handleModStateEvent));
        mods.add(new FMLModContainer<>("armorunder", "Armor Underwear", "1.0.0", "required-after:forge@[14.23.4.2705,);required-after:carrots@[1.0.0b1]", "[1.12,)", "armorunder", org.jwaresoftware.mcmods.armorunder.ArmorUnderwear::new, org.jwaresoftware.mcmods.armorunder.ArmorUnderwear::handleModStateEvent));
        namedMods = Maps.uniqueIndex(mods, ModContainer::getModId);
        for (int i = 0, l = mods.size(); i < l; ++i)
        {
            ModContainer mod = mods.get(i);
            if (mod instanceof FMLModContainer)
            {
                ((FMLModContainer<?>)mod).bindMetadata();
            }
        }
        FMLLog.log.info("Forge Mod Loader has identified {} mod{} to load", mods.size(), mods.size() != 1 ? "s" : "");
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
        com.arlenh7.eaglercraft.forge.PackDefaults.install();
        mods = Lists.newArrayList();
        namedMods = Maps.newHashMap();
        modController = new LoadController(this);
        modController.transition(LoaderState.LOADING, false);
        identifyMods();
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
