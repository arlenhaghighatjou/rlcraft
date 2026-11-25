package net.ilexiconn.llibrary.client.model.qubble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class QubbleAnimationKeyframe implements INBTSerializable<NBTTagCompound> {
   private int duration;
   private QubbleAnimationKeyframe.Type type;
   private List<QubbleAnimationAction> actions = new ArrayList();

   private QubbleAnimationKeyframe() {
   }

   public static QubbleAnimationKeyframe create(int duration, QubbleAnimationKeyframe.Type type) {
      QubbleAnimationKeyframe keyframe = new QubbleAnimationKeyframe();
      keyframe.setDuration(duration);
      keyframe.setType(type);
      return keyframe;
   }

   public static QubbleAnimationKeyframe deserialize(NBTTagCompound compound) {
      QubbleAnimationKeyframe keyframe = new QubbleAnimationKeyframe();
      keyframe.deserializeNBT(compound);
      return keyframe;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setInteger("duration", this.duration);
      compound.setString("type", this.type.name());
      NBTTagList actionsTag = new NBTTagList();

      for(QubbleAnimationAction action : this.actions) {
         actionsTag.appendTag(action.serializeNBT());
      }

      compound.setTag("actions", actionsTag);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      this.duration = compound.getInteger("duration");
      this.type = QubbleAnimationKeyframe.Type.valueOf(compound.getString("type"));
      NBTTagList actionsTag = compound.getTagList("actions", 10);

      for(int i = 0; i < actionsTag.tagCount(); ++i) {
         this.actions.add(QubbleAnimationAction.deserialize(actionsTag.getCompoundTagAt(i)));
      }

   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public QubbleAnimationKeyframe.Type getType() {
      return this.type;
   }

   public void setType(QubbleAnimationKeyframe.Type type) {
      this.type = type;
   }

   public List<QubbleAnimationAction> getActions() {
      return this.actions;
   }

   public QubbleAnimationKeyframe copy() {
      QubbleAnimationKeyframe keyframe = create(this.getDuration(), this.getType());
      keyframe.getActions().addAll((Collection)this.getActions().stream().map(QubbleAnimationAction::copy).collect(Collectors.toList()));
      return keyframe;
   }

   public static enum Type {
      DYNAMIC,
      STATIC,
      RESET;

      private Type() {
      }
   }
}
