package application;

public class Random {

    private static final String[] FIRST_NAMES = {"Alex", "Bella", "Charlie", "David", "Emma", "Frank", "Grace", "Henry"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller"};

    public static String generateRandomName() {
        java.util.Random random = new java.util.Random();
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }
}
