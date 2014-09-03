package com.is3av.shake4location;

public interface AccelerometerListener {
    
    public void onAccelerationChanged(float x, float y, float z);
  
    public void onShake(float force,float x,float y,float z);
  
}