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


import lombok.val;
import nawaman.failable.Failable;
import nawaman.failable.Failable.Function;

/**
 * Classes implementing this interface can operate on the wrapped data of a compatible pipeable.
 * 
 * If the implemented classes also implements {@code NullSafeOperator},
 *   it will be consider null-safe operator.
 * When the input data is null,
 *   an operator that is not null-safe will be skipped -- null is returned.
 * 
 * @param <TYPE>      the type of data that this pipeable is holding.
 * @param <RESULT>    the type of the operation result.
 * @param <THROWABLE> an exception thrown if there is a problem.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@FunctionalInterface
public interface Operator<TYPE, RESULT, THROWABLE extends Throwable> extends Failable.Function<TYPE, RESULT, THROWABLE> {
    
    /**
     * Perform the operation on the given data.
     * 
     * @param data  the given data to be operated on.
     * @return      the result of the operation.
     * @throws THROWABLE  the problem that might happen.
     **/
    public RESULT apply(TYPE data) throws THROWABLE;
    
    /**
     * This method will be executed if this operation is the last in the pipe;
     *   thus, creating the final result.
     * 
     * This method has a default implementation that correctly handle null value.
     * So if this method is to be overwritten, the new implementation should handle that too.
     * 
     * @param pipe  the pipe to operate on.
     * @return  the result of the operation.
     */
    public default RESULT operateToResult(Pipeable<TYPE> pipe) {
        val rawData = (pipe != null) ? pipe._data() : null;
        if (!(this instanceof NullSafeOperator)
          && (rawData == null))
            return null;
        
        val rawResult = this.gracefully().apply(rawData);
        return rawResult;
    }
    
    /**
     * This method will be executed if this operation is not the last in the pipe;
     *   thus, creating another pipe value to be passed on to the next operator.
     * 
     * This method has a default implementation that correctly handle null value.
     * So if this method is to be overwritten, the new implementation should handle that too.
     * 
     * @param pipe  the pipe to operate on.
     * @return  the result of the operation.
     */
    @SuppressWarnings("unchecked")
    public default Pipeable<RESULT> operateToPipe(Pipeable<TYPE> pipe) {
        val rawData = pipe._data();
        if (!(this instanceof NullSafeOperator)
          && (rawData == null))
            return null;
        
        val rawResult = this.gracefully().apply(rawData);
        return (rawResult instanceof Pipeable)
                ? (Pipeable<RESULT>)rawResult
                : ()->rawResult;
    }
    
    /**
     * Convert a failable function to an operator.
     * 
     * NOTE: javac is a jerk. :'-(
     *   This function was not need when done in Eclipse but javac just cry baby.
     * 
     * @param function  the input function.
     * @return  the operator.
     * 
     * @param <TYPE>      the data type of the function.
     * @param <RESULT>    the return type of the function.
     * @param <THOWABLE>  the exception type of the function.
     **/
    @SuppressWarnings("unchecked")
    public static <TYPE, RESULT, THOWABLE extends Throwable> Operator<TYPE, RESULT, THOWABLE> of(Function<TYPE, RESULT, THOWABLE> function) {
        return (Operator<TYPE, RESULT, THOWABLE>)(t-> { 
            return function.apply(t);
        });
    }
    
}