LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := hello-jni
LOCAL_LDLIBS := \
	-llog \
	-landroid \
	-ljnigraphics \

LOCAL_SRC_FILES := \
	C:\Users\t-magor\Desktop\hello-jni\app\src\main\jni\Android.mk \
	C:\Users\t-magor\Desktop\hello-jni\app\src\main\jni\Application.mk \
	C:\Users\t-magor\Desktop\hello-jni\app\src\main\jni\hello-jni.c \

LOCAL_C_INCLUDES += C:\Users\t-magor\Desktop\hello-jni\app\src\main\jni
LOCAL_C_INCLUDES += C:\Users\t-magor\Desktop\hello-jni\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
