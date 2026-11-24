package com.arlenh7.eaglercraft.forge;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.arlenh7.eaglercraft.network.INetworkManager;
import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.IFMLSidedHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class FMLServerHandler implements IFMLSidedHandler
{
	private final CompoundDataFixer dataFixer = (CompoundDataFixer)DataFixesManager.createFixer();

	public static void load()
	{
		if (Loader.instance().getLoaderState() != LoaderState.NOINIT) return;
		FMLCommonHandler.instance().beginLoading(new FMLServerHandler());
		Loader loader = Loader.instance();
		loader.loadMods();
		loader.preinitializeMods();
		loader.initializeMods();
		loader.loadingComplete();
	}

	@Override
	public List<String> getAdditionalBrandingInformation()
	{
		return ImmutableList.of();
	}

	@Override
	public Side getSide()
	{
		return Side.SERVER;
	}

	@Override
	public void haltGame(String message, Throwable exception)
	{
		throw new RuntimeException(message, exception);
	}

	@Override
	public void showGuiScreen(Object clientGuiElement)
	{
	}

	@Override
	public void beginServerLoading(MinecraftServer server)
	{
	}

	@Override
	public void finishServerLoading()
	{
	}

	@Override
	public File getSavesDirectory()
	{
		return null;
	}

	@Override
	public MinecraftServer getServer()
	{
		return EaglerIntegratedServerWorker.getServer();
	}

	@Override
	public boolean isDisplayCloseRequested()
	{
		return false;
	}

	@Override
	public boolean shouldServerShouldBeKilledQuietly()
	{
		return false;
	}

	@Override
	public void addModAsResource(ModContainer container)
	{
	}

	@Override
	public String getCurrentLanguage()
	{
		return "en_us";
	}

	@Override
	public void serverStopped()
	{
	}

	@Override
	public INetworkManager getClientToServerNetworkManager()
	{
		throw new UnsupportedOperationException("Unsupported on the server");
	}

	@Override
	public INetHandler getClientPlayHandler()
	{
		return null;
	}

	@Override
	public void fireNetRegistrationEvent(EventBus bus, INetworkManager manager, Set<String> channelSet, String channel, Side side)
	{
		bus.post(new FMLNetworkEvent.CustomPacketRegistrationEvent<>(manager, channelSet, channel, side, net.minecraft.network.NetHandlerPlayServer.class));
	}

	@Override
	public boolean shouldAllowPlayerLogins()
	{
		return true;
	}

	@Override
	public void allowLogins()
	{
	}

	@Override
	public IThreadListener getWorldThread(INetHandler net)
	{
		return getServer();
	}

	@Override
	public void processWindowMessages()
	{
	}

	@Override
	public String stripSpecialChars(String message)
	{
		return message;
	}

	@Override
	public void reloadRenderers()
	{
	}

	@Override
	public void fireSidedRegistryEvents()
	{
	}

	@Override
	public CompoundDataFixer getDataFixer()
	{
		return dataFixer;
	}

	@Override
	public boolean isDisplayVSyncForced()
	{
		return false;
	}
}
