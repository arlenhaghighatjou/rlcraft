package com.teamacronymcoders.base.capability;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class QueueFoundation<T> implements INBTSerializable<NBTTagCompound> {
   private List<T> queuedList;
   private int queueSize;

   public QueueFoundation(int queueSize) {
      this.queueSize = queueSize;
      this.queuedList = Lists.newArrayList();
   }

   public T offer(T value) {
      for(value = (T)this.addToBack(value); this.anyRemainingValue(value) && this.queuedList.size() < this.queueSize; value = (T)this.addToBack(value)) {
         ;
      }

      return value;
   }

   protected void push(T value) {
      this.queuedList.add(value);
   }

   public Optional<T> peek() {
      return Optional.ofNullable(this.queuedList.isEmpty() ? null : this.queuedList.get(0));
   }

   public Optional<T> pull() {
      Optional<T> value;
      if (this.queuedList.isEmpty()) {
         value = Optional.empty();
      } else {
         value = Optional.of(this.queuedList.get(0));
         this.queuedList.remove(0);
      }

      return value;
   }

   public Optional<T> getEndOfQueue() {
      return Optional.ofNullable(this.queuedList.isEmpty() ? null : this.queuedList.get(this.queuedList.size() - 1));
   }

   public int getQueueSize() {
      return this.queueSize;
   }

   protected void setQueueSize(int queueSize) {
      this.queueSize = queueSize;
   }

   public int getLength() {
      return this.queuedList.size();
   }

   protected List<T> getBackingList() {
      return this.queuedList;
   }

   protected abstract T addToBack(T var1);

   protected abstract boolean anyRemainingValue(T var1);

   public NBTTagCompound serializeNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setInteger("queueSize", this.getQueueSize());
      NBTTagList tagList = new NBTTagList();

      for(T value : this.getBackingList()) {
         tagList.appendTag(this.serializeValue(value));
      }

      nbtTagCompound.setTag("queueValues", tagList);
      return nbtTagCompound;
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.setQueueSize(nbt.getInteger("queueSize"));
      NBTTagList queueValueNBT = nbt.getTagList("fluidStacks", 9);

      for(int i = 0; i < queueValueNBT.tagCount(); ++i) {
         this.getBackingList().add(this.deserializeValue(queueValueNBT.getCompoundTagAt(i)));
      }

   }

   public abstract NBTTagCompound serializeValue(T var1);

   public abstract T deserializeValue(NBTTagCompound var1);
}
