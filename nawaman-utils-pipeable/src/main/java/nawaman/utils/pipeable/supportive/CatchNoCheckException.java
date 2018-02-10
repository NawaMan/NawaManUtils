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

import nawaman.failable.FailableException;
import nawaman.utils.pipeable.Catch;

/**
 * Catch handles exception thrown as a pipe is processing but will never throw a checked exception..
 * 
 * @param <RESULT>    the type of the operation result.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
@SuppressWarnings("rawtypes")
public class CatchNoCheckException<RESULT> extends Catch<RESULT, RuntimeException> {
    
    private java.util.function.Function orFunction;
    
    /**
     * Constructs a CatchNoCheckException.
     * 
     * @param orFunction  the handler function.
     * 
     * @param <R>          the return type of the given function.
     * @param <THROWABLE>  the exception type of the given function.
     */
    public <R extends RESULT, THROWABLE extends Throwable> CatchNoCheckException(java.util.function.Function<FailableException, R> orFunction) {
        super(null);
        this.orFunction = orFunction;
    }

    /**
     * Handle the exception.
     * 
     * @param exception  the exception.
     */
    @SuppressWarnings("unchecked")
    public RESULT handle(FailableException exception) {
        if (this.orFunction == null)
            return null;
        
        return (RESULT)this.orFunction.apply(exception);
    }
    
}
