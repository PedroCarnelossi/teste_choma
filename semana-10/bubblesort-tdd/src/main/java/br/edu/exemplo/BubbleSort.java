package br.edu.exemplo;

public class BubbleSort {

    private int[] numeros;

    public BubbleSort(int[] numeros) {
        this.numeros = numeros;
    }

    public void ordenar() {
        for (int fim = numeros.length - 1; fim > 0; fim--) {
            boolean houveTroca = false;

            for (int i = 0; i < fim; i++) {
                if (numeros[i] > numeros[i + 1]) {
                    trocar(i, i + 1);
                    houveTroca = true;
                }
            }

            if (!houveTroca) {
                return;
            }
        }
    }

    private void trocar(int i, int j) {
        int auxiliar = numeros[i];
        numeros[i] = numeros[j];
        numeros[j] = auxiliar;
    }

    public int[] getNumeros() {
        return numeros;
    }
}
