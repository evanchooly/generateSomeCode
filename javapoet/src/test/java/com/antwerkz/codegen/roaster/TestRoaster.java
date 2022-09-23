package com.antwerkz.codegen.roaster;

import com.antwerkz.generated.HelloWorld;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRoaster {
    @Test
    public void testHello() {
        HelloWorld hello = new HelloWorld();
        Assert.assertEquals(hello.greeting(), "Hello world!");
        hello.greet();
    }

}
