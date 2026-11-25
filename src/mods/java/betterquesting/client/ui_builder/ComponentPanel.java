package betterquesting.client.ui_builder;

import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class ComponentPanel implements INBTSaveLoad<NBTTagCompound> {
   public String refName = "New Panel";
   public String panelType = "betterquesting:canvas_empty";
   public int cvParentID = -1;
   public int tfParentID = -1;
   private NBTTagCompound transTag = new NBTTagCompound();
   private NBTTagCompound panelData = new NBTTagCompound();
   private final List<String> scripts = new ArrayList();

   public ComponentPanel() {
      this.setTransform(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(8, 8, 8, 8), 0));
   }

   public ComponentPanel(GuiTransform transform) {
      this.setTransform(transform);
   }

   public void setTransform(GuiTransform transform) {
      Vector4f anchor = transform.getAnchor();
      this.transTag.setFloat("anchor_left", anchor.x);
      this.transTag.setFloat("anchor_top", anchor.y);
      this.transTag.setFloat("anchor_right", anchor.z);
      this.transTag.setFloat("anchor_bottom", anchor.w);
      GuiPadding padding = transform.getPadding();
      this.transTag.setInteger("pad_left", padding.l);
      this.transTag.setInteger("pad_top", padding.t);
      this.transTag.setInteger("pad_right", padding.r);
      this.transTag.setInteger("pad_bottom", padding.b);
      this.transTag.setInteger("depth", transform.getDepth());
   }

   public NBTTagCompound getTransformTag() {
      return this.transTag;
   }

   public NBTTagCompound getPanelData() {
      return this.panelData;
   }

   public void setPanelData(@Nonnull NBTTagCompound tag) {
      this.panelData = tag;
   }

   public IGuiPanel build() {
      Vector4f anchor = new Vector4f(this.transTag.getFloat("anchor_left"), this.transTag.getFloat("anchor_top"), this.transTag.getFloat("anchor_right"), this.transTag.getFloat("anchor_bottom"));
      GuiPadding padding = new GuiPadding(this.transTag.getInteger("pad_left"), this.transTag.getInteger("pad_top"), this.transTag.getInteger("pad_right"), this.transTag.getInteger("pad_bottom"));
      GuiTransform transform = new GuiTransform(anchor, padding, this.transTag.getInteger("depth"));
      ResourceLocation res = StringUtils.isNullOrEmpty(this.panelType) ? new ResourceLocation("betterquesting:canvas_empty") : new ResourceLocation(this.panelType);
      return ComponentRegistry.INSTANCE.createNew(res, transform, this.panelData);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("ref_name", this.refName);
      nbt.setString("panel_type", this.panelType);
      nbt.setInteger("cv_parent", this.cvParentID);
      nbt.setInteger("tf_parent", this.tfParentID);
      nbt.setTag("transform", this.transTag.copy());
      nbt.setTag("panel_data", this.panelData.copy());
      NBTTagList sList = new NBTTagList();
      this.scripts.forEach((str) -> {
         sList.appendTag(new NBTTagString(str));
      });
      nbt.setTag("script_hooks", sList);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.refName = nbt.getString("ref_name");
      this.panelType = nbt.getString("panel_type");
      this.cvParentID = nbt.getInteger("cv_parent");
      this.tfParentID = nbt.getInteger("tf_parent");
      this.transTag = nbt.getCompoundTag("transform").copy();
      this.panelData = nbt.getCompoundTag("panel_data").copy();
      this.scripts.clear();
      NBTTagList sList = nbt.getTagList("script_hooks", 8);

      for(int i = 0; i < sList.tagCount(); ++i) {
         this.scripts.add(sList.getStringTagAt(i));
      }

   }
}
