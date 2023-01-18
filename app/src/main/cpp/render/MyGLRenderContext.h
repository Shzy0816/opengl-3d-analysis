#ifndef NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H
#define NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H

#include "stdint.h"
#include <GLES3/gl3.h>
#include "TriangleSample.h"
#include <string>

class MyGLRenderContext {
    MyGLRenderContext();

    ~MyGLRenderContext();

public:
    void SetImageData(int format, int width, int height, uint8_t *pData);

    void SetImageDataWithIndex(int index, int format, int width, int height, uint8_t *pData);

    void SetParams(int paramType, int value0, int value1, std::string fileName);

    void SetParamsFloat(int paramType, float value0, float value1);

    void SetParamsShortArr(short *const pShortArr, int arrSize);

    void UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

    void OnSurfaceCreated();

    void OnSurfaceChanged(int width, int height);

    void OnDrawFrame();

    void setViewMatrix(int eyeX, int eyeY, int eyeZ, int lookAtX, int lookAtY,
                       int lookAtZ, int upX, int upY, int upZ);

    static MyGLRenderContext *GetInstance(int i);

    static void DestroyInstance();

    void AdjustPosition(int x, int y, int z);

private:
    static MyGLRenderContext *m_pContext;
    static MyGLRenderContext *m_pContext2;
    GLSampleBase *m_pBeforeSample;
    GLSampleBase *m_pCurSample;
    int m_ScreenW;
    int m_ScreenH;

};


#endif //NDK_OPENGLES_3_0_MYGLRENDERCONTEXT_H
