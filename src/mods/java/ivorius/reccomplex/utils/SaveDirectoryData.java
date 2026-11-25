package ivorius.reccomplex.utils;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class SaveDirectoryData {
   private ResourceDirectory directory;
   private boolean deleteOther;
   private Set<String> filesInActive;
   private Set<String> filesInInactive;

   public SaveDirectoryData(ResourceDirectory directory, boolean deleteOther, Set<String> filesInActive, Set<String> filesInInactive) {
      this.directory = directory;
      this.deleteOther = deleteOther;
      this.filesInActive = filesInActive;
      this.filesInInactive = filesInInactive;
   }

   public static <T> void writeCollection(ByteBuf buf, Collection<T> collection, Consumer<T> consumer) {
      buf.writeInt(collection.size());
      collection.forEach(consumer);
   }

   public static <T> Collection<T> readCollection(ByteBuf buf, Function<ByteBuf, T> supplier) {
      return (Collection)IntStream.range(0, buf.readInt()).mapToObj((i) -> {
         return (Collection<T>)supplier.apply(buf);
      }).collect(Collectors.toList());
   }

   public static SaveDirectoryData defaultData(String id, @Nullable ResourceDirectory directory, Set<String> filesInActive, Set<String> filesInInactive) {
      if (directory == null) {
         directory = filesInActive.contains(id) ? ResourceDirectory.ACTIVE : ResourceDirectory.INACTIVE;
      }

      return new SaveDirectoryData(directory, true, filesInActive, filesInInactive);
   }

   public static SaveDirectoryData readFrom(ByteBuf buf) {
      return new SaveDirectoryData(ResourceDirectory.read(buf), buf.readBoolean(), (Set)readCollection(buf, ByteBufUtils::readUTF8String).stream().collect(Collectors.toSet()), (Set)readCollection(buf, ByteBufUtils::readUTF8String).stream().collect(Collectors.toSet()));
   }

   public void writeTo(ByteBuf buf) {
      this.directory.write(buf);
      buf.writeBoolean(this.deleteOther);
      writeCollection(buf, this.filesInActive, (s) -> {
         ByteBufUtils.writeUTF8String(buf, s);
      });
      writeCollection(buf, this.filesInInactive, (s) -> {
         ByteBufUtils.writeUTF8String(buf, s);
      });
   }

   public ResourceDirectory getDirectory() {
      return this.directory;
   }

   public void setDirectory(ResourceDirectory directory) {
      this.directory = directory;
   }

   public boolean isDeleteOther() {
      return this.deleteOther;
   }

   public void setDeleteOther(boolean deleteOther) {
      this.deleteOther = deleteOther;
   }

   public Set<String> getFilesInActive() {
      return this.filesInActive;
   }

   public void setFilesInActive(Set<String> filesInActive) {
      this.filesInActive = filesInActive;
   }

   public Set<String> getFilesInInactive() {
      return this.filesInInactive;
   }

   public void setFilesInInactive(Set<String> filesInInactive) {
      this.filesInInactive = filesInInactive;
   }

   public SaveDirectoryData.Result getResult() {
      return new SaveDirectoryData.Result(this.getDirectory(), this.isDeleteOther());
   }

   public static class Result {
      public final ResourceDirectory directory;
      public final boolean deleteOther;

      public Result(ResourceDirectory directory, boolean deleteOther) {
         this.directory = directory;
         this.deleteOther = deleteOther;
      }

      public static SaveDirectoryData.Result readFrom(ByteBuf buf) {
         return new SaveDirectoryData.Result(ResourceDirectory.read(buf), buf.readBoolean());
      }

      public void writeTo(ByteBuf buf) {
         this.directory.write(buf);
         buf.writeBoolean(this.deleteOther);
      }
   }
}
