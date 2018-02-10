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
package nawaman.utils.pipeable.supportive;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.val;
import nawaman.failable.FailableException;
import nawaman.utils.pipeable.Catch;

/**
 * Additional handling of an exception from the execution of the pipe.
 * 
 * @param <RESULT>    the type of the operation result.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public class CatchAndThen<RESULT> extends CatchNoCheckException<RESULT> {
    
    /**
     * Construct CatchAndThen with the given function.
     * 
     * @param orFunction  the function.
     */
    public <R extends RESULT> CatchAndThen(Function<FailableException, R> orFunction) {
        super(orFunction);
    }
    
    /**
     * Returns the given value after handling the exception.
     * 
     * @param orValue  the value to be returned.
     * @return  the Catch that does not throw a checked exception.
     */
    public CatchNoCheckException<RESULT> andReturn(RESULT orValue) {
        return new CatchNoCheckException<>(e->{
            CatchAndThen.this.handle(e);
            return orValue;
        });
    }
    
    /**
     * Returns the value from the supplier after handling the exception.
     * 
     * @param orSupplier  the supplier for the value to be returned.
     * @return  the Catch that does not throw a checked exception.
     */
    public CatchNoCheckException<RESULT> andGet(Supplier<RESULT> orSupplier) {
        return new CatchNoCheckException<>(e->{
            CatchAndThen.this.handle(e);
            val result = (orSupplier != null) ? orSupplier.get() : null;
            return result;
        });
    }
    
    /**
     * Returns the value from the function after handling the exception.
     * 
     * @param orFunction  the function for the value to be returned given the failable exception.
     * @return  the Catch that does not throw a checked exception.
     */
    public CatchNoCheckException<RESULT> andApply(Function<FailableException, RESULT> orFunction) {
        return new CatchNoCheckException<>(e->{
            CatchAndThen.this.handle(e);
            val result = (orFunction != null) ? orFunction.apply(e) : null;
            return result;
        });
    }
    
    /**
     * Handle the exception by throwing the cause of the exception.
     * 
     * @return  the catch.
     * 
     * @param <THROWABLE>  the exception thrown by operators in the pipe..
     */
    public <THROWABLE extends Throwable> Catch<RESULT, THROWABLE> andThrow() {
        return new Catch<RESULT, THROWABLE>(exception->{
            CatchAndThen.this.handle(exception);
            @SuppressWarnings("unchecked")
            val cause = (THROWABLE)exception.getCause();
            throw cause;
        });
    }
    
    /**
     * Handle the exception by throwing the cause of the exception.
     * 
     * @return  the catch.
     */
    public Catch<RESULT, FailableException> andThrowFailableException() {
        return new Catch<RESULT, FailableException>(exception->{
            CatchAndThen.this.handle(exception);
            throw exception;
        });
    }
    
}
