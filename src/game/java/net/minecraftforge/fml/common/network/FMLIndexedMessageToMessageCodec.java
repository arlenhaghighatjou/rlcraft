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

package net.minecraftforge.fml.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import com.arlenh7.eaglercraft.netty.ChannelHandler.Sharable;
import com.arlenh7.eaglercraft.netty.ChannelHandlerContext;
import com.arlenh7.eaglercraft.netty.MessageToMessageCodec;
import com.arlenh7.eaglercraft.netty.AttributeKey;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

@Sharable
public abstract class FMLIndexedMessageToMessageCodec<A> extends MessageToMessageCodec<FMLProxyPacket, A>
{
    private final Byte2ObjectMap<Supplier<? extends A>> discriminators = new Byte2ObjectOpenHashMap<>();
    private final Object2ByteMap<Class<? extends A>> types = new Object2ByteOpenHashMap<>();

    /**
     * Make this accessible to subclasses
     */

    public static final AttributeKey<ThreadLocal<WeakReference<FMLProxyPacket>>> INBOUNDPACKETTRACKER = AttributeKey.valueOf("fml:inboundpacket");

    protected FMLIndexedMessageToMessageCodec(Class<? super A> messageType)
    {
        super(FMLProxyPacket.class, messageType);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception
    {
        ctx.channel().attr(INBOUNDPACKETTRACKER).set(new ThreadLocal<WeakReference<FMLProxyPacket>>());
    }
    public FMLIndexedMessageToMessageCodec<A> addDiscriminator(int discriminator, Class<? extends A> type, Supplier<? extends A> factory)
    {
        discriminators.put((byte)discriminator, factory);
        types.put(type, (byte)discriminator);
        return this;
    }

    public abstract void encodeInto(ChannelHandlerContext ctx, A msg, ByteBuf target) throws Exception;

    @Override
    protected final void encode(ChannelHandlerContext ctx, A msg, List<Object> out) throws Exception
    {
        String channel = ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get();
        Class<?> clazz = msg.getClass();
        if (!types.containsKey(clazz))
        {
            throw new RuntimeException("Undefined discriminator for message type " + clazz.getName() + " in channel " + channel);
        }
        byte discriminator = types.getByte(clazz);
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeByte(discriminator);
        encodeInto(ctx, msg, buffer);
        FMLProxyPacket proxy = new FMLProxyPacket(buffer, channel);
        WeakReference<FMLProxyPacket> ref = ctx.channel().attr(INBOUNDPACKETTRACKER).get().get();
        FMLProxyPacket old = ref == null ? null : ref.get();
        if (old != null)
        {
            proxy.setDispatcher(old.getDispatcher());
        }
        out.add(proxy);
    }

    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf source, A msg);

    @Override
    protected final void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception
    {
        testMessageValidity(msg);
        ByteBuf payload = msg.payload().duplicate();
        if (payload.readableBytes() < 1)
        {
            FMLLog.log.error("The FMLIndexedCodec has received an empty buffer on channel {}, likely a result of a LAN server issue. Pipeline parts : {}", ctx.channel().attr(NetworkRegistry.FML_CHANNEL), ctx.pipeline().toString());
        }
        byte discriminator = payload.readByte();
        Supplier<? extends A> factory = discriminators.get(discriminator);
        if(factory == null)
        {
            throw new NullPointerException("Undefined message for discriminator " + discriminator + " in channel " + msg.channel());
        }
        A newMsg = factory.get();
        ctx.channel().attr(INBOUNDPACKETTRACKER).get().set(new WeakReference<FMLProxyPacket>(msg));
        decodeInto(ctx, payload.slice(), newMsg);
        out.add(newMsg);
    }

    /**
     * Called to verify the message received. This can be used to hard disconnect in case of an unexpected packet,
     * say due to a weird protocol mismatch. Use with caution.
     * @param msg
     */
    protected void testMessageValidity(FMLProxyPacket msg)
    {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log.error("FMLIndexedMessageCodec exception caught", cause);
        super.exceptionCaught(ctx, cause);
    }
}
