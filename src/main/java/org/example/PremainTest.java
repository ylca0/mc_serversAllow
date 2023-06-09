package org.example;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;


public class PremainTest {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("\n\n\n\nagent跑起来啦！=====================================");
        inst.addTransformer(new JavassistTransformer(), true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        inst.addTransformer(new JavassistTransformer(), true);
        Class classes[] = inst.getAllLoadedClasses();
        for (int i = 0; i < classes.length; i++) {
            if (classes[i].getName().equals("YggdrasilSocialInteractionsService")) {
                System.out.println("成功转换类：" + classes[i].getName());
                inst.retransformClasses(classes[i]);
                break;
            }
        }
    }

    static class JavassistTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

            if ("com/mojang/authlib/yggdrasil/YggdrasilSocialInteractionsService".equals(className)) {
                try {
                    System.out.println("正在转换类名：" + className);

                    ClassPool classPool = ClassPool.getDefault();

                    CtClass clazz = classPool.get("com.mojang.authlib.yggdrasil.YggdrasilSocialInteractionsService");

                    CtMethod method = clazz.getDeclaredMethod("checkPrivileges");

                    // 修改方法
                    method.setBody("{\n" +
                            "        chatAllowed = true;\n" +
                            "        serversAllowed = true;\n" +
                            "        realmsAllowed = true;\n" +
                            "    }");

                    System.out.println("成功修改checkPrivileges方法！方法签名：" + method.getLongName());

                    byte[] bytes = clazz.toBytecode();
                    clazz.detach();

                    return bytes;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return classfileBuffer;
        }
    }
}

