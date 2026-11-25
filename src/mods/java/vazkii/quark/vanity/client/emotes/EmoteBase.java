package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.TweenManager;

@SideOnly(Side.CLIENT)
public abstract class EmoteBase {
   public static final float PI_F = 3.1415927F;
   public final EmoteDescriptor desc;
   private final TweenManager emoteManager;
   private final ModelBiped model;
   private final ModelBiped armorModel;
   private final ModelBiped armorLegsModel;
   private final EmoteState state;
   private final EntityPlayer player;
   public float timeDone;
   public float totalTime;
   public float animatedTime;
   private long lastMs;

   public EmoteBase(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
      this.desc = desc;
      this.emoteManager = new TweenManager();
      this.state = new EmoteState(this);
      this.model = model;
      this.armorModel = armorModel;
      this.armorLegsModel = armorLegsModel;
      this.player = player;
   }

   public void startAllTimelines() {
      this.startTimeline(this.player, this.model);
      this.startTimeline(this.player, this.armorModel);
      this.startTimeline(this.player, this.armorLegsModel);
      this.lastMs = System.currentTimeMillis();
   }

   void startTimeline(EntityPlayer player, ModelBiped model) {
      Timeline timeline = (Timeline)this.getTimeline(player, model).start(this.emoteManager);
      this.totalTime = timeline.getFullDuration();
   }

   public abstract Timeline getTimeline(EntityPlayer var1, ModelBiped var2);

   public abstract boolean usesBodyPart(int var1);

   public void rotateAndOffset() {
      this.state.rotateAndOffset(this.player);
   }

   public void update() {
      this.state.load(this.model);
      this.state.load(this.armorModel);
      this.state.load(this.armorLegsModel);
      long currTime = System.currentTimeMillis();
      long timeDiff = currTime - this.lastMs;
      this.animatedTime += (float)timeDiff;
      this.emoteManager.update((float)timeDiff);
      this.state.save(this.model);
      this.lastMs = currTime;
      this.timeDone += (float)timeDiff;
   }

   public boolean isDone() {
      return this.timeDone >= this.totalTime || this.player.swingProgress > 0.0F || this.player.hurtTime > 0;
   }
}
