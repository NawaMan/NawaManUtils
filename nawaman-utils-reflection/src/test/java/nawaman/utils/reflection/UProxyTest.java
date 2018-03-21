package nawaman.utils.reflection;

import static java.lang.String.format;
import static nawaman.utils.reflection.UProxy.createDefaultProxy;
import static nawaman.utils.reflection.UProxy.invokeDefaultMethod;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import lombok.val;
import nawaman.utils.reflection.exception.NotDefaultMethodException;

@SuppressWarnings("javadoc")
public class UProxyTest {
    
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
    
    public static class HasDefaultMethodClass implements HasDefaultMethod {
        
        public String getNoDefaultMessage() {
            return "Hi world!";
        }
        
    }
    
    public static interface HasNoDefaultMethod extends HasDefaultMethod {
        
        public String getNoDefaultMessage();
        
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
        val objFromClass    = new HasDefaultMethodClass();
        val methodFromClass = HasDefaultMethod.class.getDeclaredMethod("getMessage");    // Get from the interface
        val resultFromClass = invokeDefaultMethod(objFromClass, methodFromClass, new Object[0]);
        assertEquals("Hello world!", resultFromClass);                                   // Call the default one not the class one.
    }
    
    @Test
    public void testInvokeDefaultMethod_notDefaultMethod() throws Throwable {
        val obj    = UProxy.createDefaultProxy(HasNoDefaultMethod.class);
        val method = HasNoDefaultMethod.class.getDeclaredMethod("getNoDefaultMessage");  // Get from the class
        try {
            invokeDefaultMethod(obj, method, new Object[0]);
            fail(format("Expect a %s here", NotDefaultMethodException.class.getSimpleName()));
        } catch (NotDefaultMethodException e) {
            assertEquals(method, e.getMethod());
        }
    }
    
    public static interface IGreet {
        public default String greet(String name) {
            return "Hello: " + name;
        }
    }
    
    @Test
    public void testSuccess_simple() {
        val theProxy = createDefaultProxy(IGreet.class);
        assertEquals("Hello: there", theProxy.greet("there"));
    }
    
    public static interface IGreet2Super {
        public String greet(String name);
    }
    
    public static interface IGreet2Child extends IGreet2Super {
        public default String greet(String name) {
            return "Hello: " + name;
        }
    }
    
    @Test
    public void testSuccess_implementInChild() {
        val theProxy = createDefaultProxy(IGreet2Child.class);
        assertEquals("Hello: world", theProxy.greet("world"));
    }
    
    public static interface IGreet3Super {
        public default String greet(String name) {
            return "Hello: " + name;
        }
    }
    
    public static interface IGreet3Child extends IGreet3Super {
        public String greet(String name);
    }
    
    @Test
    public void testSuccess_implementInSuper() {
        val theProxy = createDefaultProxy(IGreet3Child.class);
        assertEquals("Hello: world", theProxy.greet("world"));
    }
}
