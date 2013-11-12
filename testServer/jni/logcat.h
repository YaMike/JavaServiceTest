/*
 * logcat.h
 *
 *  Created on: Oct 9, 2013
 *      Author: Michael Likholet
 */

#ifndef LOGCAT_H_
#define LOGCAT_H_

#include <android/log.h>

#define DBG_ERR(...) (__android_log_write(ANDROID_LOG_ERROR, "JNITestService", ##__VA_ARGS__))
#define DBG_INFO(...) (__android_log_write(ANDROID_LOG_INFO,  "JNITestService", ##__VA_ARGS__))

#endif /* LOGCAT_H_ */
