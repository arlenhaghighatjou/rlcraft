package bq_standard.importers.hqm.converters;

import java.util.HashMap;

public class HQMRep {
   public final String rName;
   private final HashMap<Integer, Integer> markerList = new HashMap();

   public HQMRep(String name) {
      this.rName = name;
   }

   public void addMarker(int id, int value) {
      this.markerList.put(Integer.valueOf(id), Integer.valueOf(value));
   }

   public int getMarker(int id) {
      Integer i = (Integer)this.markerList.get(Integer.valueOf(id));
      return i == null ? 0 : i.intValue();
   }
}
