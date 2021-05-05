package com.rt21;

import android.util.Size;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;

public class CameraFlow {
    ImageCapture imageCapture =
            new ImageCapture.Builder()
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .build();

    ImageAnalysis imageAnalysis =
            new ImageAnalysis.Builder()
                    .setTargetResolution(new Size(640, 480))
                    .build();


}
