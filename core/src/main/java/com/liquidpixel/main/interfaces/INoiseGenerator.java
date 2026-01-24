package com.liquidpixel.main.interfaces;

public interface INoiseGenerator {

    float getValue(int x, int y);

    float getValue(int x, int y, int width, int height);

}
