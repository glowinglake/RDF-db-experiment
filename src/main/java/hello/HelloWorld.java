package hello;

import parser.ListInt;

public class HelloWorld {
    public static void main(String[] args) {
        Greeter greeter = new Greeter();
        System.out.println(greeter.sayHello());
        ListInt p = new ListInt();
        p.run();
        p.test();
    }
}