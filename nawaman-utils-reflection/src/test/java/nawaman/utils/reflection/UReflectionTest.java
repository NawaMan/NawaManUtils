package nawaman.utils.reflection;

import static java.lang.String.format;
import static nawaman.utils.reflection.UReflection.invokeDefaultMethod;
import static org.junit.Assert.*;

import org.junit.Test;

import lombok.val;
import nawaman.utils.reflection.exception.NotDefaultMethodException;

@SuppressWarnings("javadoc")
public class UReflectionTest {
    
    public static interface HasDefaultMethod {
        
        public default String getMessage() {
            return "Hello world!";
        }
        
    }
    
    @Test
    public void testInvokeDefaultMethod() throws NotDefaultMethodException, Throwable {
        val obj    = (HasDefaultMethod)new HasDefaultMethod() {};
        val method = HasDefaultMethod.class.getDeclaredMethod("getMessage");
        val result = invokeDefaultMethod(obj, method, new Object[0]);
        assertEquals("Hello world!", result);
    }
    
    public static interface HasOverrideDefaultMethod extends HasDefaultMethod {
        
        public default String getMessage() {
            return "Hi world!";
        }
        
    }
    
    public static class HasNoDefaultMethod implements HasDefaultMethod {
        
        public String getMessage() {
            return "Hi world!";
        }
        
    }
    
    @Test
    public void testInvokeDefaultMethod_noMatterWhatObjectIs_itIsTheMethodThatCount() throws NotDefaultMethodException, Throwable {
        // The original interface
        val objFromSuper    = (HasOverrideDefaultMethod)new HasOverrideDefaultMethod() {};
        val methodFromSuper = HasDefaultMethod.class.getDeclaredMethod("getMessage");
        val resultFromSuper = invokeDefaultMethod(objFromSuper, methodFromSuper, new Object[0]);
        assertEquals("Hello world!", resultFromSuper);
        
        // The child interface
        val objFromChild    = (HasOverrideDefaultMethod)new HasOverrideDefaultMethod() {};
        val methodFromChild = HasOverrideDefaultMethod.class.getDeclaredMethod("getMessage");
        val resultFromChild = invokeDefaultMethod(objFromChild, methodFromChild, new Object[0]);
        assertEquals("Hi world!", resultFromChild);
        
        // The class
        val objFromClass    = new HasNoDefaultMethod() {};
        val methodFromClass = HasDefaultMethod.class.getDeclaredMethod("getMessage");    // Get from the interface
        val resultFromClass = invokeDefaultMethod(objFromClass, methodFromClass, new Object[0]);
        assertEquals("Hello world!", resultFromClass);                                   // Call the default one not the class one.
    }
    
    @Test
    public void testInvokeDefaultMethod_notDefaultMethod() throws Throwable {
        val obj    = new HasNoDefaultMethod() {};
        val method = HasNoDefaultMethod.class.getDeclaredMethod("getMessage");  // Get from the class
        try {
            invokeDefaultMethod(obj, method, new Object[0]);
            fail(format("Expect a %s here", NotDefaultMethodException.class.getSimpleName()));
        } catch (NotDefaultMethodException e) {
            assertEquals(method, e.getMethod());
        }
    }
    
}
