package lifesim.modelo;

import java.util.Random;

public class JuegoVida {
    private int[][] reticulaActual;
    private int[][] reticulaAnterior;
    private final int fils;
    private final int cols;

    public JuegoVida(int fils, int cols) {
        this.fils = fils;
        this.cols = cols;
        this.reticulaActual = new int[fils][cols];
        this.reticulaAnterior = new int[fils][cols];
    }

    // ---------- Configuraciones iniciales ----------

    public void inicializarAleatorio() {
        Random random = new Random();

        for (int i = 0; i < fils; ++i)
            for (int j = 0; j < cols; ++j)
                reticulaAnterior[i][j] = random.nextBoolean() ? 1 : 0;
    }

    public void inicializarIslasdeBacterias() {
        limpiarReticula();

        Random random = new Random();
        int nIslas = 10 + random.nextInt(15); // entre 10 y 24 islas

        for (int isla = 0; isla < nIslas; ++isla) {
            int origenFil = random.nextInt(fils);
            int origenCol = random.nextInt(cols);

            // tamaño de la isla: entre 3 y 14 filas/columnas
            int radioFila = 3 + random.nextInt(12);
            int radioColu = 3 + random.nextInt(12);

            for (int i = 0; i < radioFila; ++i)
                for (int j = 0; j < radioColu; ++j) {
                    int filaColocar = origenFil + i;
                    int coluColocar = origenCol + j;

                    if (filaColocar < fils && coluColocar < cols)
                        if (random.nextDouble() < 0.5) // 50% de probabilidad
                            reticulaAnterior[filaColocar][coluColocar] = 1;
                }
        }
    }

    public void inicializarCanones() {
        limpiarReticula();

        // el cañón ocupa unas 36 columnas por 9 filas; lo centramos
        int f = fils / 2 - 5;
        int c = cols / 2 - 18;

        // coordenadas relativas del Gosper Glider Gun
        int[][] puntos = {
            // bloque izquierdo
            {5, 1}, {5, 2}, {6, 1}, {6, 2},
            // estructura circular izquierda
            {5, 11}, {6, 11}, {7, 11}, {4, 12}, {8, 12}, {3, 13},
            {9, 13}, {3, 14}, {9, 14}, {6, 15}, {4, 16}, {8, 16},
            {5, 17}, {6, 17}, {7, 17}, {6, 18},
            // estructura derecha
            {3, 21}, {4, 21}, {5, 21}, {3, 22}, {4, 22}, {5, 22},
            {2, 23}, {6, 23}, {1, 25}, {2, 25}, {6, 25}, {7, 25},
            // bloque derecho
            {3, 35}, {3, 36}, {4, 35}, {4, 36}
        };

        for (int[] punto : puntos) {
            int filDestino = f + punto[0];
            int colDestino = c + punto[1];

            if (filDestino >= 0 && filDestino < fils && colDestino >= 0 && colDestino < cols)
                reticulaAnterior[filDestino][colDestino] = 1;
        }
    }

    // ---------- Evolución ----------

    public void siguienteGeneracion() {
        for (int i = 0; i < fils; ++i)
            for (int j = 0; j < cols; ++j)
                reticulaActual[i][j] = funcTransicion(i, j, contarVecinosVivos(i, j));

        // intercambiamos las dos retículas
        int[][] temp = reticulaAnterior;
        reticulaAnterior = reticulaActual;
        reticulaActual = temp;
    }

    private int contarVecinosVivos(int fil, int col) {
        int vivos = 0;

        for (int i = -1; i < 2; ++i)
            for (int j = -1; j < 2; ++j) {
                if (i == 0 && j == 0) continue; // ignoramos la celda central

                int numFil = fil + i;
                int numCol = col + j;

                if (numFil >= 0 && numFil < fils && numCol >= 0 && numCol < cols)
                    vivos += reticulaAnterior[numFil][numCol];
            }

        return vivos;
    }

    private int funcTransicion(int fil, int col, int nVec) {
        if (reticulaAnterior[fil][col] == 1)
            return (nVec == 2 || nVec == 3) ? 1 : 0; // supervivencia / muerte
        return (nVec == 3) ? 1 : 0;                  // reproducción
    }

    // ---------- Estadísticas y acceso ----------

    /** @return [0] = vivas, [1] = muertas */
    public int[] numBacteriasVivasYMuertas() {
        int[] resultado = new int[2];

        for (int i = 0; i < fils; ++i)
            for (int j = 0; j < cols; ++j)
                if (reticulaAnterior[i][j] == 1) ++resultado[0];
                else ++resultado[1];

        return resultado;
    }

    private void limpiarReticula() {
        for (int i = 0; i < fils; ++i)
            for (int j = 0; j < cols; ++j)
                reticulaAnterior[i][j] = 0;
    }

    public int[][] getReticula() { return reticulaAnterior; }
    public int getFils() { return fils; }
    public int getCols() { return cols; }
}