package cubemoon;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;

public class Renderer implements GLEventListener {

    private float angle = 0f;

    private Texture texEarth;
    private Texture texMoon;
    private Texture texStars;   // ✅ NOWA tekstura tła

    private GLU glu = new GLU();
    private GLUquadric qEarth;
    private GLUquadric qMoon;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL.GL_DEPTH_TEST);

        try {
            texEarth = TextureIO.newTexture(new File("Kopernik/Earthtex.png"), true);
            texMoon  = TextureIO.newTexture(new File("Kopernik/Moontex.png"), true);
            texStars = TextureIO.newTexture(new File("Kopernik/stars.png"), true);   // ✅ Load stars

            // Pixel-art filtr
            texEarth.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            texEarth.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

            texMoon.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            texMoon.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

            texStars.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            texStars.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

        } catch (Exception e) {
            System.err.println("Błąd ładowania tekstur!");
            e.printStackTrace();
        }

        // Kule quadric
        qEarth = glu.gluNewQuadric();
        glu.gluQuadricTexture(qEarth, true);
        glu.gluQuadricNormals(qEarth, GLU.GLU_SMOOTH);

        qMoon = glu.gluNewQuadric();
        glu.gluQuadricTexture(qMoon, true);
        glu.gluQuadricNormals(qMoon, GLU.GLU_SMOOTH);

    }

    // ✅ FUNKCJA RYSOWANIA TŁA
    private void drawBackground(GL2 gl, int w, int h) {
        if (texStars == null) return;

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, w, 0, h, -1, 1);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        texStars.bind(gl);

        gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(w, 0);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(w, h);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(0, h);
        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glPopMatrix();
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        int w = drawable.getSurfaceWidth();
        int h = drawable.getSurfaceHeight();

        // ✅ RYSUJEMY TŁO NA PŁASKO
        drawBackground(gl, w, h);

        // ✅ teraz 3D
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, (double) w / h, 0.1, 100.0);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);

        /*
         ============================
               ZIEMIA – kula 3D
         ============================
        */
        gl.glPushMatrix();

        gl.glRotatef(280, 205, 133, 124); // przód kontynenty

        if (texEarth != null) texEarth.bind(gl);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        glu.gluSphere(qEarth, 2.5, 87, 12);

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();


        /*
         ============================
               KSIĘŻYC – orbita
         ============================
        */
        gl.glPushMatrix();

        float orbitR = 4.0f;
        float x = (float) (orbitR * Math.cos(Math.toRadians(angle)));
        float z = (float) (orbitR * Math.sin(Math.toRadians(angle)));

        gl.glTranslatef(x, 0, z);

        if (texMoon != null) texMoon.bind(gl);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        glu.gluSphere(qMoon, 0.7, 40, 40);

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();

        angle += 0.8f;
        if (angle > 360) angle -= 360;
    }

    @Override
    public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
        d.getGL().getGL2().glViewport(0, 0, w, h);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}
}
