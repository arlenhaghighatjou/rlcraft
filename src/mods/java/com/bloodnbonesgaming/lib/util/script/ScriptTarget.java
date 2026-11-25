package com.bloodnbonesgaming.lib.util.script;

public interface ScriptTarget {
   Object NO_METHOD = new Object();
   Object WRONG_SIDE = new Object();

   Object call(String method, Object[] args) throws Exception;
}
