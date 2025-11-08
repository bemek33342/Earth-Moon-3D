package cubemoon;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class CubeMoon {

    public static void main(String[] args) {

        // stabilne profile OpenGL dla Windows
        System.setProperty("jogl.disable.openglcore", "true");
        System.setProperty("jogl.disable.opengles", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                GLProfile.initSingleton();
                GLProfile glProfile = GLProfile.get(GLProfile.GL2);

                GLCapabilities caps = new GLCapabilities(glProfile);
                caps.setDoubleBuffered(true);
                caps.setHardwareAccelerated(true);
                caps.setDepthBits(24);

                GLCanvas canvas = new GLCanvas(caps);
                Renderer renderer = new Renderer();
                canvas.addGLEventListener(renderer);

                FPSAnimator animator = new FPSAnimator(canvas, 60, true);
                animator.start();

                JFrame frame = new JFrame("Earth & Moon 3D");
                frame.add(canvas);
                frame.setSize(900, 700);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                System.err.println("INIT ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
