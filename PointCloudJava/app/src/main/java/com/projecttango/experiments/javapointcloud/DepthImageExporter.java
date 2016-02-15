package com.projecttango.experiments.javapointcloud;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import com.projecttango.tangosupport.TangoSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DepthImageExporter {
    private static final String TAG = "DepthImageExporter";

    static void exportCurrentDepthImage(Context context, TangoXyzIjData xyzIj, TangoCameraIntrinsics cameraIntrinsics, TangoPoseData colorCameraTXyzIj) {
        try {
            TangoSupport.DepthBuffer depthBuffer = TangoSupport.upsampleImageNearestNeighbor(xyzIj, cameraIntrinsics, colorCameraTXyzIj);
            Bitmap depth_image = Bitmap.createBitmap(depthBuffer.width, depthBuffer.height, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < depthBuffer.width; x++) {
                for (int y = 0; y < depthBuffer.height; y++) {
                    float depth = depthBuffer.depths.get(x + (y * depthBuffer.width));
                    int color = 255 - (int) (255.0 * depth / 5.0);
                    depth_image.setPixel(x, y, Color.rgb(color, color, color));
                }
            }
            Format formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.GERMAN);
            String filename = "depth-image-" + formatter.format(new Date()) + ".png";
            File sd = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/depth_images");
            if (!sd.exists()) {
                if (!sd.mkdirs()) {
                    throw new Exception("Could not create folder " + sd.getPath());
                }
            }
            File dest = new File(sd, filename);
            FileOutputStream out = new FileOutputStream(dest);
            depth_image.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(context, "exported depth image to " + dest.getPath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "exportCurrentDepthImage: Could not upsample depthmap caused by: " + e.getMessage());
            Toast.makeText(context, "exportCurrentDepthImage: Could not upsample depthmap caused by: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
