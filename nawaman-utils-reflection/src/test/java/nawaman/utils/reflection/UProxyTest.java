package nawaman.utils.reflection;

import static java.lang.String.format;
import static nawaman.utils.reflection.UProxy.createDefaultProxy;
import static nawaman.utils.reflection.UProxy.invokeDefaultMethod;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
        val obj = (HasDefaultMethod) new HasDefaultMethod() {
        };
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
    public void testInvokeDefaultMethod_noMatterWhatObjectIs_itIsTheMethodThatCount()
            throws NotDefaultMethodException, Throwable {
        // The original interface
        val objFromSuper = (HasOverrideDefaultMethod) new HasOverrideDefaultMethod() {
        };
        val methodFromSuper = HasDefaultMethod.class.getDeclaredMethod("getMessage");
        val resultFromSuper = invokeDefaultMethod(objFromSuper, methodFromSuper, new Object[0]);
        assertEquals("Hello world!", resultFromSuper);
        
        // The child interface
        val objFromChild = (HasOverrideDefaultMethod) new HasOverrideDefaultMethod() {
        };
        val methodFromChild = HasOverrideDefaultMethod.class.getDeclaredMethod("getMessage");
        val resultFromChild = invokeDefaultMethod(objFromChild, methodFromChild, new Object[0]);
        assertEquals("Hi world!", resultFromChild);
        
        // The class
        val objFromClass = new HasDefaultMethodClass();
        val methodFromClass = HasDefaultMethod.class.getDeclaredMethod("getMessage"); // Get from the interface
        val resultFromClass = invokeDefaultMethod(objFromClass, methodFromClass, new Object[0]);
        assertEquals("Hello world!", resultFromClass); // Call the default one not the class one.
    }
    
    @Test
    public void testInvokeDefaultMethod_notDefaultMethod() throws Throwable {
        val obj = UProxy.createDefaultProxy(HasNoDefaultMethod.class);
        val method = HasNoDefaultMethod.class.getDeclaredMethod("getNoDefaultMessage"); // Get from the class
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
        assertTrue(UProxy.isDefaultInterface(IGreet.class));
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
        assertTrue(UProxy.isDefaultInterface(IGreet2Child.class));
        assertFalse(UProxy.isDefaultInterface(IGreet2Super.class));
        assertEquals("{"
                +   "greet([class java.lang.String]): class java.lang.String"
                +       "=nawaman.utils.reflection.UProxyTest.IGreet2Super"
                + "}",
                UProxy.getNonDefaultMethods(IGreet2Super.class).toString());
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
        assertTrue(UProxy.isDefaultInterface(IGreet3Child.class));
        assertTrue(UProxy.isDefaultInterface(IGreet3Super.class));
    }
    
    public static interface IGreetAll extends WithToStringHashCodeEquals {
        public default String greet(String name) {
            return "Hello: " + name;
        }
    }
    
    @Test
    public void testWithToStringHashCodeEquals() {
        val theProxy = createDefaultProxy(IGreetAll.class);
        val theProxy2 = createDefaultProxy(IGreetAll.class);
        assertEquals(((WithToStringHashCodeEquals) theProxy)._toString(), theProxy.toString());
        assertEquals(((WithToStringHashCodeEquals) theProxy)._hashCode(), theProxy.hashCode());
        assertTrue(theProxy._equals(theProxy));
        assertFalse(theProxy._equals(theProxy2));
        assertEquals(((WithToStringHashCodeEquals) theProxy)._equals(theProxy), theProxy._equals(theProxy));
        assertEquals(((WithToStringHashCodeEquals) theProxy)._equals(theProxy2), theProxy._equals(theProxy2));
    }
    
    public static interface IGreet4Super {
        public String greet(String name);
    }
    
    public static interface IGreet4Child extends IGreet4Super {
        
    }
    
    @Test
    public void testFail_hasNonDefaultMethod() {
        assertFalse(UProxy.isDefaultInterface(IGreet4Super.class));
        assertFalse(UProxy.isDefaultInterface(IGreet4Child.class));
        assertEquals("{"
                +   "greet([class java.lang.String]): class java.lang.String"
                +       "=nawaman.utils.reflection.UProxyTest.IGreet4Super"
                + "}",
                UProxy.getNonDefaultMethods(IGreet4Child.class).toString());
    }
}
