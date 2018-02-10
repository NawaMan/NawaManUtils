//  ========================================================================
//  Copyright (c) 2017 Direct Solution Software Builders (DSSB).
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
package nawaman.utils.pipeable;

import static java.util.Arrays.stream;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This utility class contains convenient functions for Pipeable.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public interface Pipe {
    
    /**
     * This method create a pipeable object for the given data.
     * 
     * This method is designed to read with use without static import.
     * Like this: Pipe.of(data).
     * 
     * @param data  the data to be pipe.
     * @return  the pipeable of the data.
     * 
     * @param <TYPE>  the type of the data.
     */
    @SuppressWarnings("unchecked")
    public static <TYPE> Pipeable<TYPE> of(TYPE data) {
        if (data instanceof Pipeable)
            return (Pipeable<TYPE>)data;
        
        return (Pipeable<TYPE>)()->data;
    }
    
    /**
     * Create a pipeable stream of the given iterable.
     * 
     * @param iterable  the iterable.
     * @return  the map operator.
     * 
     * @param <TYPE>  the type of the data.
     */
    public static <TYPE> Pipeable<Stream<TYPE>> streamOf(Iterable<TYPE> iterable) {
        return Pipe.of((iterable == null) 
                        ? Stream.empty()
                        : StreamSupport.stream(iterable.spliterator(), false));
    }
    
    /**
     * Create a pipeable stream of the given array.
     * 
     * @param array  the array.
     * @return  the map operator.
     * 
     * @param <TYPE>  the type of the data.
     */
    @SafeVarargs
    public static <TYPE> Pipeable<Stream<TYPE>> streamOf(TYPE ...  array) {
        return Pipe.of(stream(array));
    }
    
}
