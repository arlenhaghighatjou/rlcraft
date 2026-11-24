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

package net.minecraftforge.fml.common.network.handshake;

import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import net.lax1dude.eaglercraft.v1_8.sp.server.socket.IntegratedServerPlayerNetworkManager;
import com.arlenh7.eaglercraft.netty.EmbeddedChannel;
import com.arlenh7.eaglercraft.netty.AttributeKey;
import com.arlenh7.eaglercraft.network.INetworkManager;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;

public class NetworkDispatcher {
    private static enum ConnectionState {
        OPENING, AWAITING_HANDSHAKE, HANDSHAKING, HANDSHAKECOMPLETE, FINALIZING, CONNECTED
    }

    public static enum ConnectionType {
        MODDED, BUKKIT, VANILLA
    }

    public static NetworkDispatcher get(INetworkManager manager)
    {
        return manager.getDispatcher();
    }

    public static NetworkDispatcher allocAndSet(INetworkManager manager)
    {
        NetworkDispatcher net = new NetworkDispatcher(manager);
        manager.setDispatcher(net);
        return net;
    }

    public static NetworkDispatcher allocAndSet(INetworkManager manager, PlayerList scm)
    {
        NetworkDispatcher net = new NetworkDispatcher(manager, scm);
        manager.setDispatcher(net);
        return net;
    }

    public static final AttributeKey<NetworkDispatcher> FML_DISPATCHER = AttributeKey.valueOf("fml:dispatcher");
    public static final AttributeKey<Boolean> IS_LOCAL = AttributeKey.valueOf("fml:isLocal");
    public static final AttributeKey<Map<ResourceLocation, ForgeRegistry.Snapshot>> FML_GAMEDATA_SNAPSHOT = AttributeKey.valueOf("fml:gameDataSnapshot");
    public final INetworkManager manager;
    private final PlayerList scm;
    private EntityPlayerMP player;
    private ConnectionState state;
    private ConnectionType connectionType;
    private final Side side;
    private final EmbeddedChannel handshakeChannel;
    private NetHandlerPlayServer serverHandler;
    private INetHandler netHandler;
    private Map<String,String> modList = Collections.emptyMap();
    private int overrideLoginDim;

    public NetworkDispatcher(INetworkManager manager)
    {
        this.manager = manager;
        this.scm = null;
        this.side = Side.CLIENT;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new ChannelRegistrationHandler(), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeClientState>(FMLHandshakeClientState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.CHANNEL_SOURCE).set(Side.SERVER);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
        this.handshakeChannel.attr(IS_LOCAL).set(manager.isLocalChannel());
    }

    public NetworkDispatcher(INetworkManager manager, PlayerList scm)
    {
        this.manager = manager;
        this.scm = scm;
        this.side = Side.SERVER;
        this.handshakeChannel = new EmbeddedChannel(new HandshakeInjector(this), new ChannelRegistrationHandler(), new FMLHandshakeCodec(), new HandshakeMessageHandler<FMLHandshakeServerState>(FMLHandshakeServerState.class));
        this.handshakeChannel.attr(FML_DISPATCHER).set(this);
        this.handshakeChannel.attr(NetworkRegistry.CHANNEL_SOURCE).set(Side.CLIENT);
        this.handshakeChannel.attr(NetworkRegistry.FML_CHANNEL).set("FML|HS");
        this.handshakeChannel.attr(IS_LOCAL).set(manager.isLocalChannel());
    }

    public void serverToClientHandshake(EntityPlayerMP player)
    {
        this.player = player;
        if (manager.hasFMLMarker())
        {
            insertIntoChannel();
        }
        else
        {
            serverInitiateHandshake();
            FMLLog.log.info("Connection received without FML marker, assuming vanilla.");
            insertIntoChannel();
            this.completeServerSideConnection(ConnectionType.VANILLA);
        }
    }

    protected void setModList(Map<String,String> modList)
    {
        this.modList = modList;
    }

    private void insertIntoChannel()
    {
        if (this.state != null) {
            FMLLog.log.info("Opening channel which already seems to have a state set. This is a vanilla connection. Handshake handler will stop now");
            return;
        }
        FMLLog.log.trace("Handshake channel activating");
        this.state = ConnectionState.OPENING;
        this.handshakeChannel.pipeline().fireUserEventTriggered(this);
    }

    public void clientToServerHandshake()
    {
        insertIntoChannel();
    }

