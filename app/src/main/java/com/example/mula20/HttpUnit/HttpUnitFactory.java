package com.example.mula20.HttpUnit;

  public class HttpUnitFactory {

      static public IHttpUnit Get()
      {
          return new HttpUnit_Okhttp();
      }
}
