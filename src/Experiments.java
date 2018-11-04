package src;

import java.nio.file.Files;
import java.nio.file.Paths;


// Runs the experiments required
public class Experiments {

    public static void main(String args[]) {
        /* args[] are the powers of tenth that are used to run the experiments, they must be integers (as Strings), and
         * the last one is used to determine if the third experiment is run: if it's 1 the experiment runs, if it's 0
         * the experiment is skipped and if it's any other exits the program with exit code 1.
         * For example, if one wants to run the experiments with 10^2, 10^3 and 10^4 and run the third experiment, args
         * would be {"2", "3", "4", "1"}. */

        // Create a new data generator
        ExperimentDataGenerator dataGenerator = new ExperimentDataGenerator();

        // Transform arguments to int
        int[] tenthPowers = new int[args.length - 1];
        for (int i = 0; i < (args.length - 1); i++) {
            tenthPowers[i] = Integer.parseInt(args[i]);
        }
        int runThird = Integer.parseInt(args[args.length - 1]);

        //Create databases for experiments
        Database simpleDb = new Database(System.getProperty("user.dir"));
        // TODO Crear db con btree, no entendi como hacerlo
        Database btreeDb = new Database(System.getProperty("user.dir"));

        // Run first experiment
        firstExperiment(dataGenerator, tenthPowers, simpleDb);

        // Run second experiment
        secondExperiment(dataGenerator, tenthPowers, btreeDb);

        // Run third experiment
        if (runThird == 1) {
            thirdExperiment(dataGenerator, simpleDb, btreeDb);
        } else if (runThird != 0) {
            System.out.println("Argumento erroneo para el tercer experimento");
            System.exit(1);
        } else {
            System.out.println("Tercer experimento no ejecutado");
        }

    }

    private static void firstExperiment(ExperimentDataGenerator src, int[] tenthPowers, Database db) {
        System.out.println("Primer Experimento");
        generalExperiment(src, tenthPowers, db, "_1");
    }

    private static void secondExperiment(ExperimentDataGenerator src, int[] tenthPowers, Database db) {
        System.out.println("Segundo Experimento");
        generalExperiment(src, tenthPowers, db, "_2");
    }


    private static void generalExperiment(ExperimentDataGenerator src, int[] tenthPowers, Database db, String suffix) {
        for (int power : tenthPowers) {
            long[] results = experimentSingleCase(
                    src, power, ("clientes_10^" + String.valueOf(power) + suffix), db);
            System.out.printf("- Resultados para 10^%d:%n", power);
            System.out.printf("  * Tiempo de inserción: %d nanosegundos%n", results[0]);
            System.out.printf("  * Tiempo de ordenamiento: %d nanosegundos%n", results[1]);
        }
    }

