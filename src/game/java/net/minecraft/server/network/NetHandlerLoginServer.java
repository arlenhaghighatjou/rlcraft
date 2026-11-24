package net.minecraft.server.network;

import java.nio.charset.StandardCharsets;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.lang3.Validate;

import com.arlenh7.eaglercraft.network.INetworkManager;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.ClientUUIDLoadingCache;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.sp.server.skins.IntegratedTexturePackets;
import net.lax1dude.eaglercraft.v1_8.sp.server.skins.PlayerTextureData;
import net.lax1dude.eaglercraft.v1_8.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable
{
	private static final Logger LOGGER = LogManager.getLogger("NetHandlerLoginServer");
	private final MinecraftServer server;
	public final INetworkManager networkManager;
	private LoginState state = LoginState.HELLO;
	private int connectionTimer;
	private GameProfile loginGameProfile;
	private EntityPlayerMP player;
	private PlayerTextureData textureData;
	private EaglercraftUUID clientBrand;
	private int selectedProtocol = 3;

	public NetHandlerLoginServer(MinecraftServer server, INetworkManager networkManager)
	{
		this.server = server;
		this.networkManager = networkManager;
	}

	public void update()
	{
		if (state == LoginState.READY_TO_ACCEPT) tryAcceptPlayer();
		else if (state == LoginState.DELAY_ACCEPT && server.getPlayerList().getPlayerByUUID(loginGameProfile.getId()) == null)
		{
			state = LoginState.ACCEPTED;
			FMLNetworkHandler.fmlServerHandshake(server.getPlayerList(), networkManager, player);
			player = null;
		}
		if (connectionTimer++ == 600) disconnect(new TextComponentTranslation("multiplayer.disconnect.slow_login"));
	}

	public void disconnect(ITextComponent reason)
	{
		LOGGER.info("Disconnecting {}: {}", getConnectionInfo(), reason.getUnformattedText());
		networkManager.sendPacket(new SPacketDisconnect(reason));
		networkManager.closeChannel(reason);
	}

	public void tryAcceptPlayer()
	{
		if (!loginGameProfile.isComplete()) loginGameProfile = getOfflineProfile(loginGameProfile);
		String denial = server.getPlayerList().allowUserToConnect(networkManager.getRemoteAddress(), loginGameProfile);
		if (denial != null)
		{
			disconnect(new TextComponentTranslation(denial));
			return;
		}
		state = LoginState.ACCEPTED;
		boolean integrated = networkManager instanceof IntegratedServerPlayerNetworkManager;
		networkManager.sendPacket(integrated ? new SPacketLoginSuccess(loginGameProfile, selectedProtocol)
				: new SPacketLoginSuccess(loginGameProfile));
		networkManager.setConnectionState(EnumConnectionState.PLAY);
		EntityPlayerMP existing = server.getPlayerList().getPlayerByUUID(loginGameProfile.getId());
		player = server.getPlayerList().createPlayerForUser(loginGameProfile);
		player.textureData = textureData;
		player.clientBrandUUID = clientBrand;
		if (existing != null) state = LoginState.DELAY_ACCEPT;
		else
		{
			FMLNetworkHandler.fmlServerHandshake(server.getPlayerList(), networkManager, player);
			player = null;
		}
	}

	public void onDisconnect(ITextComponent reason)
	{
		LOGGER.info("{} lost connection: {}", getConnectionInfo(), reason.getUnformattedText());
	}

	public String getConnectionInfo()
	{
		return loginGameProfile == null ? String.valueOf(networkManager.getRemoteAddress()) : loginGameProfile + " (" + networkManager.getRemoteAddress() + ")";
	}

	public void processLoginStart(CPacketLoginStart packet)
	{
		Validate.validState(state == LoginState.HELLO, "Unexpected hello packet");
		loginGameProfile = packet.getProfile();
		if (networkManager instanceof IntegratedServerPlayerNetworkManager)
		{
			byte[] protocols = packet.getProtocols();
			if (protocols != null)
			{
				try (DataInputStream input = new DataInputStream(new EaglerInputStream(protocols)))
				{
					int highest = 0;
					int count = input.readUnsignedShort();
					for (int i = 0; i < count; ++i)
					{
						int version = input.readUnsignedShort();
						if (version >= 3 && version <= 5) highest = Math.max(highest, version);
					}
					if (highest == 0)
					{
						disconnect(new net.minecraft.util.text.TextComponentString("Unknown protocol!"));
						return;
					}
					selectedProtocol = highest;
				}
				catch (IOException ex)
				{
					selectedProtocol = 3;
				}
			}
			((IntegratedServerPlayerNetworkManager)networkManager).setEaglerProtocolVersion(selectedProtocol);
			textureData = IntegratedTexturePackets.handleTextureData(packet.getSkin(), packet.getCape());
			clientBrand = selectedProtocol <= 3 ? EaglercraftVersion.legacyClientUUIDInSharedWorld : packet.getBrandUUID();
			if (ClientUUIDLoadingCache.PENDING_UUID.equals(clientBrand) || ClientUUIDLoadingCache.VANILLA_UUID.equals(clientBrand))
			{
				clientBrand = null;
			}
		}
		state = LoginState.READY_TO_ACCEPT;
	}

	public void processEncryptionResponse(CPacketEncryptionResponse packet)
	{
		disconnect(new TextComponentTranslation("multiplayer.disconnect.unverified_username"));
	}

	protected GameProfile getOfflineProfile(GameProfile original)
	{
		EaglercraftUUID uuid = EaglercraftUUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(StandardCharsets.UTF_8));
		return new GameProfile(uuid, original.getName());
	}

	private enum LoginState
	{
		HELLO, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED
	}
}
