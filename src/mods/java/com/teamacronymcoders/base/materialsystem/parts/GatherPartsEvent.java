package com.teamacronymcoders.base.materialsystem.parts;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import java.util.List;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GatherPartsEvent extends Event {
   private List<Part> partList = Lists.newArrayList();
   private List<PartType> partTypeList = Lists.newArrayList();

   public GatherPartsEvent() {
   }

   public void addPart(Part part) {
      this.partList.add(part);
   }

   public void addPartType(PartType partType) {
      this.partTypeList.add(partType);
   }

   public List<Part> getPartList() {
      return this.partList;
   }

   public List<PartType> getPartTypeList() {
      return this.partTypeList;
   }
}
