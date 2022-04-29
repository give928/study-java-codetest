package com.give928.java.codetest.study;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private long THRESHOLD;

    public FindSlowTestExtension(long THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        String testClassName = context.getRequiredTestClass().getName();
        String testMethodName = context.getRequiredTestMethod().getName();
        ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));
        store.put("START_TIME", System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method requiredTestMethod = context.getRequiredTestMethod();
        SlowTest slowTest = requiredTestMethod.getAnnotation(SlowTest.class);
        if (slowTest == null) {
            String testClassName = context.getRequiredTestClass().getName();
            String testMethodName = requiredTestMethod.getName();
            ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));
            long startTime = store.remove("START_TIME", long.class);
            long duration = System.currentTimeMillis() - startTime;
            if (duration > THRESHOLD) {
                System.out.printf("[Warning] Please consider mark method [%s.%s] with @SlowTest.\n", testClassName, testMethodName);
            }
        }
    }
}
