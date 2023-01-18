#include "util/LogUtil.h"
#include <MyGLRenderContext.h>
#include <EGLRender.h>
#include "jni.h"

#define NATIVE_RENDER_CLASS_NAME "com/shenyutao/opengldemo/third/NativeRender"
#define NATIVE_BG_RENDER_CLASS_NAME "com/shenyutao/opengldemo/third/egl/NativeEglRender"

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     MyNativeRender
 * Method:    native_Init
 * Signature: (I)V
 */
JNIEXPORT void JNICALL native_Init(JNIEnv *env, jobject instance, jint i) {
    MyGLRenderContext::GetInstance(i);
}

/*
 * Class:     MyNativeRender
 * Method:    native_UnInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL native_UnInit(JNIEnv *env, jobject instance) {
    MyGLRenderContext::DestroyInstance();
}

/*
 * Class:     MyNativeRender
 * Method:    native_SetImageData
 * Signature: (III[BI)V
 */
JNIEXPORT void JNICALL native_SetImageData
        (JNIEnv *env, jobject instance, jint format, jint width, jint height,
         jbyteArray imageData, int i) {
    int len = env->GetArrayLength(imageData);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(imageData, 0, len, reinterpret_cast<jbyte *>(buf));
    MyGLRenderContext::GetInstance(i)->SetImageData(format, width, height, buf);
    delete[] buf;
    env->DeleteLocalRef(imageData);
}

/*
 * Class:     MyNativeRender
 * Method:    native_SetImageDataWithIndex
 * Signature: (IIII[BI)V
 */
JNIEXPORT void JNICALL native_SetImageDataWithIndex
        (JNIEnv *env, jobject instance, jint index, jint format, jint width, jint height,
         jbyteArray imageData, jint i) {
    int len = env->GetArrayLength(imageData);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(imageData, 0, len, reinterpret_cast<jbyte *>(buf));
    MyGLRenderContext::GetInstance(i)->SetImageDataWithIndex(index, format, width, height, buf);
    delete[] buf;
    env->DeleteLocalRef(imageData);
}

/*
 * Class:     MyNativeRender
 * Method:    native_AdjustPosition
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL native_AdjustPosition
        (JNIEnv *env, jobject instance, jint x,jint y,jint z,jint i) {
    MyGLRenderContext::GetInstance(i)->AdjustPosition(x,y,z);
}



/*
 * Class:     MyNativeRender
 * Method:    native_SetParamsInt
 * Signature: (IIILjava/lang/String;I)V
 */
JNIEXPORT void JNICALL native_SetParams
        (JNIEnv *env, jobject instance, jint paramType, jint value0, jint value1,
         jstring fileName, jint i) {
    MyGLRenderContext::GetInstance(i)->SetParams(paramType, value0, value1,
                                                 env->GetStringUTFChars(fileName, NULL));
}

/*
 * Class:     MyNativeRender
 * Method:    native_SetParamsFloat
 * Signature: (IFFI)V
 */
JNIEXPORT void JNICALL native_SetParamsFloat
        (JNIEnv *env, jobject instance, jint paramType, jfloat value0, jfloat value1, jint i) {
    MyGLRenderContext::GetInstance(i)->SetParamsFloat(paramType, value0, value1);
}


/*
 * Class:     MyNativeRender
 * Method:    native_SetAudioData
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL native_SetAudioData
        (JNIEnv *env, jobject instance, jshortArray data, jint i) {
    int len = env->GetArrayLength(data);
    short *pShortBuf = new short[len];
    env->GetShortArrayRegion(data, 0, len, reinterpret_cast<jshort *>(pShortBuf));
    MyGLRenderContext::GetInstance(i)->SetParamsShortArr(pShortBuf, len);
    delete[] pShortBuf;
    env->DeleteLocalRef(data);
}

/*
 * Class:     MyNativeRender
 * Method:    native_UpdateTransformMatrix
 * Signature: (FFFFI)V
 */
JNIEXPORT void JNICALL
native_UpdateTransformMatrix(JNIEnv *env, jobject instance, jfloat rotateX, jfloat rotateY,
                             jfloat scaleX, jfloat scaleY, jint i) {
    MyGLRenderContext::GetInstance(i)->UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
}

/*
 * Class:     MyNativeRender
 * Method:    native_UpdateTransformMatrix
 * Signature: (IIIIIIIIII)V
 */
JNIEXPORT void JNICALL
native_setViewMatrix(JNIEnv *env, jobject instance, jint eyeX, jint eyeY, jint eyeZ,
                     jint lookAtX,jint lookAtY, jint lookAtZ,
                     jint upX, jint upY, jint upZ,
                     jint i) {
    MyGLRenderContext::GetInstance(i)->setViewMatrix(eyeX, eyeY, eyeZ, lookAtX, lookAtY, lookAtZ, upX, upY, upZ);
}


/*
 * Class:     MyNativeRender
 * Method:    native_OnSurfaceCreated
 * Signature: (I)V
 */
JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject instance, jint i) {
    MyGLRenderContext::GetInstance(i)->OnSurfaceCreated();
}

/*
 * Class:     MyNativeRender
 * Method:    native_OnSurfaceChanged
 * Signature: (III)V
 */
