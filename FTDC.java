import java.util.Scanner;

public class FTDC {
    static final String lalpha = "#abcdefghijklmnopqrstuvwxyz";
    static final String ualpha = lalpha.toUpperCase();

    public static int[] braidStringToIntArray(String braid) {
        int[] b = new int[braid.length()];
        for (int i = 0; i < braid.length(); i++) {
            // a is positive, A is negative
            if (Character.isUpperCase(braid.charAt(i))) {
                b[i] = -1 * ualpha.indexOf(braid.substring(i, i + 1));
            } else {
                b[i] = lalpha.indexOf(braid.substring(i, i + 1));
            }
        }
        return b;
    }

    public static String braidIntArrayToString(int[] braid) {
        String b = "";
        for (int i = 0; i < braid.length; i++) {
            if (braid[i] < 0) {
                b += ualpha.charAt(braid[i] * -1);
            } else {
                b += lalpha.charAt(braid[i]);
            }
        }
        return b;
    }

    public static int[] inverseBraid(int[] braid) {
        int[] inverse = new int[braid.length];
        for (int i = braid.length - 1; i >= 0; i--) {
            inverse[braid.length - 1 - i] = braid[i] * -1;
        }
        return inverse;
    }

    // Finds inclusive bounds for FTDC by Prop 13.1
    public static int[] bounds(int[] braid, int n) {
        int s = Integer.MAX_VALUE;
        int r = Integer.MAX_VALUE;

        if (n <= 3) {
            for (int i = 1; i < n; i++) {
                int posi = 0;
                int negi = 0;
                for (int j = 0; j < braid.length; j++) {
                    if (Math.abs(braid[j]) == i) {
                        if (braid[j] > 0) {
                            posi++;
                        } else {
                            negi++;
                        }
                    }
                }
                r = Math.min(r, posi);
                s = Math.min(s, negi);
            }
            int[] bound = { -s, r };
            return bound;
        }
        // n >= 4
        for (int i = 1; i < n; i++) {
            int posi = 0;
            int negi = 0;
            for (int j = 2; j < braid.length - 1; j++) {
                if (Math.abs(braid[j]) == i) {
                    if (braid[j] > 0) {
                        posi++;
                    } else {
                        negi++;
                    }
                }
            }
            r = Math.min(r, posi);
            s = Math.min(s, negi);
        }
        int[] bound = { -1 * (int) (Math.ceil(s / 2)) - 1, (int) Math.ceil(r / 2) + 1};
        return bound;
    }

    public static int lcm(int n) {
        int result = 1;
        int prev = 0;
        for (int i = 1; i < n + 1; i++) {
            prev = result;
            while (result % i > 0) {
                result += prev;
            }
        }
        return result;
    }

    public static int[] ftdc(String braid, int strands) {
        int lcm2 = 2 * lcm(strands);
        String braidToLCM = braid.repeat(lcm2);
        String invBraidToLCM = braidIntArrayToString(inverseBraid(braidStringToIntArray(braidToLCM)));
        String fullTwist = lalpha.substring(1, strands).repeat(strands);

        int[] bounds = bounds(braidStringToIntArray(braidToLCM), strands);

        int[] signs = new int[bounds[1] - bounds[0] + 1];
        System.out.println("Bounds: [" + bounds[0] + ", " + bounds[1]+ "]");

        int left = bounds[0];
        int right = bounds[1];

        boolean flag = false;
        int floor = 0;
        while (left <= right) { // binary search
            int mid = left + (right - left) / 2;
            String toReduce;
            if (mid == 0) {
                toReduce = invBraidToLCM;
            } else if (mid > 0) {
                toReduce = fullTwist.repeat(mid) + invBraidToLCM;
            } else {
                toReduce = braidIntArrayToString(inverseBraid(braidStringToIntArray(fullTwist.repeat(mid * -1))))
                        + invBraidToLCM;
            }
            System.out.print("Testing " + mid);
            String newBraid = HandleReduction.dacHandleReduce(toReduce, strands);
            //System.out.print("test: " + mid + ", braid: " + toReduce + " -> " + newBraid);
            if (newBraid.equals("")) { // it is a power of the full twist
                System.out.println(" - a power of the full twist");
                flag = true;
                floor = mid;
                break;
            } else if (HandleReduction.isPositiveBraid(newBraid)) { // move left
                System.out.println(" - positive");
                signs[mid - bounds[0]] = 1;
                right = mid - 1;
            } else {
                System.out.println(" - negative");
                signs[mid - bounds[0]] = -1;
                left = mid + 1;
            }
        }
        if (!flag) {
            for (int i = 0; i < signs.length - 1; i++) {
                if (signs[i] == -1 && signs[i + 1] == 1) {
                    // found!
                    floor = i + bounds[0];
                }
            }
        }
        //System.out.println("Floor: " + floor);
        int[] frac = new int[2];
        if (floor % 2 == 0) {
            frac[0] = floor;
            frac[1] = lcm2;
        } else {
            frac[0] = floor + 1;
            frac[1] = lcm2;
        }
        return frac;
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static String asFraction(int num, int den) {
        if (den == 0 && num == 0) {
            return "NaN";
        }
        int gcd = gcd(num, den);
        if (den / gcd == 1) {
            return "" + (num / gcd);
        }
        if (num * den > 0) {
            return (num / gcd) + "/" + (den / gcd);
        }
        
        return "-" + Math.abs(num / gcd) + "/" + Math.abs(den / gcd);
    }

    public static boolean isValidBraid(int[] b, int strands) {
        for(int i : b) {
            if (Math.abs(i) >= strands) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("How many strands? ");
        int strands = Integer.parseInt(input.nextLine().trim());
        System.out.print("Enter your braid word (a string of [A-Za-z] characters): ");
        String sbraid = input.nextLine().trim();
        input.close();
        if (!isValidBraid(braidStringToIntArray(sbraid), strands)) {
            System.out.println("Invalid braid!");
            System.exit(0);
        }
        long startTime = System.currentTimeMillis();
        int[] frac = ftdc(sbraid, strands);
        long endTime = System.currentTimeMillis();
        System.out.println("FTDC: " + asFraction(frac[0], frac[1]));
        System.out.println("The process took " + (endTime - startTime) + "ms to execute.");
    }
}