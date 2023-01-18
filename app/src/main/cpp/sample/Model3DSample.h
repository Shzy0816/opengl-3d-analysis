#ifndef NDK_OPENGLES_3_0_MODEL3DSample_H
#define NDK_OPENGLES_3_0_MODEL3DSample_H


#include <detail/type_mat.hpp>
#include <detail/type_mat4x4.hpp>
#include <shader.h>
#include <model.h>
#include "GLSampleBase.h"

class Model3DSample : public GLSampleBase {
public:
    Model3DSample(string fileName);

    virtual ~Model3DSample();

    virtual void LoadImage(NativeImage *pImage);

    virtual void Init();

    virtual void Draw(int screenW, int screenH);

    virtual void Destroy();

    virtual void UpdateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY);

    void UpdateMVPMatrix(glm::mat4 &mvpMatrix, int angleX, int angleY, float ratio);

    void initMatrix();

    virtual void setViewMatrix(int eyeX, int eyeY, int eyeZ,
                               int lookAtX, int lookAtY, int lookAtZ,
                               int upX, int upY, int upZ);

    virtual void AdjustModelPosition(int x, int y, int z);

private:
    glm::mat4 m_MVPMatrix;
    glm::mat4 m_ModelMatrix;
    glm::mat4 m_ProjectionMatrix;
    glm::mat4 m_ViewMatrix;
    glm::vec3 m_ViewPosition;
    Shader *m_pShader;
    Model *m_pModel;

    int m_AngleX;
    int m_AngleY;
    float m_ScaleX;
    float m_ScaleY;

    int m_adjustX;
    int m_adjustY;
    int m_adjustZ;

    string fileName;
};


#endif //NDK_OPENGLES_3_0_MODEL3DSample_H
