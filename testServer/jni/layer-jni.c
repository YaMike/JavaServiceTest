#include "logcat.h"
#include "layer-jni.h"

#include <jni.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

JavaVM *jvm = NULL;
jobject jObj;

static int run = 0;
static pthread_t t;

void callVoidMethodString(JNIEnv *env, jobject jcl, jmethodID jmid, const char *str);

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *ajvm, void *dummy) {
	return JNI_VERSION_1_6;
}

void *thread_func(void *dummy) {
	run = 1;
	JNIEnv *env = NULL;
	if (JNI_EDETACHED == (*jvm)->GetEnv(jvm, (void**)&env, JNI_VERSION_1_6)) {
		if ( 0 != (*jvm)->AttachCurrentThread(jvm, &env, NULL)) {
			DBG_INFO("Cannot attach JNIEnv!\n");
		}
	}
	jmethodID jmid = (*env)->GetMethodID(env, jObj, "stringJavaMethod", "(Ljava/lang/String;)V");
	if (!jmid) {
		DBG_ERR("Cannot find java method...Terminating\n");
		return NULL;
	}

	while(run) {
		struct timespec ts = {.tv_sec = 1, .tv_nsec = 0 };
		nanosleep(&ts, NULL);
		DBG_INFO("Trying to call method\n");
		callVoidMethodString(env, jObj, jmid, "***** Native2Java call works! *****\n");
	}
	(*jvm)->DetachCurrentThread(jvm);
	return NULL;
}

JNIEXPORT void JNICALL
Java_com_example_testservice_TestNative_startAthread( JNIEnv* env,
		jobject thiz)
{
	DBG_INFO("enter startAthread()\n");
	if (JNI_OK != (*env)->GetJavaVM(env, &jvm)) {
		DBG_ERR("Cannot access Java VM! Terminating call.\n");
		return;
	}
	DBG_INFO("Caching class tc...\n");
	jObj = thiz;
	jobject globalRef = (*env)->NewGlobalRef(env, jObj);
	(*env)->DeleteLocalRef(env, jObj);
	jObj = globalRef;
	if (NULL == jObj) {
		DBG_ERR("Cannot cache class TronNative!\n");
		return;
	}
	if (pthread_create(&t, NULL, thread_func, NULL)) {
		DBG_ERR("Cannot create thread!\n");
	}
}

static unsigned call_count = 0;

void callVoidMethodString(JNIEnv *env, jobject jcl, jmethodID jmid, const char *str) {
	jstring jstr = (*env)->NewStringUTF(env, str);
	char calls_str[50] = {0};
	sprintf(calls_str, "calls:%u\n", call_count++);
	(*env)->CallVoidMethod(env, jcl, jmid, jstr);
	if ((*env)->ExceptionCheck(env)) {
		DBG_ERR("There is some exceptional situation!\n");
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	(*env)->DeleteLocalRef(env, jstr);
	DBG_INFO(calls_str);
}

