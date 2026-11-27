package ichttt.mods.firstaid.common.config;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;

public class ConfigEntry<T extends Annotation> {
   @Nonnull
   private final Supplier<Object> getter;
   @Nonnull
   private final Consumer<ByteBuf> writer;
   @Nonnull
   private final Consumer<ByteBuf> reader;
   private final boolean revert;
   @Nonnull
   public final UniqueProperty property;
   private boolean hasRemoteData = false;
   @Nullable
   private ByteBuf originalState;

   public ConfigEntry(@Nonnull Supplier<Object> getter, @Nonnull Consumer<ByteBuf> writer, @Nonnull Consumer<ByteBuf> reader, boolean revertOnSave, @Nonnull UniqueProperty property) {
      this.getter = getter;
      this.writer = writer;
      this.reader = reader;
      this.revert = revertOnSave;
      this.property = property;
      if (revertOnSave) {
         ByteBuf buffer = Unpooled.buffer();

         try {
            Object originalState = getter.get();
            this.writeToBuf(buffer);
            this.readFromBuf(buffer);
            if (!originalState.equals(getter.get())) {
               throw new RuntimeException("Error while writing and reading: Values do not match!");
            }
         } finally {
            this.hasRemoteData = false;
         }

         this.originalState = Unpooled.buffer();
         this.writeToBuf(this.originalState);
      }

   }

   public boolean hasRemoteData() {
      return this.hasRemoteData;
   }

   public void revert() {
      if (this.revert && this.hasRemoteData) {
         this.readFromBuf(((ByteBuf)Objects.requireNonNull(this.originalState)).copy());
         this.hasRemoteData = false;
      }

   }

   public void updateOrigState() {
      if (this.revert) {
         this.originalState = Unpooled.buffer();
         this.writeToBuf(this.originalState);
      }

   }

   public void writeToBuf(ByteBuf buf) {
      this.writer.accept(buf);
   }

   public void readFromBuf(ByteBuf buf) {
      this.hasRemoteData = true;
      this.reader.accept(buf);
   }
}
