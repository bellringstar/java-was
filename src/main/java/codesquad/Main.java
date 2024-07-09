package codesquad;

import codesquad.webserver.conatiner.ApplicationContext;
import codesquad.webserver.conatiner.SimpleApplicationContext;


public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new SimpleApplicationContext();
        applicationContext.initialize(Main.class);
    }
}
