package com.leishuai;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description TODO
 * @Author leishuai
 * @Date 2019/3/20 18:23
 * @Version 1.0
 */
@Configuration
@Import(A.class)
public class Test2 {
    //    @Bean
//    A getA(){
//        return new A();
//    }
    @Bean
    Tool getTool() {
        return new Tool();
    }

    public static void main(String[] args) {
//        ApplicationContext context=new AnnotationConfigApplicationContext(Test2.class);
//        System.out.println(context.getBean("getA").getClass());
//        System.out.println(context.getBean("getA"));
//        ApplicationContext context=new AnnotationConfigApplicationContext(A.class);
        ApplicationContext context2 = new AnnotationConfigApplicationContext(Test2.class);
//        System.out.println(context.getBean("tool"));
        System.out.println(context2.getBean("tool"));
//        System.out.println(context2.getBean("getTool"));
//        System.out.println(context.getBean("a"));

    }
}

@ComponentScan
@Component
class A {
    void ff() {
        System.out.println("这是A");
    }
}

@Component
class Tool {
//    Map getClass() {
//        Map<String, byte[]> results = null;
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
//        try  {
//            MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager);
//                JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
//                JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null, Arrays.asList(javaFileObject));
//                if (task.call()) {
//                    results = manager.getClassBytes();
//                }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return results;
//    }
}