JNIEXPORT void JNICALL native_OnSurfaceChanged
        (JNIEnv *env, jobject instance, jint width, jint height, jint i) {
    MyGLRenderContext::GetInstance(i)->OnSurfaceChanged(width, height);

}

/*
 * Class:     MyNativeRender
 * Method:    native_OnDrawFrame
 * Signature: (I)V
 */
JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject instance, jint i) {
    MyGLRenderContext::GetInstance(i)->OnDrawFrame();
}


/*
 * Class:     egl_NativeBgRender
 * Method:    native_EglRenderInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL native_EglRenderInit(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->Init();

}

/*
 * Class:     egl_NativeBgRender
 * Method:    native_EglRenderSetImageData
 * Signature: ([BII)V
 */
JNIEXPORT void JNICALL
native_EglRenderSetImageData(JNIEnv *env, jobject instance, jbyteArray data, jint width,
                             jint height) {
    int len = env->GetArrayLength(data);
    uint8_t *buf = new uint8_t[len];
    env->GetByteArrayRegion(data, 0, len, reinterpret_cast<jbyte *>(buf));
    EGLRender::GetInstance()->SetImageData(buf, width, height);
    delete[] buf;
    env->DeleteLocalRef(data);


}

/*
 * Class:     egl_NativeBgRender
 * Method:    native_EglRenderSetIntParams
 * Signature: (II)V
 */
JNIEXPORT void JNICALL
native_EglRenderSetIntParams(JNIEnv *env, jobject instance, jint type, jint param) {
    EGLRender::GetInstance()->SetIntParams(type, param);

}

/*
 * Class:     egl_NativeBgRender
 * Method:    native_EglRenderDraw
 * Signature: ()V
 */
JNIEXPORT void JNICALL native_EglRenderDraw(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->Draw();
}

/*
 * Class:     egl_NativeBgRender
 * Method:    natuve_BgRenderUnInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL natuve_BgRenderUnInit(JNIEnv *env, jobject instance) {
    EGLRender::GetInstance()->UnInit();
}

#ifdef __cplusplus
}
#endif

static JNINativeMethod g_RenderMethods[] = {
        {"native_Init",                  "(I)V",                      (void *) (native_Init)},
        {"native_UnInit",                "()V",                       (void *) (native_UnInit)},
        {"native_SetImageData",          "(III[BI)V",                 (void *) (native_SetImageData)},
        {"native_SetImageDataWithIndex", "(IIII[BI)V",                (void *) (native_SetImageDataWithIndex)},
        {"native_SetParams",             "(IIILjava/lang/String;I)V", (void *) (native_SetParams)},
        {"native_SetParamsFloat",        "(IFFI)V",                   (void *) (native_SetParamsFloat)},
        {"native_SetAudioData",          "([SI)V",                    (void *) (native_SetAudioData)},
        {"native_UpdateTransformMatrix", "(FFFFI)V",                  (void *) (native_UpdateTransformMatrix)},
        {"native_OnSurfaceCreated",      "(I)V",                      (void *) (native_OnSurfaceCreated)},
        {"native_OnSurfaceChanged",      "(III)V",                    (void *) (native_OnSurfaceChanged)},
        {"native_OnDrawFrame",           "(I)V",                      (void *) (native_OnDrawFrame)},
        {"native_setViewMatrix",           "(IIIIIIIIII)V",           (void *) (native_setViewMatrix)},
        {"native_AdjustPosition",           "(IIII)V",           (void *) (native_AdjustPosition)}
};

static JNINativeMethod g_BgRenderMethods[] = {
        {"native_EglRenderInit",         "()V",     (void *) (native_EglRenderInit)},
        {"native_EglRenderSetImageData", "([BII)V", (void *) (native_EglRenderSetImageData)},
        {"native_EglRenderSetIntParams", "(II)V",   (void *) (native_EglRenderSetIntParams)},
        {"native_EglRenderDraw",         "()V",     (void *) (native_EglRenderDraw)},
        {"native_EglRenderUnInit",       "()V",     (void *) (natuve_BgRenderUnInit)},
};

static int
RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods, int methodNum) {
    LOGCATE("RegisterNativeMethods");
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGCATE("RegisterNativeMethods fail. clazz == NULL");
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, methodNum) < 0) {
        LOGCATE("RegisterNativeMethods fail");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    LOGCATE("UnregisterNativeMethods");
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGCATE("UnregisterNativeMethods fail. clazz == NULL");
        return;
    }
    if (env != NULL) {
        env->UnregisterNatives(clazz);
    }
}

// call this func when loading lib
extern "C" jint JNI_OnLoad(JavaVM *jvm, void *p) {
    LOGCATE("===== JNI_OnLoad =====");
    jint jniRet = JNI_ERR;
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return jniRet;
    }

    jint regRet = RegisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME, g_RenderMethods,
                                        sizeof(g_RenderMethods) /
                                        sizeof(g_RenderMethods[0]));
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }

    regRet = RegisterNativeMethods(env, NATIVE_BG_RENDER_CLASS_NAME, g_BgRenderMethods,
                                   sizeof(g_BgRenderMethods) /
                                   sizeof(g_BgRenderMethods[0]));
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

extern "C" void JNI_OnUnload(JavaVM *jvm, void *p) {
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    UnregisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME);

    UnregisterNativeMethods(env, NATIVE_BG_RENDER_CLASS_NAME);
}
