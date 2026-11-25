package melonslise.locks.common.command;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ILockableStorage;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.BiIntFunction;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class CommandLocksDebug extends CommandBase {
   public CommandLocksDebug() {
   }

   public String getName() {
      return "locksdebug";
   }

   public String getUsage(ICommandSender sender) {
      return "/locksdebug";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      World world = sender.getEntityWorld();
      String dimensionInfo = "Running locksdebug for dimension " + world.provider.getDimension();
      Locks.logger.info(dimensionInfo);
      this.informPlayer(dimensionInfo, sender);
      ILockableHandler handler = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
      Set<Integer> loadedIds = new HashSet();
      boolean foundAnyIssue = false;
      int issues = 0;
      ObjectIterator var10 = handler.getLoaded().entrySet().iterator();

      while(var10.hasNext()) {
         Entry<Integer, Lockable> entry = (Entry)var10.next();
         loadedIds.add(Integer.valueOf(((Integer)entry.getKey()).intValue()));
         boolean sendLockableInfo = false;
         Integer mappedId = (Integer)entry.getKey();
         Lockable lockable = (Lockable)entry.getValue();
         String lockableInfo = mappedId + " : " + lockable.toString();
         Locks.logger.info(lockableInfo);
         if (lockable.networkID != mappedId.intValue()) {
            Locks.logger.warn("Lock has mismatched network ID");
            this.informPlayer("Lock has mismatched network ID", sender);
            sendLockableInfo = true;
            ++issues;
         }

         if (lockable.countObservers() != 1) {
            Locks.logger.warn("Lockable has incorrect observer count: " + lockable.countObservers());
            this.informPlayer("Lockable has incorrect observer count: " + lockable.countObservers(), sender);
            sendLockableInfo = true;
            ++issues;
         }

         if (lockable.lock.countObservers() != 1) {
            Locks.logger.warn("Lockable's Lock has incorrect observer count: " + lockable.lock.countObservers());
            this.informPlayer("Lockable's lock has incorrect observer count: " + lockable.lock.countObservers(), sender);
            sendLockableInfo = true;
            ++issues;
         }

         List<Chunk> chs = lockable.box.<Chunk>containedChunksTo((x, z) -> {
            return LocksUtil.hasChunk(world, x, z) ? world.getChunkFromChunkCoords(x, z) : null;
         }, true);
         if (chs != null && chs.size() != 0) {
            for(Chunk chunk : chs) {
               ILockableStorage storage = (ILockableStorage)chunk.getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null);
               Lockable chunkLockable = (Lockable)storage.get().get(lockable.networkID);
               if (chunkLockable == null) {
                  Locks.logger.warn("Loaded lockable has chunk that is missing itself");
                  this.informPlayer("Loaded lockable has chunk that is missing itself", sender);
                  sendLockableInfo = true;
                  ++issues;
               } else if (!lockable.equals(chunkLockable)) {
                  Locks.logger.warn("Loaded lockable has chunk that is outdated");
                  this.informPlayer("Loaded lockable has chunk that is outdated", sender);
                  sendLockableInfo = true;
                  ++issues;
               }
            }
         } else {
            Locks.logger.warn("Loaded lockable has no chunks");
            this.informPlayer("Loaded lockable has no chunks", sender);
            sendLockableInfo = true;
            ++issues;
         }

         if (sendLockableInfo) {
            foundAnyIssue = true;
            this.informPlayer("|_ " + lockableInfo, sender);
         }
      }

      for(Chunk chunk : Lists.newArrayList(((ChunkProviderServer)world.getChunkProvider()).getLoadedChunks())) {
         ILockableStorage storage = (ILockableStorage)chunk.getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null);
         ObjectIterator var24 = storage.get().entrySet().iterator();

         while(var24.hasNext()) {
            Entry entry = (Entry)var24.next();
            if (!loadedIds.contains(entry.getKey())) {
               foundAnyIssue = true;
               ++issues;
               Locks.logger.warn("Chunk has ghost lockable: " + chunk.getPos() + " : " + entry.getValue().toString());
               this.informPlayer("Chunk has ghost lockable: " + chunk.getPos() + " : " + entry.getValue().toString(), sender);
            }
         }
      }

      if (foundAnyIssue) {
         Locks.logger.info("Locks Debug Completed with issues: " + issues);
         this.informPlayer("Locks Debug Completed with issues: " + issues, sender);
      } else {
         Locks.logger.info("Locks Debug completed with no obvious issues found");
         this.informPlayer("Locks Debug completed with no obvious issues found", sender);
      }

   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   private void informPlayer(String str, ICommandSender sender) {
      if (sender instanceof EntityPlayer) {
         sender.sendMessage(new TextComponentString(str));
      }

   }
}
