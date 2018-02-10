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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.val;
import nawaman.failable.Failable;
import nawaman.failable.FailableException;
import nawaman.utils.pipeable.supportive.CatchAndThen;
import nawaman.utils.pipeable.supportive.CatchNoCheckException;

/**
 * Catch handles exception thrown as a pipe is processing.
 * 
 * @param <RESULT>    the type of the operation result.
 * @param <THROWABLE> an exception thrown if there is a problem.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public class Catch<RESULT, THROWABLE extends Throwable> {
    
    private Failable.Function<FailableException, RESULT, THROWABLE> handler;
    
    /**
     * Constructor a Catch with the handler.
     * 
     * @param handler  the handler.
     */
    public Catch(Failable.Function<FailableException, RESULT, THROWABLE> handler) {
        this.handler = handler;
    }
    
    /**
     * Handle the exception (a FailableException).
     * 
     * @param exception  the failable exception to be handled.
     * @return  the result to be retured.
     * @throws  THROWABLE  the exception that might be thrown.
     */
    public RESULT handle(FailableException exception) throws THROWABLE {
        if (this.handler == null)
            return null;
        
        return this.handler.apply(exception);
    }
    
    //== Factory methods ==
    
    /**
     * Create the catch for the given handled. This is the same thing as the constructor.
     * 
     * @param handler  the given handler.
     * @return  the newly create Catch.
     * 
     * @param <RESULT>     the return type of the pipe.
     * @param <THROWABLE>  the exception type of the pipe.
     */
    public static <RESULT, THROWABLE extends Throwable> Catch<RESULT, THROWABLE> then(Failable.Function<FailableException, RESULT, THROWABLE> handler) {
        return new Catch<RESULT, THROWABLE>(handler);
    }
    
    /**
     * Handle the problem by returning the given value.
     * 
     * @param orValue  the value to be returned.
     * @return  the Catch that does not throw a checked exception.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchNoCheckException<RESULT> thenReturn(RESULT orValue) {
        return new CatchNoCheckException<>(e->orValue);
    }
    
    /**
     * Handle the problem by returning the value from the supplier.
     * 
     * @param orSupplier  the supplier for the value to be returned.
     * @return  the Catch that does not throw a checked exception.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchNoCheckException<RESULT> thenGet(Supplier<RESULT> orSupplier) {
        return new CatchNoCheckException<>(e->((orSupplier != null) ? orSupplier.get() : null));
    }
    
    /**
     * Handle the problem by returning the value from the function.
     * 
     * @param orFunction  the function for the value to be returned given the failable exception.
     * @return  the Catch that does not throw a checked exception.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchNoCheckException<RESULT> thenApply(Function<FailableException, RESULT> orFunction) {
        return new CatchNoCheckException<>(orFunction);
    }
    
    /**
     * Handle the exception by throwing the cause of the exception.
     * 
     * @return  the catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> Catch<RESULT, FailableException> thenThrowFailableException() {
        return new Catch<RESULT, FailableException>(exception -> {
            throw exception;
        });
    }
    
    /**
     * Handle the exception by ignoring it.
     * 
     * @return  the catch that ignore the exception.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchNoCheckException<RESULT> thenIgnore() {
        return new CatchNoCheckException<RESULT>(null);
    }
    
    /**
     * Handle the exception by printing the stacktrace. This method use Exception.printStackTrace().
     * 
     * @return the Catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchAndThen<RESULT> thenPrint() {
        return new CatchAndThen<RESULT>(e->{
            e.printStackTrace();
            return null;
        });
    }
    
    /**
     * Handle the exception by printing the stacktrace to the given print stream. This method use Exception.printStackTrace(PrintStream).
     * 
     * @param ps the print stream to be printed to.
     * 
     * @return the Catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchAndThen<RESULT> thenPrintTo(PrintStream ps) {
        return new CatchAndThen<RESULT>(e->{
            e.printStackTrace(ps);
            return null;
        });
    }
    
    /**
     * Handle the exception by printing the stacktrace to the given print stream. This method use Exception.printStackTrace(PrintWriter).
     * 
     * @param pw the print writer to be printed to.
     * 
     * @return the Catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchAndThen<RESULT> thenPrintTo(PrintWriter pw) {
        return new CatchAndThen<RESULT>(e->{
            e.printStackTrace(pw);
            return null;
        });
    }
    
    /**
     * Handle the exception by printing the stacktrace to the given print stream. This method use Exception.printStackTrace(PrintWriter).
     * 
     * @param stackTraceElementsHolder  the holder of the stacktrack.
     * @return the Catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchAndThen<RESULT> thenSet(AtomicReference<StackTraceElement[]> stackTraceElementsHolder) {
        return new CatchAndThen<RESULT>(e->{
            if (stackTraceElementsHolder != null)
                stackTraceElementsHolder.set(e.getStackTrace());
            
            return null;
        });
    }
    
    /**
     * Handle the exception by printing the stacktrace to the given print stream. This method use Exception.printStackTrace(PrintWriter).
     * 
     * @param stackTraceHolder  the holder of the stacktrack as a printout by printStackTrace().
     * @return the Catch.
     * 
     * @param <RESULT>  the return type of the pipe.
     */
    public static <RESULT> CatchAndThen<RESULT> thenPrintTo(AtomicReference<String> stackTraceHolder) {
        return new CatchAndThen<RESULT>(e->{
            if (stackTraceHolder != null) {
                try (val baos = new ByteArrayOutputStream();
                     val ps   = new PrintStream(baos)) {
                    e.printStackTrace(ps);
                    stackTraceHolder.set(baos.toString());
                } catch (IOException e1) {
                    // BAOS will not throw an exception.
                }
            }
            
            return null;
        });
    }
    
}
