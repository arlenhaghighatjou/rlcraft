package melonslise.locks.common.util;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.ThreadLocalRandom;
import java.io.IOException;
import java.util.HashSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksNetworks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class LocksUtil {
   public static final String KEY_X1 = "x1";
   public static final String KEY_Y1 = "y1";
   public static final String KEY_Z1 = "z1";
   public static final String KEY_X2 = "x2";
   public static final String KEY_Y2 = "y2";
   public static final String KEY_Z2 = "z2";
   public static final String KEY_ID = "id";
   public static final String KEY_LENGTH = "length";
   public static final String KEY_OLD_CODE = "code";
   public static final String KEY_OLD_COMBINATION = "combination";
   public static final String KEY_LOCKED = "locked";
   public static final String KEY_BOX = "box";
   public static final String KEY_LOCK = "lock";
   public static final String KEY_ORIENTATION = "orientation";
   public static final String KEY_OLD_SIDE = "side";
   public static final String KEY_STACK = "Stack";

   private LocksUtil() {
   }

   public static void shuffle(byte[] array, EaglercraftRandom rng) {
      for(int a = array.length - 1; a > 0; --a) {
         int index = rng.nextInt(a + 1);
         byte temp = array[index];
         array[index] = array[a];
         array[a] = temp;
      }

   }

   public static BlockPos getAdjacentChest(TileEntityChest te) {
      BlockPos pos = null;
      te.checkForAdjacentChests();
      if (te.adjacentChestXNeg != null) {
         pos = te.adjacentChestXNeg.getPos();
      } else if (te.adjacentChestXPos != null) {
         pos = te.adjacentChestXPos.getPos();
      } else if (te.adjacentChestZNeg != null) {
         pos = te.adjacentChestZNeg.getPos();
      } else if (te.adjacentChestZPos != null) {
         pos = te.adjacentChestZPos.getPos();
      }

      return pos;
   }

   public static AttachFace faceFromDir(EnumFacing dir) {
      return dir == EnumFacing.UP ? AttachFace.CEILING : (dir == EnumFacing.DOWN ? AttachFace.FLOOR : AttachFace.WALL);
   }

   public static Stream<Lockable> intersecting(World world, BlockPos pos) {
      return intersecting((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null), pos);
   }

   public static Stream<Lockable> intersecting(ILockableHandler handler, BlockPos pos) {
      return handler.getInChunk(pos).values().stream().filter((lkb) -> {
         return lkb.box.intersects(pos);
      });
   }

   public static boolean locked(World world, BlockPos pos) {
      return intersecting(world, pos).anyMatch(LocksPredicates.LOCKED);
   }

   public static NBTTagCompound getTag(ItemStack stack) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      return stack.getTagCompound();
   }

   public static boolean hasKey(ItemStack stack, String key) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key);
   }

   public static Cuboid6i readBoxFromNBT(NBTTagCompound nbt) {
      return new Cuboid6i(nbt.getInteger("x1"), nbt.getInteger("y1"), nbt.getInteger("z1"), nbt.getInteger("x2"), nbt.getInteger("y2"), nbt.getInteger("z2"));
   }

   public static NBTTagCompound writeBoxToNBT(Cuboid6i box) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("x1", box.x1);
      nbt.setInteger("y1", box.y1);
      nbt.setInteger("z1", box.z1);
      nbt.setInteger("x2", box.x2);
      nbt.setInteger("y2", box.y2);
      nbt.setInteger("z2", box.z2);
      return nbt;
   }

   public static Lock readLockFromNBT(NBTTagCompound nbt) {
      int id = nbt.hasUniqueId("id") ? ThreadLocalRandom.current().nextInt() : nbt.getInteger("id");
      int length = nbt.hasKey("length") ? nbt.getByte("length") : (nbt.hasKey("code") ? nbt.getByteArray("code").length : nbt.getByteArray("combination").length);
      return new Lock(id, length, nbt.getBoolean("locked"));
   }

   public static NBTTagCompound writeLockToNBT(Lock lock) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setInteger("id", lock.id);
      nbt.setByte("length", (byte)lock.combination.length);
      nbt.setBoolean("locked", lock.locked);
      return nbt;
   }

   public static Lockable readLockableFromNBT(NBTTagCompound nbt) {
      Orientation orient = nbt.hasKey("orientation") ? Orientation.values()[nbt.getByte("orientation")] : Orientation.fromDirection(EnumFacing.getFront(nbt.getByte("side")), EnumFacing.NORTH);
      return new Lockable(readBoxFromNBT(nbt.getCompoundTag("box")), readLockFromNBT(nbt.getCompoundTag("lock")), orient, new ItemStack(nbt.getCompoundTag("Stack")), nbt.getInteger("id"));
   }

   public static NBTTagCompound writeLockableToNBT(Lockable lockable) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setTag("box", writeBoxToNBT(lockable.box));
      nbt.setTag("lock", writeLockToNBT(lockable.lock));
      nbt.setByte("orientation", (byte)lockable.orient.ordinal());
      nbt.setTag("Stack", lockable.stack.serializeNBT());
      nbt.setInteger("id", lockable.networkID);
      return nbt;
   }

   public static <T extends Enum<T>> T readEnumFromBuffer(ByteBuf buffer, Class<T> c) {
      return (T)((Enum[])((Enum[])c.getEnumConstants()))[buffer.readByte()];
   }

   public static void writeEnumToBuffer(ByteBuf buffer, Enum<?> value) {
      buffer.writeByte((byte)value.ordinal());
   }

   public static Cuboid6i readBoxFromBuffer(ByteBuf buf) {
      return new Cuboid6i(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
   }

   public static void writeBoxToBuffer(ByteBuf buf, Cuboid6i box) {
      buf.writeInt(box.x1);
      buf.writeInt(box.y1);
      buf.writeInt(box.z1);
      buf.writeInt(box.x2);
      buf.writeInt(box.y2);
      buf.writeInt(box.z2);
   }

   public static Lock readLockFromBuffer(ByteBuf buf) {
      return new Lock(buf.readInt(), buf.readByte(), buf.readBoolean());
   }

   public static void writeLockToBuffer(ByteBuf buf, Lock lock) {
      buf.writeInt(lock.id);
      buf.writeByte(lock.getLength());
      buf.writeBoolean(lock.isLocked());
   }

   public static ItemStack readItemStackFromBuffer(ByteBuf buf) {
      try {
         return (new PacketBuffer(buf)).readItemStack();
      } catch (IOException var2) {
         var2.printStackTrace();
         return ItemStack.EMPTY.copy();
      }
   }

   public static void writeItemStackToBuffer(ByteBuf buf, ItemStack stack) {
      (new PacketBuffer(buf)).writeItemStack(stack);
   }

   public static Lockable readLockableFromBuffer(ByteBuf buf) {
      return new Lockable(readBoxFromBuffer(buf), readLockFromBuffer(buf), (Orientation)readEnumFromBuffer(buf, Orientation.class), readItemStackFromBuffer(buf), buf.readInt());
   }

   public static void writeLockableToBuffer(ByteBuf buf, Lockable lockable) {
      writeBoxToBuffer(buf, lockable.box);
      writeLockToBuffer(buf, lockable.lock);
      writeEnumToBuffer(buf, lockable.orient);
      writeItemStackToBuffer(buf, lockable.stack);
      buf.writeInt(lockable.networkID);
   }

   public static AxisAlignedBB rotateY(AxisAlignedBB bb) {
      return new AxisAlignedBB(bb.minZ, bb.minY, bb.minX, bb.maxZ, bb.maxY, bb.maxX);
   }

   public static AxisAlignedBB rotateX(AxisAlignedBB bb) {
      return new AxisAlignedBB(bb.minX, bb.minZ, bb.minY, bb.maxX, bb.maxZ, bb.maxY);
   }

   public static boolean intersectsInclusive(AxisAlignedBB box1, AxisAlignedBB box2) {
      return box1.minX <= box2.maxX && box1.maxX >= box2.minX && box1.minY <= box2.maxY && box1.maxY >= box2.minY && box1.minZ <= box2.maxZ && box1.maxZ >= box2.minZ;
   }

   public static Vec3d getAABBSideCenter(AxisAlignedBB box, EnumFacing side) {
      switch(side) {
      case DOWN:
         return new Vec3d((box.minX + box.maxX) / 2.0D, box.minY, (box.minZ + box.maxZ) / 2.0D);
      case UP:
         return new Vec3d((box.minX + box.maxX) / 2.0D, box.maxY, (box.minZ + box.maxZ) / 2.0D);
      case NORTH:
         return new Vec3d((box.minX + box.maxX) / 2.0D, (box.minY + box.maxY) / 2.0D, box.minZ);
      case SOUTH:
         return new Vec3d((box.minX + box.maxX) / 2.0D, (box.minY + box.maxY) / 2.0D, box.maxZ);
      case WEST:
         return new Vec3d(box.minX, (box.minY + box.maxY) / 2.0D, (box.minZ + box.maxZ) / 2.0D);
      case EAST:
         return new Vec3d(box.maxX, (box.minY + box.maxY) / 2.0D, (box.minZ + box.maxZ) / 2.0D);
      default:
         return null;
      }
   }

   public static boolean chance(EaglercraftRandom rng, double ch) {
      return ch == 1.0D || ch != 0.0D && rng.nextDouble() <= ch;
   }

   public static void sendToTrackingPlayers(Cuboid6i bounds, IMessage message, World world) {
      if (world instanceof WorldServer) {
         WorldServer worldServer = (WorldServer)world;
         PlayerChunkMap playerchunkmap = worldServer.getPlayerChunkMap();
         Set<EntityPlayerMP> playerSet = new HashSet();
         bounds.containedChunksTo((x, z) -> {
            PlayerChunkMapEntry entry = playerchunkmap.getEntry(x, z);
            if (entry != null) {
               playerSet.addAll(entry.getWatchingPlayers());
            }

            return null;
         }, false).clear();
         playerSet.stream().forEach((player) -> {
            LocksNetworks.MAIN.sendTo(message, player);
         });
         playerSet.clear();
      }

   }

   public static long getOverworldSeed() {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (server != null) {
         World world = server.getEntityWorld();
         if (world != null) {
            return world.getSeed();
         }
      }

      return 1L;
   }

   public static boolean hasChunkAt(World world, BlockPos pos) {
      return world.isBlockLoaded(pos);
   }

   public static boolean hasChunk(World world, int xx, int zz) {
      return hasChunkAt(world, new BlockPos((xx << 4) + 8, 64, (zz << 4) + 8));
   }
}
