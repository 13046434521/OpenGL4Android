package com.jtl.opengl.camera;

/**
 * 作者:jtl
 * 日期:Created in 2019/9/26 20:28
 * 描述:
 * 更改:
 */
public class YuvToRgb {
    static double[][] YUV2RGB_CONVERT_MATRIX = new double[][]{{1, 0, 1.4022}, {1, -0.3456, -0.7145}, {1, 1.771, 0}};
    private static int R = 0;
    private static int G = 1;
    private static int B = 2;

    //I420是yuv420格式，是3个plane，排列方式为(Y)(U)(V)
    public static int[] I420ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int positionOfU = numOfPixel / 4 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];
        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = (i / 2) * (width / 2);
            int startU = positionOfV + step;
            int startV = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = startV + j / 2;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }

        return rgb;
    }

    private static RGB yuvTorgb(byte Y, byte U, byte V) {
        RGB rgb = new RGB();
        rgb.r = (int) ((Y & 0xff) + 1.4075 * ((V & 0xff) - 128));
        rgb.g = (int) ((Y & 0xff) - 0.3455 * ((U & 0xff) - 128) - 0.7169 * ((V & 0xff) - 128));
        rgb.b = (int) ((Y & 0xff) + 1.779 * ((U & 0xff) - 128));
        rgb.r = (rgb.r < 0 ? 0 : rgb.r > 255 ? 255 : rgb.r);
        rgb.g = (rgb.g < 0 ? 0 : rgb.g > 255 ? 255 : rgb.g);
        rgb.b = (rgb.b < 0 ? 0 : rgb.b > 255 ? 255 : rgb.b);
        return rgb;
    }

    //YV16是yuv422格式，是三个plane，(Y)(U)(V)
    public static int[] YV16ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int positionOfV = numOfPixel / 2 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];
        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width / 2;
            int startU = positionOfU + step;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = startV + j / 2;
                int index = Y * 3;
                //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YV12是yuv420格式，是3个plane，排列方式为(Y)(V)(U)
    public static int[] YV12ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int positionOfU = numOfPixel / 4 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = (i / 2) * (width / 2);
            int startV = positionOfV + step;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = startU + j / 2;
                int index = Y * 3;

                //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YUY2是YUV422格式，排列是(YUYV)，是1 plane
    public static int[] YUY2ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startY = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int Y1 = j + startY;
                int Y2 = Y1 + 2;
                int U = Y1 + 1;
                int V = Y1 + 3;
                int index = (Y1 >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //UYVY是YUV422格式，排列是(UYVY)，是1 plane
    public static int[] UYVYToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startU = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int U = j + startU;
                int Y1 = U + 1;
                int Y2 = U + 3;
                int V = U + 2;
                int index = (U >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV21是YUV420格式，排列是(Y), (VU)，是2 plane
    public static int[] NV21ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i / 2 * width;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = V + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV12是YUV420格式，排列是(Y), (UV)，是2 plane
    public static int[] NV12ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i / 2 * width;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = U + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV16是YUV422格式，排列是(Y), (UV)，是2 plane
    public static int[] NV16ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = U + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV61是YUV422格式，排列是(Y), (VU)，是2 plane
    public static int[] NV61ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = V + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YVYU是YUV422格式，排列是(YVYU)，是1 plane
    public static int[] YVYUToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startY = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int Y1 = j + startY;
                int Y2 = Y1 + 2;
                int V = Y1 + 1;
                int U = Y1 + 3;
                int index = (Y1 >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //VYUY是YUV422格式，排列是(VYUY)，是1 plane
    public static int[] VYUYToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startV = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int V = j + startV;
                int Y1 = V + 1;
                int Y2 = V + 3;
                int U = V + 2;
                int index = (U >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    static void ConvertYUV2RGB(byte[] yuvFrame, byte[] rgbFrame, int width, int height) {
        int uIndex = width * height;
        int vIndex = uIndex + ((width * height) >> 2);
        int gIndex = width * height;
        int bIndex = gIndex * 2;

        int temp = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // R分量
                temp = (int) (yuvFrame[y * width + x] + (yuvFrame[vIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[0][2]);
                rgbFrame[y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));

                // G分量
                temp = (int) (yuvFrame[y * width + x] + (yuvFrame[uIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[1][1] + (yuvFrame[vIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[1][2]);
                rgbFrame[gIndex + y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));

                // B分量
                temp = (int) (yuvFrame[y * width + x] + (yuvFrame[uIndex + (y / 2) * (width / 2) + x / 2] - 128) * YUV2RGB_CONVERT_MATRIX[2][1]);
                rgbFrame[bIndex + y * width + x] = (byte) (temp < 0 ? 0 : (temp > 255 ? 255 : temp));
            }
        }
    }

    private static class RGB {
        public int r, g, b;
    }
}
