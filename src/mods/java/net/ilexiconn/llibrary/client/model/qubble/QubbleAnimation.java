package net.ilexiconn.llibrary.client.model.qubble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class QubbleAnimation implements INBTSerializable<NBTTagCompound> {
   private String name;
   private List<QubbleAnimationKeyframe> keyframes = new ArrayList();

   private QubbleAnimation() {
   }

   public static QubbleAnimation create(String name) {
      QubbleAnimation animation = new QubbleAnimation();
      animation.setName(name);
      return animation;
   }

   public static QubbleAnimation deserialize(NBTTagCompound compound) {
      QubbleAnimation animation = new QubbleAnimation();
      animation.deserializeNBT(compound);
      return animation;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("name", this.name);
      NBTTagList keyframesTag = new NBTTagList();

      for(QubbleAnimationKeyframe keyframe : this.keyframes) {
         keyframesTag.appendTag(keyframe.serializeNBT());
      }

      compound.setTag("keyframes", keyframesTag);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      this.name = compound.getString("name");
      NBTTagList keyframesTag = compound.getTagList("name", 10);

      for(int i = 0; i < keyframesTag.tagCount(); ++i) {
         this.keyframes.add(QubbleAnimationKeyframe.deserialize(keyframesTag.getCompoundTagAt(i)));
      }

   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<QubbleAnimationKeyframe> getKeyframes() {
      return this.keyframes;
   }

   public QubbleAnimation copy() {
      QubbleAnimation animation = create(this.getName());
      animation.getKeyframes().addAll((Collection)this.getKeyframes().stream().map(QubbleAnimationKeyframe::copy).collect(Collectors.toList()));
      return animation;
   }
}
