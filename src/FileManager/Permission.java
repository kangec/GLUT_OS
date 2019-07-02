package FileManager;

public class Permission {
    public static final String READ = "r";
    public static final String WRITE= "w";

    public static boolean canRead(String p) {
        if(p.equals(READ + "-") || p.equals(READ + WRITE)) {
            return true;
        }else
            return false;
    }
    public static boolean canWrite(String p) {
        if(p.equals(WRITE + "-") || p.equals(READ + WRITE)) {
            return true;
        }else
            return false;
    }
}
