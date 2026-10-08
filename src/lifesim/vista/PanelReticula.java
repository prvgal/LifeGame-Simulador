/**
 * PanelReticula.java
 *
 * @author Pablo Rivero Galvín [prvgal]
 * @version 1.0
 */

package lifesim.vista;

import lifesim.modelo.JuegoVida;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PanelReticula extends JPanel {
    private JuegoVida config;

    public PanelReticula() {
        setBackground(Estilo.FONDO_RETICULA);
    }

    /**
     * Crea la retícula con tantas celdas como quepan en el panel
     * usando el tamaño de celda indicado (en píxeles).
     */
    public void inicializarPanel(String conf, int pixelesXbacteria) {
        int cols = Math.max(40, getWidth() / pixelesXbacteria);
        int fils = Math.max(40, getHeight() / pixelesXbacteria);
        config = new JuegoVida(fils, cols);

        if (conf.equals("Aleatorio")) config.inicializarAleatorio();
        else if (conf.equals("Islas de bacterias")) config.inicializarIslasdeBacterias();
        else config.inicializarCanones();

        repaint();
    }

    public void limpiar() {
        config = null;
        repaint();
    }

    public JuegoVida getConfiguracion() { return config; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (config == null) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Estilo.TEXTO_SUAVE);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 16f));
            String msg = "Elige una configuración y pulsa «Inicio»";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            return;
        }

        int[][] matriz = config.getReticula();
        int fils = matriz.length;
        int cols = matriz[0].length;

        // la celda se adapta al tamaño actual del panel (si se redimensiona la ventana)
        int celda = Math.max(1, Math.min(getWidth() / cols, getHeight() / fils));
        int offX = (getWidth() - celda * cols) / 2;
        int offY = (getHeight() - celda * fils) / 2;

        // zona de juego un poco más clara para distinguir los bordes
        g2.setColor(new Color(22, 22, 30));
        g2.fillRect(offX, offY, celda * cols, celda * fils);

        g2.setColor(Estilo.ROSA);
        int lado = celda > 2 ? celda - 1 : celda; // dejamos 1 px de separación si cabe

        for (int i = 0; i < fils; i++)
            for (int j = 0; j < cols; j++)
                if (matriz[i][j] == 1)
                    g2.fillRect(offX + j * celda, offY + i * celda, lado, lado);
    }
}