    int serverInitiateHandshake()
    {
        this.state = ConnectionState.AWAITING_HANDSHAKE;
        serverHandler = new NetHandlerPlayServer(scm.getServerInstance(), (IntegratedServerPlayerNetworkManager)manager, player)
        {
            @Override
            public void update()
            {
                if (NetworkDispatcher.this.state == ConnectionState.FINALIZING)
                {
                    completeServerSideConnection(ConnectionType.MODDED);
                }
                if (this.player.connection != this) return;
                super.update();
            }
        };
        this.netHandler = serverHandler;
        player.connection = null;
        this.manager.setConnectionState(EnumConnectionState.PLAY);

        NBTTagCompound playerNBT = scm.getPlayerNBT(player);
        if (playerNBT!=null)
        {
            int dimension = playerNBT.getInteger("Dimension");
            if (DimensionManager.isDimensionRegistered(dimension))
            {
                return dimension;
            }
        }
        return 0;
    }

    void clientListenForServerHandshake()
    {
        manager.setConnectionState(EnumConnectionState.PLAY);
        this.netHandler = FMLCommonHandler.instance().getClientPlayHandler();
        this.state = ConnectionState.AWAITING_HANDSHAKE;
    }

    private void completeClientSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.log.info("Client side {} connection established", this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientConnectedToServerEvent(manager, this.connectionType.name()));
    }

    private void completeServerSideConnection(ConnectionType type)
    {
        this.connectionType = type;
        FMLLog.log.info("Server side {} connection established", this.connectionType.name().toLowerCase(Locale.ENGLISH));
        this.state = ConnectionState.CONNECTED;
        scm.initializeConnectionToPlayer((IntegratedServerPlayerNetworkManager)manager, player, serverHandler);
    }

    public Packet<?> handleInbound(Packet<?> msg)
    {
        if (msg instanceof CPacketCustomPayload)
        {
            return handleServerSideCustomPacket((CPacketCustomPayload) msg);
        }
        else if (msg instanceof SPacketCustomPayload)
        {
            return handleClientSideCustomPacket((SPacketCustomPayload)msg);
        }
        else if (state != ConnectionState.CONNECTED && state != ConnectionState.HANDSHAKECOMPLETE)
        {
            handleVanilla(msg);
        }
        return msg;
    }

    private void handleVanilla(Packet<?> msg)
    {
        if (state == ConnectionState.AWAITING_HANDSHAKE && msg instanceof SPacketJoinGame)
        {
            handshakeChannel.pipeline().fireUserEventTriggered(msg);
        }
        else
        {
            FMLLog.log.info("Unexpected packet during modded negotiation - assuming vanilla or keepalives : {}", msg.getClass().getName());
        }
    }

    public INetHandler getNetHandler()
    {
        return netHandler;
    }

    public Map<String,String> getModList()
    {
        return Collections.unmodifiableMap(modList);
    }

    private void kickWithMessage(String message)
    {
        FMLLog.log.error("Network Disconnect: {}", message);
        final TextComponentString textComponent = new TextComponentString(message);
        if (side == Side.SERVER)
        {
            manager.sendPacket(new SPacketDisconnect(textComponent));
        }
        manager.closeChannel(textComponent);
    }

    private MultiPartCustomPayload multipart = null;

    private Packet<?> handleClientSideCustomPacket(SPacketCustomPayload msg)
    {
        String channelName = msg.getChannelName();
        if ("FML|MP".equals(channelName))
        {
            return handleMultiPartCustomPacket(msg);
        }
        if ("FML|HS".equals(channelName) || "REGISTER".equals(channelName) || "UNREGISTER".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            forwardHandshake(proxy, Side.CLIENT);
            return null;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.CLIENT))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            return proxy;
        }
        return msg;
    }

    private Packet<?> handleMultiPartCustomPacket(SPacketCustomPayload msg)
    {
        try
        {
            if (multipart == null)
            {
                multipart = new MultiPartCustomPayload(msg.getBufferData());
            }
            else
            {
                multipart.processPart(msg.getBufferData());
            }
        }
        catch (IOException e)
        {
            this.kickWithMessage(e.getMessage());
            multipart = null;
            return null;
        }

        if (multipart.isComplete())
        {
            Packet<?> result = handleClientSideCustomPacket(multipart);
            multipart = null;
            return result;
        }
        return null;
    }

    private Packet<?> handleServerSideCustomPacket(CPacketCustomPayload msg)
    {
        if (state == ConnectionState.AWAITING_HANDSHAKE)
        {
            state = ConnectionState.HANDSHAKING;
        }
        String channelName = msg.getChannelName();
        if ("FML|HS".equals(channelName) || "REGISTER".equals(channelName) || "UNREGISTER".equals(channelName))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            forwardHandshake(proxy, Side.SERVER);
            return null;
        }
        else if (NetworkRegistry.INSTANCE.hasChannel(channelName, Side.SERVER))
        {
            FMLProxyPacket proxy = new FMLProxyPacket(msg);
            proxy.setDispatcher(this);
            return proxy;
        }
        return msg;
    }

    private void forwardHandshake(FMLProxyPacket proxy, Side target)
    {
        handshakeChannel.writeInbound(proxy);
        INetHandler handler = manager.getNetHandler();
        for (Object push : handshakeChannel.inboundMessages())
        {
            List<FMLProxyPacket> messageResult = FMLNetworkHandler.forwardHandshake((FMLMessage.CompleteHandshake)push, this, target);
            for (int i = 0, l = messageResult.size(); i < l; ++i)
            {
                FMLProxyPacket result = messageResult.get(i);
                result.setTarget(target);
                result.payload().resetReaderIndex();
                result.processPacket(handler);
            }
        }
        handshakeChannel.inboundMessages().clear();
    }

    public void sendProxy(FMLProxyPacket msg)
    {
        if (side == Side.CLIENT)
        {
            manager.sendPacket(msg.toC17Packet());
        }
        else
        {
			List<Packet<INetHandlerPlayClient>> parts;
			try
			{
				parts = msg.toS3FPackets();
			}
			catch (IOException ex)
			{
				throw new java.io.UncheckedIOException("Could not encode Forge packet " + msg.channel(), ex);
			}
            for (int i = 0, l = parts.size(); i < l; ++i)
            {
                manager.sendPacket(parts.get(i));
            }
        }
    }

    public void rejectHandshake(String result)
    {
        kickWithMessage(result);
    }

    public void onClose()
    {
        if (side == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ClientDisconnectionFromServerEvent(manager));
        }
        else
        {
            MinecraftForge.EVENT_BUS.post(new FMLNetworkEvent.ServerDisconnectionFromClientEvent(manager));
        }
        this.handshakeChannel.attr(FML_DISPATCHER).set(null);
        this.manager.setDispatcher(null);
        NetworkRegistry.INSTANCE.cleanAttributes();
    }

    public void completeHandshake(Side target)
    {
        if (state == ConnectionState.CONNECTED)
        {
            FMLLog.log.fatal("Attempt to double complete the network connection!");
            throw new FMLNetworkException("Attempt to double complete!");
        }
        if (side == Side.CLIENT)
        {
            completeClientSideConnection(ConnectionType.MODDED);
        }
        else
        {
            this.state = ConnectionState.FINALIZING; //Delay and finalize in the world tick loop.
        }
    }

    public void completeClientHandshake()
    {
        state = ConnectionState.HANDSHAKECOMPLETE;
    }

    public void abortClientHandshake(String type)
    {
        FMLLog.log.info("Aborting client handshake \"{}\"", type);
        completeClientSideConnection(ConnectionType.valueOf(type));
    }

    public void setOverrideDimension(int overrideDim) {
        this.overrideLoginDim = overrideDim;
        FMLLog.log.debug("Received override dimension {}", overrideDim);
    }

    public int getOverrideDimension(SPacketJoinGame packetIn) {
        FMLLog.log.debug("Overriding dimension: using {}", this.overrideLoginDim);
        return this.overrideLoginDim != 0 ? this.overrideLoginDim : packetIn.getDimension();
    }

    private class MultiPartCustomPayload extends SPacketCustomPayload
    {
        private String channel;
        private byte[] data;
        private PacketBuffer data_buf = null;
        private int part_count = 0;
        private int part_expected = 0;
        private int offset = 0;

        private MultiPartCustomPayload(PacketBuffer preamble) throws IOException
        {
            channel = preamble.readString(20);
            part_count = preamble.readUnsignedByte();
            int length = preamble.readInt();
            if (length <= 0 || length >= FMLProxyPacket.MAX_LENGTH)
            {
                throw new IOException("The received FML MultiPart packet outside of valid length bounds, Max: " + FMLProxyPacket.MAX_LENGTH + ", Received: " + length);
            }
            data = new byte[length];
            data_buf = new PacketBuffer(Unpooled.buffer(data, data.length).writerIndex(data.length));
        }

        public void processPart(PacketBuffer input) throws IOException
        {
            int part = input.readByte() & 0xFF;
            if (part != part_expected)
            {
                throw new IOException("Received FML MultiPart packet out of order, Expected " + part_expected + " Got " + part);
            }
            int len = input.readableBytes();
            input.readBytes(data, offset, len);
            part_expected++;
            offset += len;
        }

        public boolean isComplete()
        {
            return part_expected == part_count;
        }

        @Override
        public String getChannelName()
        {
            return this.channel;
        }

        @Override
        public PacketBuffer getBufferData()
        {
            return this.data_buf;
        }
    }

    public ConnectionType getConnectionType()
    {
        return this.connectionType;
    }
}
