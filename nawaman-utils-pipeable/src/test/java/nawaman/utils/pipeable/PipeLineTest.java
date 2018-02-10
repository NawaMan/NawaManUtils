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

import static nawaman.utils.pipeable.operators.Operators.or;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import lombok.experimental.Accessors;
import nawaman.utils.pipeable.Catch;
import nawaman.utils.pipeable.IPipe;
import nawaman.utils.pipeable.PipeLine;

@SuppressWarnings("javadoc")
public class PipeLineTest {

    
    @Data
    @Accessors(chain=true)
    @AllArgsConstructor
    static class Person implements IPipe<Person> {
        private String name;
        
        public String burn() throws IOException {
            throw new IOException();
        }
        public String runtimeBurn() throws IOException {
            throw new NullPointerException();
        }
    }
    
    @Test
    public void testSingle() {
        val pipeline = PipeLine.startingWith(Person::getName).build();
        assertEquals("Jack", new Person("Jack").pipe(pipeline));
        assertEquals("Jack", pipeline.apply(new Person("Jack")));
    }
    
    @Test
    public void testMultiple() {
        val pipeline = PipeLine.startingWith(Person::getName).next(String::length).build();
        assertEquals(4, new Person("Jack").pipe(pipeline).intValue());
        assertEquals(4, pipeline.apply(new Person("Jack")).intValue());
    }
    
    @Test
    public void testOr() {
        val pipeline = PipeLine.startingWith(Person::getName).next(String::length).next(or(-1)).build();
        assertEquals(-1, new Person(null).pipe(pipeline).intValue());
        assertEquals(-1, pipeline.apply(new Person(null)).intValue());
    }
    
    @Test
    public void testCatchReturn() {
        val pipeline = PipeLine.startingWith(Person::burn).buildWith(Catch.thenReturn("<none>"));
        assertEquals("<none>", new Person(null).pipe(pipeline));
        assertEquals("<none>", pipeline.apply(new Person(null)));
    }
    
    @Test
    public void testCatchIgnore() {
        val pipeline = PipeLine.startingWith(Person::burn).buildWith(Catch.thenIgnore());
        assertNull(new Person(null).pipe(pipeline));
        assertNull(pipeline.apply(new Person(null)));
    }
    
}
