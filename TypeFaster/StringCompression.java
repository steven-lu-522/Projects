

public class StringCompression {
    //Lowers the amount of space needed to hold a String (e.g. int, int[], int[][])
    private static final char[] DICT = { '\u0000', '`','\n', ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '{', '}', '[', ']',
            '.', ',', '?', ':', ';', '\'', '<', '>', '/', '\"', '\\', '-', '_', '=', '+', '~',
            '|', '\t'};
    public static int intForChar(char c) {
        //converts a character into a 2-digit integer
        for (int i = 0; i < DICT.length; i++) {
            if (DICT[i] == c) {
                return i;
            }
        }
        //Return 0 if character can't be found.
        return 0;
    }
    public static char charForInt(int i) {
        return DICT[i];
    }
    public static int shortStringToInt(String s) {
        //converts a String object that can be represented as a single 32-bit integer
        int retval = 0;
        for (int i = 0; i < s.length(); i++) {
            int j = intForChar(s.charAt(i));
            //If the character can't be found (is not a keyboard character), then don't include.
            if (j != 0) {
                retval *= 100;
                retval += j;
            }
    }
        return retval;
    }
    public static int[] compressString(String s) {
        int[] temp = new int[(s.length() / 4) + 1];
        int count = 0;
        int first = 0;
        int last = 0;
        while (last < s.length()) {
            last += 5;
            if (last > s.length()) {
                last = s.length();

            } else if (!isShortString(s.substring(first, last))) {
                last -= 1;
            }
            temp[count] = shortStringToInt(s.substring(first, last));
            count++;
            first = last;
        }
        int[] retval = new int[count];
        for (int i = 0; i < count; i++) {
            retval[i] = temp[i];
        }
        return retval;
    }
    public static int[][] compressStringArray(String[] s) {
        int maxLength = 0;
        for (int j = 0; j < s.length; j++) {
            if (s[j].length() > maxLength) {
                maxLength = s[j].length();
            }
        }
        int[][] temp = new int[s.length][(maxLength / 4) + 1];
        for (int i = 0; i < s.length; i++) {
            temp[i] = compressString(s[i]);
        }
        return temp;
    }
    public static String[] decompressStringArray(int[][] arr) {
        String[] retval = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            retval[i] = decompressString(arr[i]);
        }
        return retval;
    }
    public static String decompressString(int[] compressedArr) {
        String retval = "";
        for (int i = 0; i < compressedArr.length; i++) {
            retval += intToShortString(compressedArr[i]);
        }
        return retval;
    }
    public static String intToShortString(int num) {
        String retval = "";
        while (num > 0) {
            retval = charForInt(num % 100) + retval;
            num /= 100;
        }
        return retval;
    }
    public static boolean isShortString(String s) {
        return (s.length() < 5) || ((s.length() == 5) && (intForChar(s.charAt(0)) < 20));
    }
    public static int getNumChars() {
        return DICT.length;
    }
    public static void main(String[] args) {
//        String[] arr = {"LMAOOO thIS aCTu411y w0rkss!*`~`", "-+d-f2561fv5222344f"};
//        int[][] temp = stringArrToInt(arr);
//        arr = intArr2DToStringArr(temp);
//        for (int i = 0; i < arr.length; i++) {
//            System.out.println(arr[i]);
//        }
    }
}
