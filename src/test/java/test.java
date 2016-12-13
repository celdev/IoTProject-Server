public class test {

    public static void main(String[] args) {
        String r = "type=sensor     protocol=fineoffset     model=temperature       id=135  temperature=24.7        time=2016-12-02 15:50:39        age=1\n" +
                "type=sensor     protocol=fineoffset     model=temperature       id=255  temperature=-204.7      time=2016-12-02 15:50:32        age=8\n";

        int index = r.indexOf("temperature=");
        System.out.println(r.substring(index + "temperature=".length(), r.indexOf("time=")).trim());

    }
}
