package com.teamacronymcoders.base.multiblocksystem.validation;

public interface IMultiblockValidator {
   ValidationError getLastError();

   void setLastError(ValidationError var1);

   void setLastError(String var1, Object... var2);
}
