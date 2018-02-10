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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import lombok.experimental.Accessors;
import nawaman.failable.FailableException;
import nawaman.utils.pipeable.IPipe;
import nawaman.utils.pipeable.Pipeable;

@SuppressWarnings("javadoc")
public class PipeableTest {
    
    @Test
    public void testBasicPiping() {
        val str = (Pipeable<String>)()->"Str";
        assertEquals(3, str.pipe(String::length).intValue());
    }
    
    @Data
    @Accessors(fluent=true, chain=true)
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
    public void testChainPiping() {
        val person = new Person("Alice");
        assertEquals(5,
                person.pipe(
                    Person::name,
                    String::length)
                .intValue());
        
        assertEquals("false",
                person.pipe(
                    Person::name,
                    String::isEmpty,
                    Object::toString));
    }
    
    @Test
    public void testNull() {
        val person = new Person(null);
        assertNull(person.pipe(Person::name, String::length));
    }
    
    @Test
    public void testOr() {
        val person = new Person(null);
        assertEquals(-1, person.pipe(Person::name, String::length, or(-1)).intValue());
    }
    
    @Test
    public void testException() {
        val person = new Person(null);
        try {
            person.pipe(Person::burn, String::length);
        } catch (FailableException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }
    
    @Test
    public void testException_runtime() {
        val person = new Person(null);
        try {
            person.pipe(Person::runtimeBurn, String::length);
        } catch (FailableException e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }
    
}
