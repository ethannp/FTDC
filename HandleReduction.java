import java.util.Scanner;

public class HandleReduction {
    /**
     * Returns an String consisting of the first permissible handle in the braid.
     * Returns the empty string if no handle exists.
     * 
     * @param braid The braid word to check
     * @return The first permissible handle, if there exists one.
     */
    public static String getHandle(String braid) {
        // first check for free reductions (ex. aA or Aa)
        for (int i = 0; i < braid.length() - 1; i++) {
            if (Character.toLowerCase(braid.charAt(i)) == Character.toLowerCase(braid.charAt(i + 1))) {
                if (Character.isUpperCase(braid.charAt(i)) ^ Character.isUpperCase(braid.charAt(i + 1))) {
                    return braid.substring(i, i + 2);
                }
            }
        }
        // check for other handles
        for (int i = 0; i < braid.length(); i++) {
            char startLetter = braid.charAt(i);
            boolean isLower = Character.isLowerCase(startLetter);
            String iplusone = "";
            for (int j = i + 1; j < braid.length(); j++) {
                if ((isLower && Character.toUpperCase(startLetter) == braid.charAt(j))
                        || (!isLower && Character.toLowerCase(startLetter) == braid.charAt(j))) {
                    // found handle
                    return braid.substring(i, j + 1);
                }
                // check if permissible
                if ((int) Character.toLowerCase(startLetter) + 1 == (int) Character.toLowerCase(braid.charAt(j))) {
                    if (iplusone.equals("")) {
                        iplusone = braid.substring(j, j + 1);
                    } else if (!iplusone.equals(braid.substring(j, j + 1))) {
                        // not permissible
                        break;
                    }
                }
                if ((int) Character.toLowerCase(startLetter) >= (int) Character.toLowerCase(braid.charAt(j))) {
                    // found letter that is alphabetically before or the same
                    break;
                }
            }
        }
        return "";
    }

    /**
     * Returns if a nontrivial braid (after handle reduction) is positive or
     * negative.
     * 
     * @param braid A nontrivial braid
     * @return Whether the nontrivial braid is positive or negative
     */
    public static boolean isPositiveBraid(String braid) {
        char earliestLetter = braid.charAt(0);
        for (int i = 1; i < braid.length(); i++) {
            if ((int) Character.toLowerCase(braid.charAt(i)) < (int) Character.toLowerCase(earliestLetter)) {
                earliestLetter = braid.charAt(i);
            }
        }
        return Character.isLowerCase(earliestLetter);
    }

    /**
     * Handle Reduction Algorithm
     * 
     * @param braid     The braid to perform the handle reduction algorithm on
     * @param showSteps Whether to print intermediate steps
     * @return The reduced braid
     */
    public static String handleReduce(String braid, boolean showSteps) {
        // Repeatedly check if handle exists.
        // This process terminates eventually (Prop 1.5)
        int steps = 0;
        while (true) {
            String handle = getHandle(braid);
            if (handle == "") {
                break;
            }
            steps++;
            int handleIndex = braid.indexOf(handle);
            String beforeHandle = braid.substring(0, handleIndex);
            String afterHandle = braid.substring(handleIndex + handle.length());
            String newHandle = "";
            char handleLetter = handle.charAt(0);
            boolean isLowerHandle = Character.isLowerCase(handleLetter);
            String s = "" + Character.toLowerCase(handleLetter);
            String S = "" + Character.toUpperCase(handleLetter);
            for (int i = 1; i < handle.length() - 1; i++) {
                char character = handle.charAt(i);
                if (((int) Character.toLowerCase(character)) - 1 != (int) Character.toLowerCase(handleLetter)) {
                    newHandle += character;
                    continue;
                }
                // Reduction
                String r = "" + Character.toLowerCase(character);
                String R = "" + Character.toUpperCase(character);
                if (isLowerHandle) {
                    if (Character.isLowerCase(character)) {
                        newHandle += R + s + r;
                    } else {
                        newHandle += R + S + r;
                    }
                } else {
                    if (Character.isLowerCase(character)) {
                        newHandle += r + s + R;
                    } else {
                        newHandle += r + S + R;
                    }
                }
            }
            braid = beforeHandle + newHandle + afterHandle;
            if (showSteps) {
                System.out.println("Step " + steps + ": " + beforeHandle + "(" + handle + ")" + afterHandle);
            }
        }
        return braid;
    }

    /**
     * Divide and Conquer Handle Reduction Algorithm
     * 
     * @param braid   The braid to perform the handle reduction algorithm on
     * @param strands The number of strands the braid has
     * @return The reduced braid
     */
    public static String dacHandleReduce(String braid, int strands) {
        // split braid into half until length reaches [?] times braid index (#strands)
        if (braid.length() <= 4 * strands) {
            return HandleReduction.handleReduce(braid, false);
        } else {
            int mid = braid.length() / 2;
            String leftBraid = dacHandleReduce(braid.substring(0, mid), strands);
            String rightBraid = dacHandleReduce(braid.substring(mid), strands);
            return HandleReduction.handleReduce(leftBraid + rightBraid, false);
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your braid word (a string of [A-Za-z] characters): ");
        String braid = input.nextLine().trim();
        long startTime = System.currentTimeMillis();
        braid = handleReduce(braid, true);
        long endTime = System.currentTimeMillis();
        if (braid.equals("")) {
            System.out.println("Your braid is trivial.");
        } else {
            System.out.println("Resulting braid: " + braid);
            System.out.println("Your braid is nontrivial and " + (isPositiveBraid(braid) ? "positive." : "negative.")
                    + " The process took " + (endTime - startTime) + "ms to execute.");
        }
        input.close();
    }
}