/*
 * layer-jni.h
 *
 *  Created on: Oct 30, 2013
 *      Author: michael
 */

#ifndef LAYER_JNI_H_
#define LAYER_JNI_H_

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

extern JavaVM *jvm;
extern jobject   jTronController;
extern jmethodID jRegStateChangeId;
extern jmethodID jIncomingCallId;
extern jmethodID jCallFinishedId;

void makeGlobalRef(JNIEnv *env, jobject *ref);
void deleteGlobalRef(JNIEnv *env, jobject *ref);

void regStateChange(JNIEnv *env, const char *str);
void incomingCall(JNIEnv *env, const char *str);
void callFinished(JNIEnv *env, const char *str);

#ifdef __cplusplus
}
#endif

#endif /* LAYER_JNI_H_ */
