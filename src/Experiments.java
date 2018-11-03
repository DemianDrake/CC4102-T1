package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Runs the experiments required
public class Experiments {

    public static void main(String args[]) {
        /* args[] are the powers of tenth that are used to run the experiments.
         * For example, if one wants to run the experiments with 10^2, 10^3 and 10^4, args
         * would be {"2", "3", "4"}. */

        // Create a new data generator
        ExperimentDataGenerator dataGenerator = new ExperimentDataGenerator();

        // Test generator and parseToString, uncomment to test
        // System.out.println(dataGenerator.generateProduct().parseToString());
        // System.out.println(dataGenerator.generateClient().parseToString());

        // Transform arguments to int
        int[] tenthPowers = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            tenthPowers[i] = Integer.parseInt(args[i]);
        }

        // Run first experiment
        firstExperiment(dataGenerator, tenthPowers);


    }

    private static void firstExperiment(ExperimentDataGenerator src, int[] tenthPowers) {
        System.out.println("Primer Experimento");
        for (int power : tenthPowers) {
            try {
                long[] results = firstExperimentSingleCase(src, power, ("clientes_10^" + String.valueOf(power)));
                String tablePath = (System.getProperty("user.dir") + "/clientes_10^" + String.valueOf(power));
                System.out.printf("- Resultados para 10^%d:%n", power);
                System.out.printf("  * Tiempo de inserción: %d nanosegundos%n", results[0]);
                System.out.printf("  * Tiempo de ordenamiento: %d nanosegundos%n", results[1]);
                Files.delete(Path.of(tablePath)); // ???
                src.resetClientId();
            } catch (IOException e) {
                System.out.printf("Couldn't complete first experiment:%n" +
                        "Couldn't create database for 10^%d%n", power);
                System.exit(1);
            }
        }
    }

    private static long[] firstExperimentSingleCase(ExperimentDataGenerator src, int tenthPower, String tableName)
            throws IOException {
        String workingDir = System.getProperty("user.dir");
        Database db = new Database(workingDir);
//        String queryString = ("CREATE TABLE " + tableName + " id precio puntosNec puntosRec");
        db.createTable(tableName);
//        db.query(queryString);

        long insertTime = 0;
        long orderTime;
        long preTime, postTime;
        int totalInserts = (int) Math.pow(10, tenthPower);

        // Insert
        if (totalInserts > 100) {
            nodo[] nodeInserts = new nodo[1000];
            for (int i = 0; i < totalInserts; i += 1000) {
                for (int j = 0; j < 1000; j++) {
                    nodeInserts[j] = src.generateClient();
                }
                preTime = System.nanoTime();
                for (int j = 0; j < 1000; j++) {
                    // TODO crear función para insertar nodos en tabla
                    db.insert(nodeInserts[i]);
                }
                postTime = System.nanoTime();
                insertTime += (postTime - preTime);
            }
        } else {
            nodo[] nodeInserts = new nodo[totalInserts];
            for (int i = 0; i < totalInserts; i++) {
                nodeInserts[i] = src.generateClient();
            }
            preTime = System.nanoTime();
            for (int i = 0; i < totalInserts; i++) {
                // TODO crear función para insertar nodos en tabla
                db.insert(nodeInserts[i]);
            }
            postTime = System.nanoTime();
            insertTime = postTime - preTime;
        }

        // Order
        // TODO Hacer la query correcta, quizas crear metodo para ejecutar order by saltando el parse de la query
//        queryString = (/* "SELECT * FROM " + tableName + */ " ORDER BY puntosNec");
        preTime = System.nanoTime();
  //      db.query(queryString);
        postTime = System.nanoTime();
        orderTime = (postTime - preTime);

        return new long[]{insertTime, orderTime};

    }

}
