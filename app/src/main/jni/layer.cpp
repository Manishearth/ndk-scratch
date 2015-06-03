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
class Layer;
uint32_t randomColor();

class Layer {
    public:
        vector<Layer> sublayers;
        bool hasDirty;
        bool isDirty;
        int x;
        int y;
        int w;
        int h;
        int canvasW;
        Layer *parentPtr;
        bool hasParent;
        uint32_t color;
        Layer(int tw, int th) {
            hasParent = false;
            w = tw;
            canvasW = tw;
            h = th;
            color = randomColor();
            x = 0;
            y = 0;
        }
        void bindNewLayer(int relx, int rely, int tw, int th) {
            assert(relx+tw > this->w || rely+th > this->h);
            Layer layer = Layer(tw, th);
            layer.x = relx + this->x;
            layer.y = rely + this->y;
            layer.canvasW = this->canvasW;
            layer.parentPtr = this;
            layer.hasParent = true;
            this->sublayers.push_back(layer);
        }
        Layer* get(int index) {
            return &sublayers[index];
        }

        void paint(void* pixels) {

            int i,j;
            for (i = 0; i< h; i++) {
                for (j = 0; j<w; j++) {
                    ((uint32_t*)pixels)[canvasW*(y+i) + x + j] = color;
                }
            }
            vector<Layer>::iterator itr = sublayers.begin();
            for(itr; itr != sublayers.end(); itr++) {
                itr->paint(pixels);
            }
        }

        Layer* getLayerByCoord(int cx, int cy) {
            if (cx < this->x || cx> this->x + this->w || cy < this->y || cy> this->y + this->h) {
                return NULL;
            }
            vector<Layer>::iterator itr = sublayers.begin();
            for(itr; itr != sublayers.end(); itr++) {
                Layer* found = itr->getLayerByCoord(cx, cy);
                if (found != NULL) {
                    return found;
                }
            }
            return this;
        }
};

uint32_t randomColor() {

    // return (long) (static_cast <float> (rand()) / static_cast <float> (RAND_MAX))*0xFFFFFFFF;
    // float randn = ((static_cast <float> (rand()) / static_cast <float> (RAND_MAX)));
    uint32_t randl = (uint32_t) (rand()%0xFFFFFF) + 0xFF000000;
    LOGI("Manish %d", randl);
    return  randl;
}
extern "C" {
    jlong  Java_com_example_hellojni_HelloJni_getLayer(JNIEnv* env, jobject thiz, jint w, jint h) {
        srand (time(NULL));
        Layer *root = new Layer(w, h);
        root->bindNewLayer( 20, 20, 100, 100);
        root->bindNewLayer( 200, 200, 100, 150);
        root->get(0)->bindNewLayer(10,10,50, 50);
        return (long long int)(void*)(root);
    }
    void Java_com_example_hellojni_HelloJni_flushLayer(JNIEnv* env, jobject thiz,jobject bitmap, jlong rootLayer) {
        Layer* lay = (Layer*)rootLayer;
        void* pixels;
        AndroidBitmap_lockPixels(env, bitmap, &pixels);
        lay->paint(pixels);
        AndroidBitmap_unlockPixels(env, bitmap);
    }
    jlong Java_com_example_hellojni_HelloJni_touchLayer(JNIEnv* env, jobject thiz,
                                                       jlong rootLayer,
                                                       jint x, jint y) {
        Layer* lay = (Layer*)rootLayer;
        Layer* found = lay->getLayerByCoord(x,y);
        if (found != NULL) {
            found->color = randomColor();
        }
        return (jlong)found;
    }
}
