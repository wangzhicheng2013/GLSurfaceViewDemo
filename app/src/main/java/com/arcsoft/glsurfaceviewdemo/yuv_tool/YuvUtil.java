package com.arcsoft.glsurfaceviewdemo.yuv_tool;

import android.util.Log;

public class YuvUtil {
    private final static String TAG = "DisplaySurfaceView";
    public static byte[] UYVY_2_NV21(byte[] uyvy_content, int width, int height) {
        if (null == uyvy_content) {
            Log.e(TAG, "uyvy_content is null!");
            return null;
        }
        int frame_size = width * height * 3 / 2;
        byte[] nv21_content = new byte[frame_size];
        int y_size = width * height;
        int pixels_in_a_row = width * 2;
        int nv21_y_pos = 0;
        int nv21_uv_pos = y_size;
        int lines = 0;
        for (int i = 0;i < uyvy_content.length;i += 4) {
            nv21_content[nv21_y_pos++] = uyvy_content[i + 1];
            nv21_content[nv21_y_pos++] = uyvy_content[i + 3];
            if (0 == i % pixels_in_a_row) {
                ++lines;
            }
            if (lines % 2 != 0) {       // extract the UV value of odd rows
                // copy uv channel
                nv21_content[nv21_uv_pos++] = uyvy_content[i + 2];
                nv21_content[nv21_uv_pos++] = uyvy_content[i];
            }
        }
        return nv21_content;
    }
    // blacken the designated area of nv12 image
    // left:Abscissa of upper left corner of rectangular area
    // top:Vertical coordinate of upper left corner of rectangular area
    // right:Abscissa of lower right corner of rectangular area
    // bottom:Vertical coordinate of the lower right corner of the rectangular area
    // width:Original image width
    // height:Original image height
    public static void blackening_nv12(int left,
                         int top,
                         int right,
                         int bottom,
                         int width,
                         int height,
                         byte[] nv21_bytes) {
        if (left < 0) {
            left = 0;
        }
        else if (left >= width) {
            left = width - 1;
        }
        if (right < 0) {
            right = 0;
        }
        else if (right >= width) {
            right = width - 1;
        }
        if (top < 0) {
            top = 0;
        }
        else if (top >= height) {
            top = height - 1;
        }
        if (bottom < 0) {
            bottom = 0;
        }
        else if (bottom >= height) {
            bottom = height - 1;
        }
        int w = right - left;
        int h = bottom - top;
        if (w <= 0 || h <= 0) {
            return;
        }
        int ppu8Plane_y_pos = 0;
        int ppu8Plane_uv_pos = width * height;
        for (int j = top;j <= bottom;j++) {
            for (int k = 0;k < w;k++) {
                nv21_bytes[ppu8Plane_y_pos + left + j * width + k] = 0;
                nv21_bytes[ppu8Plane_uv_pos + left + j / 2 * width + k] = (byte) 128;
            }
        }
    }
}
