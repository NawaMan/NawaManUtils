package nawaman.utils.reflection;

import static nawaman.utils.reflection.UReflection.getValueFromStaticFieldOrNull;
import static nawaman.utils.reflection.UReflection.hasAnnotationWithName;
import static nawaman.utils.reflection.UReflection.invokeStaticMethodOrNull;
import static nawaman.utils.reflection.UReflection.isPublicStaticFinalAndCompatible;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("javadoc")
public class UReflectionTest {
    
    @Target({ ElementType.METHOD, ElementType.FIELD })  
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        
    }
    
    public static class TestClass {
        
        public static final String noAnnotation = "";
        @TestAnnotation
        public static final String withAnnotation = "";

        public static final String compatible = "42";
        public static String noFinal = "";
        public final String noStatic = "";
        @SuppressWarnings("unused")
        private static final String noPublic = "";
        public static final int noCompatible = 42;
        
        public static String fortyTwo() {
            return "42";
        }
    }
    
    @Test
    public void testHasAnnotation() throws NoSuchFieldException, SecurityException {
        assertFalse(hasAnnotationWithName(getAnnotations(TestClass.class, "noAnnotation"), "TestAnnotation"));
        assertTrue(hasAnnotationWithName(getAnnotations(TestClass.class, "withAnnotation"), "TestAnnotation"));
    }
    
    private Annotation[] getAnnotations(Class<TestClass> theClass, String fieldName) throws NoSuchFieldException {
        return theClass.getField(fieldName).getAnnotations();
    }
    
    @Test
    public void testPublicStaticFinalCompatible() throws NoSuchFieldException, SecurityException {
        Field compatibleField = TestClass.class.getField("compatible");
        Field noFinalField = TestClass.class.getField("noFinal");
        Field noStatic     = TestClass.class.getField("noStatic");
        Field noPublic     = TestClass.class.getDeclaredField("noPublic");
        Field noCompatible = TestClass.class.getField("noCompatible");
        
        assertTrue(isPublicStaticFinalAndCompatible(String.class,  compatibleField.getType(), compatibleField.getModifiers()));
        assertFalse(isPublicStaticFinalAndCompatible(String.class, noFinalField.getType(),    noFinalField.getModifiers()));
        assertFalse(isPublicStaticFinalAndCompatible(String.class, noStatic.getType(),        noStatic.getModifiers()));
        assertFalse(isPublicStaticFinalAndCompatible(String.class, noPublic.getType(),        noPublic.getModifiers()));
        assertFalse(isPublicStaticFinalAndCompatible(String.class, noCompatible.getType(),    noCompatible.getModifiers()));
    }
    
    @Test
    public void testGetValueFromStaticFieldOrNull() throws NoSuchFieldException, SecurityException {
        Field compatibleField = TestClass.class.getField("compatible");
        assertEquals("42", getValueFromStaticFieldOrNull(TestClass.class, compatibleField));
    }
    
    @Test
    public void testInvokeStaticMethodOrNull() throws NoSuchMethodException, SecurityException {
        Method method = TestClass.class.getMethod("fortyTwo", new Class[0]);
        assertEquals("42", invokeStaticMethodOrNull(Test.class, method));
    }
    
}
