import com.qqwing.Difficulty;
import com.qqwing.QQWing;

import java.util.*;


public class GeneticAlgorithm {

    static final int TAM = 9;

    private static List<Integer> limitRanges = new ArrayList<>();

    static double mutateRate = 0.08;

    static double crossoverRate = 0.6;

    static final int numIndividuals = 100000;

    static int MIN = 18;
    static int MAX = 162;

    public static void main(String[] args) {
        int [] initialSudoku = ExampleQQWing.computePuzzleWithNHolesPerRow(9);
        /*QQWing qq = new QQWing();
        qq.setRecordHistory(true);
        qq.setLogHistory(false);
        qq.generatePuzzle();
        int [] initialSudoku = qq.getPuzzle();*/

        /* THIS IS AUXILIAR IN ORDER TO SHOW THE MATRIX IN A MAZE WAY ... */

        for (int k = 0; k < TAM; k++) {
            for (int w = 0; w < TAM; w++) {
                System.out.print(initialSudoku[k * TAM + w] + " ");
            }
            System.out.println();
        }

        System.out.println("-----------------------------------------");

        List<int []> listOfIndividuals = new ArrayList<>();

        int [] initialPopulation = generateInitialIndividual(initialSudoku);
        listOfIndividuals.add(initialPopulation);

        for (int x = 1; x <= (numIndividuals - 1); x++) {
            int [] auxPermutation = permute(initialPopulation,initialSudoku);
            listOfIndividuals.add(auxPermutation);
        }

        /* THIS IS AUXILIAR IN ORDER TO SHOW THE DIFFERENT INDIVIDUALS AND THEIR FITNESS ... */

        boolean encontrado = false;

        while(!encontrado) {
            encontrado = newPopulation(encontrado,listOfIndividuals,initialSudoku);
        }

        int [] aux = listOfIndividuals.get(0);
        int cnt = 0;
        System.out.println("Sudoku resuelto: ");
        for (int k = 0; k < TAM; k++) {
            for (int w = 0; w < TAM; w++) {
                int num = initialSudoku[k * TAM + w];
                if (num != 0) {
                    System.out.print(num + " ");
                } else {
                    System.out.print(aux[cnt] + " ");
                    cnt++;
                }
            }
            System.out.println();
        }
    }

    private static boolean newPopulation (boolean encontrado, List<int[]> listOfIndividuals, int[] initialSudoku) {
        boolean returnBoolean = false;
        List<int[]> aux = new ArrayList<>();
        int añadidos = 0;
        int cnt = 0;
        int min = 162;
        int max = 0;
        Random alea = new Random();
        Random aleaCrossover = new Random();
        while (añadidos != numIndividuals && !returnBoolean) {
            int rnd = alea.nextInt(101);
            int f = fitness(listOfIndividuals.get(cnt),initialSudoku);
            if(f > max) {
                max = f;
            }
            if(f < min) {
                min = f;
            }

            int p;
            if (MAX == MIN) {
                p = 100;
            } else {
                mutateRate = 0.08;
                p = Math.round(((100 * f) - MIN*100) / (MAX-MIN));
            }
            if (f == 162) {
                returnBoolean = true;
                aux.add(0,listOfIndividuals.get(cnt));
                continue;
            } else if (p >= rnd) {
                aux.add(listOfIndividuals.get(cnt));
                añadidos++;
                if (añadidos % 2 == 0) {
                    double auxRndCrossover = aleaCrossover.nextDouble();
                    if (auxRndCrossover <= crossoverRate) {
                        crossover(aux,añadidos-2,añadidos-1);
                    }
                }
            }

            if (cnt + 1 == numIndividuals) {
                cnt = 0;
            } else {
                cnt++;
            }
        }

        if (!returnBoolean) {
            mutationGroup(aux,initialSudoku);
        }

        listOfIndividuals.clear();

        for (int j = 0; j < aux.size(); j++) {
            listOfIndividuals.add(aux.get(j));
        }

        MAX = max;
        MIN = min;
        System.out.println("Min: " + MIN);
        System.out.println();
        System.out.println("Max: " + MAX);
        System.out.println();

        return returnBoolean;
    }

    private static void mutationGroup (List<int[]> aux, int[] initialSudoku) {
        Random alea = new Random();
        for (int i = 0; i < aux.size(); i++) {
            double r = alea.nextDouble();
            if (r <= mutateRate) {
                mutation(aux.get(i));
            }
        }
    }

    private static void mutation(int[] aux) {
        Random alea = new Random();
        int rndLimit = alea.nextInt(9);
        int limtInf, limitSup;
        if (rndLimit == 0) {
            limtInf = 0;
            limitSup = limitRanges.get(0);
        } else if (rndLimit == 8) {
            limtInf = limitRanges.get(rndLimit-1);
            limitSup = aux.length;
        } else {
            limtInf = limitRanges.get(rndLimit - 1);
            limitSup = limitRanges.get(rndLimit);
        }
        int rnd1 = alea.nextInt(limtInf,limitSup);
        int rnd2 = alea.nextInt(limtInf,limitSup);
        while (rnd1 == rnd2) {
            rnd2 = alea.nextInt(limtInf,limitSup);
        }

        int auxx = aux[rnd1];
        aux[rnd1] = aux[rnd2];
        aux[rnd2] = auxx;
    }

