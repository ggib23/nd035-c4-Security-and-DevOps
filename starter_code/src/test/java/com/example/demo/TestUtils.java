package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {
    // Purpose of test class is to help us inject objects
    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName); 

            // Check if the field is private
            if (!field.canAccess(target)) {
                field.setAccessible(true);
                wasPrivate = true;
            }

            field.set(target, toInject);

            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
