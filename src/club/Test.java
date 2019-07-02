package club;
import org.apache.commons.lang.StringUtils;
public class Test {

    public static void main(String[] args) {
        String s = "lastF:\\Android\\sdk";

        System.out.println(StringUtils.substringBeforeLast(s,"\\"));
    }
}
