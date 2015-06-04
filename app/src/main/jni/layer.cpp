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
void paint(void* pixels, int w, int h);

class CBitmapHandle {\
    public:
    void* pixels;
    uint32_t width;
    uint32_t height;
    CBitmapHandle(int w, int h) {
        width = w;
        height = h;
    }
    void fill() {
        paint(pixels, width, height);
    }
};


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
    jlong Java_com_example_hellojni_DualBitmap_LOCK(JNIEnv* env, jobject thiz,jobject bitmap,jint w, jint h) {

       CBitmapHandle *bh = new CBitmapHandle(w, h);
       AndroidBitmap_lockPixels(env, bitmap, &bh->pixels);
       return (long)(void*)bh;

    }
    void Java_com_example_hellojni_DualBitmap_UNLOCK(JNIEnv* env, jobject thiz,jobject bitmap) {

       AndroidBitmap_unlockPixels(env, bitmap);


    }
    void Java_com_example_hellojni_DualBitmap_drawC(JNIEnv* env, jobject thiz,jlong bh) {
        CBitmapHandle *handle = (CBitmapHandle*)bh;
        handle->fill();
    }

    jint Java_com_example_hellojni_DualBitmap_randomColor(JNIEnv* env) {
        return randomColor();
    }
}
