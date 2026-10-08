/**
 * VentanaPrincipal.java
 *
 * @author Pablo Rivero Galvín [prvgal]
 * @version 1.0
 */

package lifesim.vista;

import lifesim.modelo.JuegoVida;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class VentanaPrincipal extends JFrame {

    private static final String[] MODOS = {"Aleatorio", "Islas de bacterias", "Cañones de planeadores"};
    private static final String[] TAMANOS = {"Muy fino (3 px)", "Fino (4 px)", "Medio (6 px)", "Grande (8 px)"};
    private static final int[] PIXELES = {3, 4, 6, 8};

    private final PanelReticula panelReticula = new PanelReticula();
    private final PanelGrafica graficaPoblacion = new PanelGrafica("Población (vivas)", Estilo.ROSA);
    private final PanelGrafica graficaHuecos = new PanelGrafica("Huecos (muertas)", Estilo.CIAN);

    private final JComboBox<String> comboModo = new JComboBox<>(MODOS);
    private final JComboBox<String> comboTamano = new JComboBox<>(TAMANOS);
    private final JSpinner spinGeneraciones = new JSpinner(new SpinnerNumberModel(200, 1, 10000, 10));
    private final JSlider sliderVelocidad = new JSlider(1, 10, 6);

    private final JLabel lblGeneracion = new JLabel("Generación: 0");
    private final JLabel lblVivas = new JLabel("Vivas: -");
    private final JLabel lblMuertas = new JLabel("Muertas: -");

    private final JButton btnInicio = new JButton("Inicio");
    private final JButton btnReset = new JButton("Reset");

    private final Timer temporizador;

    private double[] bacteriasVivas;
    private double[] bacteriasMuertas;
    private int genActual;
    private int totalGens;

    public VentanaPrincipal() {
        super("El Juego de la Vida");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // si falla, usamos el aspecto por defecto
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 0));

        temporizador = new Timer(retardo(), e -> paso());

        // la retícula ocupa casi toda la ventana
        panelReticula.setPreferredSize(new Dimension(820, 700));
        JPanel contenedorReticula = new JPanel(new BorderLayout());
        contenedorReticula.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        contenedorReticula.add(panelReticula, BorderLayout.CENTER);

        add(contenedorReticula, BorderLayout.CENTER);
        add(crearPanelLateral(), BorderLayout.EAST);

        configurarEventos();

        pack();
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }

    // ---------- Construcción de la interfaz ----------

    private JPanel crearPanelLateral() {
        JPanel lateral = new JPanel(new BorderLayout(0, 10));
        lateral.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        lateral.setPreferredSize(new Dimension(380, 0));

        // parte superior: configuración y estadísticas
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JPanel config = new JPanel(new GridLayout(0, 1, 0, 4));
        config.setBorder(BorderFactory.createTitledBorder("Configuración"));
        config.add(new JLabel("Configuración inicial:"));
        config.add(comboModo);
        config.add(new JLabel("Tamaño de la bacteria:"));
        comboTamano.setSelectedIndex(1);
        config.add(comboTamano);
        config.add(new JLabel("Número de generaciones:"));
        config.add(spinGeneraciones);
        config.add(new JLabel("Velocidad:"));
        config.add(sliderVelocidad);

        JPanel stats = new JPanel(new GridLayout(1, 3, 5, 0));
        stats.setBorder(BorderFactory.createTitledBorder("Estado"));
        stats.add(lblGeneracion);
        stats.add(lblVivas);
        stats.add(lblMuertas);

        superior.add(config);
        superior.add(Box_vertical(8));
        superior.add(stats);

        // centro: las dos gráficas apiladas, ocupan todo el espacio sobrante
        JPanel graficas = new JPanel(new GridLayout(2, 1, 0, 10));
        graficas.add(graficaPoblacion);
        graficas.add(graficaHuecos);

        // parte inferior: botones
        JPanel botones = new JPanel(new GridLayout(1, 2, 8, 0));
        botones.add(btnInicio);
        botones.add(btnReset);
        btnInicio.setPreferredSize(new Dimension(0, 36));

        lateral.add(superior, BorderLayout.NORTH);
        lateral.add(graficas, BorderLayout.CENTER);
        lateral.add(botones, BorderLayout.SOUTH);
        return lateral;
    }

    private static javax.swing.Box.Filler Box_vertical(int alto) {
        return (javax.swing.Box.Filler) javax.swing.Box.createVerticalStrut(alto);
    }

    private void configurarEventos() {
        sliderVelocidad.addChangeListener(e -> temporizador.setDelay(retardo()));
        btnInicio.addActionListener(e -> alternarEjecucion());
        btnReset.addActionListener(e -> resetear());
    }

    // ---------- Lógica de control ----------

    /** Velocidad 1..10 -> retardo entre generaciones (470 ms ... 20 ms). */
    private int retardo() {
        return 520 - sliderVelocidad.getValue() * 50;
    }

    private void alternarEjecucion() {
        if (!temporizador.isRunning()) {
            if (genActual == 0) {
                totalGens = (int) spinGeneraciones.getValue();
                bacteriasVivas = new double[totalGens];
                bacteriasMuertas = new double[totalGens];
                panelReticula.inicializarPanel(
                        (String) comboModo.getSelectedItem(),
                        PIXELES[comboTamano.getSelectedIndex()]);
                activarControlesConfig(false);
            }
            temporizador.start();
            btnInicio.setText("Pausar");
        } else {
            temporizador.stop();
            btnInicio.setText("Continuar");
        }
    }

    private void paso() {
        JuegoVida juego = panelReticula.getConfiguracion();
        if (juego == null || genActual >= totalGens) {
            terminar();
            return;
        }

        juego.siguienteGeneracion();
        panelReticula.repaint();

        int[] vm = juego.numBacteriasVivasYMuertas();
        bacteriasVivas[genActual] = vm[0];
        bacteriasMuertas[genActual] = vm[1];
        genActual++;

        graficaPoblacion.actualizarDatos(bacteriasVivas, genActual);
        graficaHuecos.actualizarDatos(bacteriasMuertas, genActual);

        lblGeneracion.setText("Generación: " + genActual);
        lblVivas.setText("Vivas: " + vm[0]);
        lblMuertas.setText("Muertas: " + vm[1]);

        if (genActual >= totalGens) terminar();
    }

    private void terminar() {
        temporizador.stop();
        btnInicio.setText("Terminado");
        btnInicio.setEnabled(false);
    }

    private void resetear() {
        temporizador.stop();
        genActual = 0;
        btnInicio.setText("Inicio");
        btnInicio.setEnabled(true);
        graficaPoblacion.resetea();
        graficaHuecos.resetea();
        panelReticula.limpiar();
        lblGeneracion.setText("Generación: 0");
        lblVivas.setText("Vivas: -");
        lblMuertas.setText("Muertas: -");
        activarControlesConfig(true);
    }

    /** Bloqueamos la configuración mientras hay una simulación en curso. */
    private void activarControlesConfig(boolean activo) {
        comboModo.setEnabled(activo);
        comboTamano.setEnabled(activo);
        spinGeneraciones.setEnabled(activo);
    }
}