//  Copyright (c) 2017-2018 Nawapunth Manusitthipol (NawaMan).
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
package nawaman.utils.reflection;

import static java.util.Arrays.asList;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import lombok.NonNull;
import lombok.val;
import nawaman.utils.reflection.exception.NotDefaultMethodException;

/**
 * Utility class relating to dynamic proxy.
 * 
 * @author NawaMan -- nawa@nawaman.net
 */
public class UProxy {
    
    /**
     * Create a dynamic proxy for the given interface that call all default method.
     * 
     * @param <OBJECT>              the main interface type.
     * @param theGivenInterface     the main interface class.
     * @param additionalInterfaces  additional interfaces.
     * @return  the newly created dyamic proxy for the interface.
     */
    @SuppressWarnings("unchecked")
    public static <OBJECT> OBJECT createDefaultProxy(@NonNull Class<OBJECT> theGivenInterface, Class<?> ... additionalInterfaces) {
        val interfaces  = prepareInterfaces(theGivenInterface, additionalInterfaces);
        val classLoader = theGivenInterface.getClassLoader();
        val theProxy    = (OBJECT)Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args)->{
            return invokeDefaultMethod(proxy, method, args);
        });
        return (OBJECT)theProxy;
    }
    
    private static <OBJECT> java.lang.Class<?>[] prepareInterfaces(Class<OBJECT> theGivenInterface,
            Class<?>... additionalInterfaces) {
        if (!theGivenInterface.isInterface())
            throw new IllegalArgumentException("Interface is required: " + theGivenInterface);
        
        val interfaceList = new ArrayList<Class<?>>();
        interfaceList.add(theGivenInterface);
        if (additionalInterfaces != null) {
            val additionalList = asList(additionalInterfaces);
            additionalList.forEach(each->{
                if (!each.isInterface())
                    throw new IllegalArgumentException("Interface is required: " + each);
            });
            interfaceList.addAll(additionalList);
        }
        
        val interfaces  = interfaceList.toArray(new Class<?>[interfaceList.size()]);
        return interfaces;
    }
    
    /**
     * Invoke the default of an interface method of the proxy object given the methodArgs.
     * 
     * @param proxy       the proxy object.
     * @param method      the method -- this has to be a default object.
     * @param methodArgs  the arguments for the invocation.
     * @return  the invocation result.
     * @throws NotDefaultMethodException  if the method is not a default method - to avoid this use {@code Method.isDefault()}.
     * @throws Throwable                  any exception that might occur.
     */
    public static Object invokeDefaultMethod(@NonNull Object proxy, @NonNull Method method, Object[] methodArgs) 
                    throws NotDefaultMethodException, Throwable {
        // TODO - See if we can avoid this in case it is already done.
        val defaultMethod = getDefaultMethod(method);
        val declaringClass = defaultMethod.getDeclaringClass();
        val constructor    = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);
        
        val result = constructor
                .newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                .unreflectSpecial(defaultMethod, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(methodArgs);
        return result;
    }
    
    private static Method getDefaultMethod(Method method) {
        if (method.isDefault())
            return method;
        
        for (Class<?> superInterface : method.getDeclaringClass().getInterfaces()) {
            Method defaultMethod = getDefaultMethodFrom(method, superInterface);
            if (defaultMethod != null)
                return defaultMethod;
        }
        throw new NotDefaultMethodException(method);
    }
    
    private static Method getDefaultMethodFrom(Method method, Class<?> thisInterface) {
        try {
            Method foundMethod = thisInterface.getDeclaredMethod(method.getName(), method.getParameterTypes());
            if (foundMethod.isDefault())
                return foundMethod;
        } catch (NoSuchMethodException | SecurityException e) {
            
        }
        
        for (Class<?> superInterface : thisInterface.getInterfaces()) {
            Method defaultMethod = getDefaultMethodFrom(method, superInterface);
            if (defaultMethod != null)
                return defaultMethod;
        }
        
        return null;
    }
}