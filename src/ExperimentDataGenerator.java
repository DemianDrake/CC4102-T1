package src;

import java.util.concurrent.ThreadLocalRandom;

// Generates data for the experiments
public class ExperimentDataGenerator {

    private int currentProductId;
    private int currentClientId;

    // Creates a new data generator
    public ExperimentDataGenerator() {
        this.currentProductId = 1;
        this.currentClientId = 1;
    }

    // Resets current product id of the generator
    public void resetProductId() {
        this.currentProductId = 1;
    }

    // Resets current client id of the generator
    public void resetClientId() {
        this.currentClientId = 1;
    }

    /* Creates a new random product. Prices are generated randomly between 100 and 10000000 and
     * point scaling is based on the fidelity program used by Cencosud. */
    public nodo generateProduct() {
        int randomPrice = ThreadLocalRandom.current().nextInt(100, 10000001);
        double necessaryPointsScaling = ThreadLocalRandom.current().nextDouble(0.35, 0.6);
        double prizePointsScaling = ThreadLocalRandom.current().nextDouble(0.004, 0.12);
        nodo newProduct = new nodo();
        newProduct.put("id", currentProductId);
        this.currentProductId += 1;
        newProduct.put("precio", randomPrice);
        newProduct.put("puntosNec", (int) (randomPrice * necessaryPointsScaling));
        newProduct.put("puntosRec", (int) (randomPrice * prizePointsScaling));
        return newProduct;
    }

    /* Creates a new random client. Generates a RUT between 1-9 and 30.000.000-2 (with a valid
     * verificator digit) and a point amount random between 0 and 10000000. */
    public nodo generateClient() {
        int randomPoints = ThreadLocalRandom.current().nextInt(100, 10000001);
        int randomRut = ThreadLocalRandom.current().nextInt(1, 30000001);
        // Calculate verifier number
        int randomRutCopy = randomRut;
        int[] rutMultipliers = {2, 3, 4, 5, 6, 7};
        int rutVerifier = 0;
        int i = 0;
        while (randomRutCopy != 0) {
            rutVerifier += ((randomRutCopy % 10) * (rutMultipliers[i % 6]));
            randomRutCopy /= 10;
            i++;
        }
        rutVerifier = rutVerifier % 11;
        rutVerifier = 11 - rutVerifier;
        String verifier;
        if (rutVerifier < 10) {
            verifier = String.valueOf(rutVerifier);
        } else if (rutVerifier == 10) {
            verifier = "K";
        } else {
            verifier = "0";
        }
        String rut = String.valueOf(randomRut) + "-" + verifier;
        // Create new client
        nodo newClient = new nodo();
        newClient.put("id", currentClientId);
        this.currentClientId += 1;
        newClient.put("rut", rut);
        newClient.put("puntosAcumulados", randomPoints);
        return newClient;
    }

}
