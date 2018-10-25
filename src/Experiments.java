package src;

// Runs the experiments required
public class Experiments {

    public static void main(String args[]){
        // Create a new data generator
        ExperimentDataGenerator dataGenerator = new ExperimentDataGenerator();

        // Test generator and parseToString, TODO: remove next lines when done
        System.out.println(dataGenerator.generateProduct().parseToSring());
        System.out.println(dataGenerator.generateClient().parseToSring());
    }

}