    private static long[] experimentSingleCase(ExperimentDataGenerator src, int tenthPower, String tableName,
                                               Database db) {

        db.createTable(tableName);

        // Index by BTree if necessary
        if (db.usesBTree()) {
            // TODO hacer el BTree para puntosAcumulados si la DB usa BTree (no entendi como hacer una db con BTree)
        }

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
                db.insert(nodeInserts[i]);
            }
            postTime = System.nanoTime();
            insertTime = (postTime - preTime);
        }

        // Order
        preTime = System.nanoTime();
        db.order(tableName, "puntosAcumulados");
        postTime = System.nanoTime();
        orderTime = (postTime - preTime);

        return new long[]{insertTime, orderTime};

    }

    private static void thirdExperiment(ExperimentDataGenerator src, Database textDb, Database btreeDb) {
        System.out.println("Tercer Experimento");
        int[] tenthPowersClients = {1, 2, 3};
        int[] tenthPowersProducts = {1, 2, 3, 4};

        //Create tables

        String workingDir = System.getProperty("user.dir");

        // Check pre-existence of client tables
        int totalInserts;
        for (int power : tenthPowersClients) {
            String tablePathSimple = workingDir + "clientes_10^" + String.valueOf(power) + "_1";
            String tablePathBTree = workingDir + "clientes_10^" + String.valueOf(power) + "_2";
            // Text db client pre-existence
            if (!Files.exists(Paths.get(tablePathSimple))) {
                textDb.createTable("clientes_10^" + String.valueOf(power) + "_1");
                totalInserts = (int) Math.pow(10, power);
                nodo[] nodeInserts = new nodo[totalInserts];
                for (int i = 0; i < totalInserts; i++) {
                    nodeInserts[i] = src.generateClient();
                }
                for (int i = 0; i < totalInserts; i++) {
                    textDb.insert(nodeInserts[i]);
                }
            }
            // BTree db client pre-existence
            if (!Files.exists(Paths.get(tablePathBTree))) {
                btreeDb.createTable("clientes_10^" + String.valueOf(power) + "_2");
                // TODO hacer el BTree para puntosAcumulados
                totalInserts = (int) Math.pow(10, power);
                nodo[] nodeInserts = new nodo[totalInserts];
                for (int i = 0; i < totalInserts; i++) {
                    nodeInserts[i] = src.generateClient();
                }
                for (int i = 0; i < totalInserts; i++) {
                    btreeDb.insert(nodeInserts[i]);
                }
            }
        }

        // Create products tables
        for (int power : tenthPowersProducts) {
            // Text db products
            textDb.createTable("productos_10^" + String.valueOf(power) + "_1");
            totalInserts = (int) Math.pow(10, power);
            nodo[] nodeInserts = new nodo[totalInserts];
            for (int i = 0; i < totalInserts; i++) {
                nodeInserts[i] = src.generateProduct();
            }
            for (int i = 0; i < totalInserts; i++) {
                textDb.insert(nodeInserts[i]);
            }
            // BTree db products
            btreeDb.createTable("productos_10^" + String.valueOf(power) + "_2");
            // TODO hacer el BTree para puntosNec
            totalInserts = (int) Math.pow(10, power);
            nodeInserts = new nodo[totalInserts];
            for (int i = 0; i < totalInserts; i++) {
                nodeInserts[i] = src.generateProduct();
            }
            for (int i = 0; i < totalInserts; i++) {
                btreeDb.insert(nodeInserts[i]);
            }
        }

        // Run strategies
        long result1, result2, result3;
        for (int clientPower: tenthPowersClients) {
            for (int productPower: tenthPowersProducts) {
                result1 = firstStrategy(btreeDb, ("clientes_10^" + String.valueOf(clientPower) + "_2"),
                        ("productos_10^" + String.valueOf(productPower) + "_2"));
                result2 = secondStrategy(btreeDb, ("clientes_10^" + String.valueOf(clientPower) + "_2"),
                        ("productos_10^" + String.valueOf(productPower) + "_2"));
                result3 = thirdStrategy(btreeDb, ("clientes_10^" + String.valueOf(clientPower) + "_1"),
                        ("productos_10^" + String.valueOf(productPower) + "_1"));
                System.out.printf("- Resultados para 10^%d Clientes y 10^%d Productos:%n", clientPower, productPower);
                System.out.printf("  * Tiempo primera estrategia: %d nanosegundos%n", result1);
                System.out.printf("  * Tiempo segunda estrategia: %d nanosegundos%n", result2);
                System.out.printf("  * Tiempo tercera estrategia: %d nanosegundos%n", result3);
            }
        }

    }

    private static long firstStrategy(Database db, String clientTable, String productTable){
        long preTime, postTime;
        preTime = System.nanoTime();
        // TODO Hacer la primera estrategia, retornar el tiempo que toma
        postTime = System.nanoTime();
        return (postTime - preTime);
    }

    private static long secondStrategy(Database db, String clientTable, String productTable){
        long preTime, postTime;
        preTime = System.nanoTime();
        // TODO Hacer la segunda estrategia, retornar el tiempo que toma
        postTime = System.nanoTime();
        return (postTime - preTime);
    }

    private static long thirdStrategy(Database db, String clientTable, String productTable){
        long preTime, postTime;
        preTime = System.nanoTime();
        String clientOrdered = db.order(clientTable, "puntosAcumulados");
        String productOrdered = db.order(productTable, "puntosNec");
        // TODO Hacer la tercera estrategia, retornar el tiempo que toma
        postTime = System.nanoTime();
        return (postTime - preTime);
    }

}
