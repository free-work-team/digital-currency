package com.jyt.bitcoinmaster.facerecognization.listener;

public interface FaceCompareListener {
    void detectedResult(boolean success,float score,String imageData);
}
