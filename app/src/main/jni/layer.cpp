#include <string.h>
#include <jni.h>
#include <android/bitmap.h>
#include<assert.h>
#include<vector>
#include<cstdlib>
#include<ctime>
#include<android/log.h>


#ifdef __ANDROID__
#define LOG_TAG "MyNative"
#define STRINGIFY(x) #x
#define LOG_TAG    __FILE__ ":" STRINGIFY(__MyNative__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#endif


using namespace std;
uint32_t randomColor();

// inefficiently paint to buffer
void paint(void* pixels, int w, int h) {
    int i;
    uint32_t color = randomColor();
    for(i=0; i <w*h; i++) {
        ((uint32_t*)pixels)[i] = color;
    }
}

uint32_t randomColor() {
    uint32_t randl = (uint32_t) (rand()%0xFFFFFF) + 0xFF000000;
    return  randl;
}

extern "C" {
    // paint each time, but directly
    void Java_com_example_hellojni_HelloJni_draw1(JNIEnv* env, jobject thiz,jobject bitmap,jint w, jint h) {

        int i;
        for(i = 0; i<100; i++) {
            void* pixels;
            AndroidBitmap_lockPixels(env, bitmap, &pixels);
            paint(pixels, w, h);
            AndroidBitmap_unlockPixels(env, bitmap);
        }

    }
    // paint once, memcpy multiple times
    void Java_com_example_hellojni_HelloJni_draw2(JNIEnv* env, jobject thiz,jobject bitmap, jint w, jint h) {
        uint32_t* pixels0 = new uint32_t[w*h];

        paint((void*)pixels0, w, h);
        int i;
        for(i = 0; i<100; i++) {
            void *pixels;
            AndroidBitmap_lockPixels(env, bitmap, &pixels);
            memcpy(pixels, pixels0, w*h*sizeof(uint32_t));
            AndroidBitmap_unlockPixels(env, bitmap);
        }
    }

}
