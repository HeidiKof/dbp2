package at.campus02.dbp2.repository;

public class Application {

    public static void log(String msg) {
        System.out.println("Application:  --> " + msg);
    }

    public static void main(String[] args) {
        log("application started");
    }
}
