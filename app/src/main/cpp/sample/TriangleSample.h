#ifndef NDK_OPENGLES_3_0_TRIANGLESAMPLE_H
#define NDK_OPENGLES_3_0_TRIANGLESAMPLE_H


#include "GLSampleBase.h"

class TriangleSample : public GLSampleBase
{
public:
	TriangleSample();
	virtual ~TriangleSample();

	virtual void LoadImage(NativeImage *pImage);

	virtual void Init();

	virtual void Draw(int screenW, int screenH);

	virtual void Destroy();

	virtual void setViewMatrix(int eyeX, int eyeY, int eyeZ,
							   int lookAtX, int lookAtY, int lookAtZ,
							   int upX, int upY, int upZ);
	virtual void AdjustModelPosition(int x, int y, int z);

};


#endif //NDK_OPENGLES_3_0_TRIANGLESAMPLE_H
