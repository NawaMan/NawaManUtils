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
package nawaman.utils.pipeable.operators;


import java.util.function.Supplier;

import nawaman.failable.Failable.Function;
import nawaman.nullable.NullableJ;
import nawaman.utils.pipeable.NullSafeOperator;
import nawaman.utils.pipeable.Operator;

/**
 * This class contains example operators.
 * 
 * Do not reply on these operators, create yourown as you need.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public class Operators {
    
    /**
     * This operator simply call the given function.
     * 
     * This is not a very useful operator as the mapping function itself already done this.
     * Use this operator only to improve readability to your code -- when it actual does, of course.
     * 
     * @param function  the function.
     * @return  the operator for the given function.
     * 
     * @param <T>          the data type of the object in the pipe.
     * @param <R>          the data type of the return value.
     * @param <THROWABLE>  the exception type the function might throw.
     */
    public static <T, R, THROWABLE extends Throwable>
                Operator<T, R, THROWABLE> to(Function<T, R, THROWABLE> function) {
        return Operator.of(function);
    }
    
    /**
     * This operator simply convert the value to string.
     * 
     * @return  the operator to convert oject to string.
     * 
     * @param <T>  the data type of the object in the pipe.
     */
    public static <T> Operator<T, String, RuntimeException> toStr() {
        return Operator.of(Object::toString);
    }
    
    /**
     * This null-safe operator return the default value if the pipe value is null.
     * 
     * @param defaultValue  the default value to be returned.
     * @return  the OR operator.
     * 
     * @param <T>  the data type of the object in the pipe.
     */
    public static <T> NullSafeOperator<T, T, RuntimeException> or(T defaultValue) {
        return NullSafeOperator.of(t->{ 
            return NullableJ._or(t, defaultValue);
        });
    }
    
    /**
     * This null-safe operator return the default value if the pipe value is null.
     * 
     * @param defaultSupplier  the default value to be returned.
     * @return  the OR operator.
     * 
     * @param <T>  the data type of the object in the pipe.
     */
    public static <T> NullSafeOperator<T, T, RuntimeException> orGet(Supplier<T> defaultSupplier) {
        return NullSafeOperator.of(t->{
            return NullableJ._orGet(t, defaultSupplier);
        });
    }
    
    /**
     * This null-safe operator return the default value if the pipe value is null.
     * 
     * @param defaultValue  the default value to be returned.
     * @return  the OR operator.
     * 
     * @param <T>  the data type of the object in the pipe.
     */
    public static <T> NullSafeOperator<T, T, RuntimeException> otherwise(T defaultValue) {
        return NullSafeOperator.of(t->{
            return NullableJ._or(t, defaultValue);
        });
    }
    
    /**
     * This null-safe operator return the default value if the pipe value is null.
     * 
     * @param defaultSupplier  the default value to be returned.
     * @return  the OR operator.
     * 
     * @param <T>  the data type of the object in the pipe.
     */
    public static <T> NullSafeOperator<T, T, RuntimeException> otherwiseGet(Supplier<T> defaultSupplier) {
        return NullSafeOperator.of(t->{
            return NullableJ._orGet(t, defaultSupplier);
        });
    }
    
}
