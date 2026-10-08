/**
 * PanelGrafica.java
 *
 * @author Pablo Rivero Galvín [prvgal]
 * @version 1.0
 */

package lifesim.vista;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class PanelGrafica extends JPanel {
    private double[] datos;
    private int genActual;
    private final String titulo;
    private final Color color;

    public PanelGrafica(String titulo, Color color) {
        this.titulo = titulo;
        this.color = color;
        setBackground(Estilo.FONDO_GRAFICA);
        setBorder(BorderFactory.createLineBorder(Estilo.REJILLA));
    }

    public void actualizarDatos(double[] nuevosDatos, int genActual) {
        this.datos = nuevosDatos;
        this.genActual = genActual;
        repaint();
    }

    public void resetea() {
        datos = null;
        genActual = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // título
        g2.setColor(Estilo.TEXTO);
        g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
        g2.drawString(titulo, 10, 20);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));

        if (datos == null || genActual < 1) {
            g2.dispose();
            return;
        }

        // márgenes
        int izq = 48, der = 15, arriba = 32, abajo = 32;
        int w = getWidth() - izq - der;
        int h = getHeight() - arriba - abajo;
        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        // máximo actual para escalar el eje Y
        double maxVal = 1;
        for (int k = 0; k < genActual; k++) if (datos[k] > maxVal) maxVal = datos[k];

        FontMetrics fm = g2.getFontMetrics();

        // rejilla horizontal + etiquetas del eje Y
        int ticksY = 4;
        for (int j = 0; j <= ticksY; j++) {
            int y = arriba + h - (j * h / ticksY);
            g2.setColor(Estilo.REJILLA);
            g2.drawLine(izq, y, izq + w, y);

            String label = String.valueOf((int) (j * maxVal / ticksY));
            g2.setColor(Estilo.TEXTO_SUAVE);
            g2.drawString(label, izq - 6 - fm.stringWidth(label), y + 4);
        }

        // etiquetas del eje X
        int ticksX = 5;
        for (int j = 0; j <= ticksX; j++) {
            int x = izq + (j * w / ticksX);
            String label = String.valueOf(j * datos.length / ticksX);
            g2.setColor(Estilo.TEXTO_SUAVE);
            g2.drawString(label, x - fm.stringWidth(label) / 2, arriba + h + 15);
        }
        String etiquetaX = "Generaciones";
        g2.drawString(etiquetaX, izq + (w - fm.stringWidth(etiquetaX)) / 2, arriba + h + 28);

        // puntos de la curva
        int denom = Math.max(1, datos.length - 1);
        int[] xs = new int[genActual];
        int[] ys = new int[genActual];
        for (int i = 0; i < genActual; i++) {
            xs[i] = izq + (int) ((double) i / denom * w);
            ys[i] = arriba + h - (int) (datos[i] / maxVal * h);
        }

        // área bajo la curva (semitransparente)
        Polygon area = new Polygon();
        for (int i = 0; i < genActual; i++) area.addPoint(xs[i], ys[i]);
        area.addPoint(xs[genActual - 1], arriba + h);
        area.addPoint(xs[0], arriba + h);
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
        g2.fillPolygon(area);

        // línea
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(xs, ys, genActual);

        // último punto y su valor
        int ult = genActual - 1;
        g2.fillOval(xs[ult] - 3, ys[ult] - 3, 6, 6);

        String valor = String.valueOf((int) datos[ult]);
        g2.setColor(Estilo.TEXTO);
        g2.drawString(valor, getWidth() - der - fm.stringWidth(valor), 20);

        g2.dispose();
    }
}