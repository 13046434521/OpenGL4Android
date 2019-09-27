#include <jni.h>
#include <string>
#include <malloc.h>
#include <string.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_jtl_opengl_helper_NativeHelper_yuvToRgb(JNIEnv *env, jclass clazz, jbyteArray yuv_data,
                                                 jbyteArray rgb_data, jint width, jint height) {

    jbyte *tYUVData = env->GetByteArrayElements(yuv_data, 0);
    jbyte *tRGBData = env->GetByteArrayElements(rgb_data, 0);
    int length = width * height * 4;
    jbyte yValue, vValue, uValue;
    int index;
    int r, g, b;

    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            index = j % 2 == 0 ? j : j - 1;

            yValue = tYUVData[width * i + j];
            vValue = tYUVData[width * height + width * (i / 2) + index];
            uValue = tYUVData[width * height + width * (i / 2) + index + 1];

            r = yValue + (1.370705 * (vValue - 128));
            g = yValue - (0.698001 * (vValue - 128)) - (0.337633 * (uValue - 128));
            b = yValue + (1.732446 * (uValue - 128));

            r = r < 0 ? 0 : (r > 255 ? 255 : r);
            g = g < 0 ? 0 : (g > 255 ? 255 : g);
            b = b < 0 ? 0 : (b > 255 ? 255 : b);

            tRGBData[width * i * 4 + j * 4 + 0] = r;
            tRGBData[width * i * 4 + j * 4 + 1] = g;
            tRGBData[width * i * 4 + j * 4 + 2] = b;
            tRGBData[width * i * 4 + j * 4 + 3] = 255;
        }
    }
    jbyte *buf = (jbyte *) malloc(length);
    memcpy(buf, tRGBData, length);
    env->SetByteArrayRegion(rgb_data, 0, length, buf);
    free(buf);
}