    private static void crossover (List<int[]> aux, int i, int j) {
        Random alea = new Random();
        int rndCrosIndex = alea.nextInt(limitRanges.size());
        int CLimit = limitRanges.get(rndCrosIndex);

        int [] fAux = aux.get(i).clone();
        int [] sAux = aux.get(j).clone();

        int [] first = new int [fAux.length];
        int [] second = new int [sAux.length];

        for (int k = 0; k < CLimit; k++) {
            first[k] = fAux[k];
            second[k] = sAux[k];
        }
        for (int w = CLimit; w < fAux.length; w++) {
            first[w] = sAux[w];
            second[w] = fAux[w];
        }

        aux.remove(j);
        aux.remove(i);
        aux.add(first);
        aux.add(second);
    }

    private static int fitness(int[] individual, int[] initialSudoku) {
        int result = 0;
        int [] sudokuCompleted = initialSudoku.clone();
        int cnt = 0;
        for (int i = 0; i < sudokuCompleted.length; i++) {
            if (sudokuCompleted[i] == 0) {
                sudokuCompleted[i] = individual[cnt];
                cnt++;
            }
        }

        for (int col = 0; col < 9; col++) {
            Set<Integer> uniqueValues = new HashSet<>();
            for (int row = 0; row < 9; row++) {
                uniqueValues.add(sudokuCompleted[row * 9 + col]);
            }
            result += uniqueValues.size();
        }

        for (int quadrantRow = 0; quadrantRow < 3; quadrantRow++) {
            for (int quadrantCol = 0; quadrantCol < 3; quadrantCol++) {
                Set<Integer> uniqueValues = new HashSet<>();
                for (int row = quadrantRow * 3; row < quadrantRow * 3 + 3; row++) {
                    for (int col = quadrantCol * 3; col < quadrantCol * 3 + 3; col++) {
                        uniqueValues.add(sudokuCompleted[row * 9 + col]);
                    }
                }
                result += uniqueValues.size();
            }
        }
        return result;
    }

    private static int[] permute (int[] initialPopulation, int[] initialSudoku) {
        int [] permutation = initialPopulation.clone();
        int auxSt = 0;
        for (int k = 0; k < TAM; k++) {
            int cnt = 0;
            for (int w = 0; w < TAM; w++) {
                int element = initialSudoku[k * TAM + w];
                if (element == 0) cnt++;
            }
            shuffleSubarray(permutation,auxSt,auxSt + cnt - 1);
            auxSt = auxSt + cnt;
        }
        return permutation;
    }

    private static void shuffleSubarray(int[] arr, int start, int end) {
        Random rnd = new Random();
        for (int i = end; i > start; i--) {
            int index = rnd.nextInt(i - start + 1) + start;
            int tmp = arr[index];
            arr[index] = arr[i];
            arr[i] = tmp;
        }
    }

    private static int[] generateInitialIndividual (int[] initialSudoku) {
        int tamIndividual = numberOfEmptyCells(initialSudoku);
        int [] aux = new int[tamIndividual];
        int cnt = 0;
        int rn = 0;
        for (int k = 0; k < TAM; k++) {
            List<Integer> auxList1 = new ArrayList<>();
            for (int w = 0; w < TAM; w++) {
                int element = initialSudoku[k * TAM + w];
                if (element != 0) {
                    auxList1.add(element);
                } else {
                    ++rn;
                }
            }
            limitRanges.add(rn);

            List<Integer> auxList2 = elementsNotInRow(auxList1);
            for (int w = 0; w < TAM; w++) {
                int element = initialSudoku[k * TAM + w];
                if (element == 0) {
                    aux[cnt] = auxList2.get(0);
                    cnt++;
                    auxList2.remove(0);
                }
            }
        }

        limitRanges.remove(limitRanges.size() - 1);

        return aux;
    }

    private static List<Integer> elementsNotInRow(List<Integer> auxList1) {
        List<Integer> a = new ArrayList<>();
        for (int i = 1; i <= TAM; i++) {
            if (!auxList1.contains(i)) {
                a.add(i);
            }
        }
        return a;
    }

    private static int numberOfEmptyCells(int[] initialSudoku) {
        int cnt = 0;
        for (int i = 0; i < initialSudoku.length; i++) {
            if (initialSudoku[i] == 0) {
                cnt++;
            }
        }
        return cnt;
    }

    private static void showSudoku(int[][] sudokuVisual) {
        for (int k = 0; k < sudokuVisual.length; k++) {
            for (int w = 0; w < sudokuVisual[k].length; w++) {
                System.out.print(sudokuVisual[k][w] + " ");
            }
            System.out.println();
        }
    }
}
