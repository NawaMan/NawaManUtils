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

import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nawaman.utils.pipeable.Operator;

/**
 * This class contains example operators.
 * 
 * Do not reply on these operators, create yourown as you need.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public class StreamOperations {
    
    /**
     * Map element in the stream to another value.
     * 
     * @return  the map operator.
     * 
     * @param <T> the data type of the input.
     */
    public static <T> Operator<T, Stream<T>, RuntimeException> stream() {
        return Operator.of(Stream::of);
    }
    
    /**
     * Map element in the stream to another value.
     * 
     * @param mapper  the mapper.
     * @return  the map operator.
     * 
     * @param <T> the data type of the data in the input stream.
     * @param <R> the data type of the data in the output stream.
     */
    public static <T, R> Operator<Stream<T>, Stream<R>, RuntimeException> map(Function<T, R> mapper) {
        return Operator.of(stream -> {
            return stream.map(mapper);
        });
    }
    
    /**
     * FlatMap element in the stream to another value.
     * 
     * @param mapper  the mapper.
     * @return  the faltMap operator.
     * 
     * @param <T> the data type of the data in the input stream.
     * @param <R> the data type of the data in the output stream.
     * @param <C> the type of the intermediate collection.
     */
    public static <T, R, C extends Collection<R>> Operator<Stream<T>, Stream<R>, RuntimeException> faltMap(Function<T, C> mapper) {
        return Operator.of(stream -> { 
            return stream.map(mapper).flatMap(Collection::stream);
        });
    }
    
    /**
     * Spread element in the stream to another value. (flatMap from List)
     * 
     * @param mapper  the mapper.
     * @return the spread operator.
     * 
     * @param <T> the data type of the data in the input stream.
     * @param <R> the data type of the data in the output stream.
     * @param <C> the type of the intermediate collection.
     */
    public static <T, R, C extends Collection<R>> Operator<List<T>, Stream<R>, RuntimeException> spread(Function<T, C> mapper) {
        return Operator.of(stream -> {
            return stream.stream().map(mapper).flatMap(Collection::stream);
        });
    }
    
    /**
     * Filter the stream.
     * 
     * @param predicate  the check if an element should be included.
     * @return  the collect operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, Stream<T>, RuntimeException> filter(Predicate<T> predicate) {
        return Operator.of(stream -> {
            return stream.filter(predicate);
        });
    }
    
    /**
     * Peek into the stream.
     * 
     * @param consumer  the consumer of the element.
     * @return  the peek operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, Stream<T>, RuntimeException> peek(Consumer<T> consumer) {
        return Operator.of(stream -> {
            return stream.peek(consumer);
        });
    }
    
    /**
     * anyMatch into the stream.
     * 
     * @param predicate  the check if an element should be included.
     * @return  the anyMatch operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, Boolean, RuntimeException> anyMatch(Predicate<T> predicate) {
        return Operator.of(stream -> {
            return stream.anyMatch(predicate);
        });
    }
    
    /**
     * allMatch into the stream.
     * 
     * @param predicate  the check if an element should be included.
     * @return  the allMatch operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, Boolean, RuntimeException> allMatch(Predicate<T> predicate) {
        return Operator.of(stream -> {
            return stream.allMatch(predicate);
        });
    }
    
    /**
     * Collect the stream.
     * 
     * @param collector  the collectors.
     * @return  the collect operator.
     * 
     * @param <T> the data type of the data in the input stream.
     * @param <A> the type of the accumulator.
     * @param <R> the data type of the data in the output stream.
     */
    public static<T, A, R> Operator<Stream<T>, R, RuntimeException> collect(Collector<? super T, A, R> collector) {
        return Operator.of(stream-> {
            return stream.collect(collector);
        });
    }
    
    /**
     * Collect the stream.
     * 
     * @param accumulator  function for combining two values.
     * @return  the reduce operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, T, RuntimeException> reduce(BinaryOperator<T> accumulator) {
        return Operator.of(stream-> {
            return stream.reduce(accumulator).orElse(null);
        });
    }
    
    /**
     * Collect the stream to list.
     * 
     * @return  the reduce operator.
     * 
     * @param <T> the data type of the data in the stream.
     */
    public static <T> Operator<Stream<T>, List<T>, RuntimeException> collectToList() {
        return Operator.of(stream-> {
            return stream.collect(Collectors.toList());
        });
    }
    
}
