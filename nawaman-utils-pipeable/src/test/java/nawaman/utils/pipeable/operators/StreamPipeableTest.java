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

import static java.util.Arrays.asList;
import static nawaman.utils.pipeable.operators.Operators.to;
import static nawaman.utils.pipeable.operators.Operators.toStr;
import static nawaman.utils.pipeable.operators.StreamOperations.collect;
import static nawaman.utils.pipeable.operators.StreamOperations.collectToList;
import static nawaman.utils.pipeable.operators.StreamOperations.map;
import static nawaman.utils.pipeable.operators.StreamOperations.reduce;
import static nawaman.utils.pipeable.operators.StreamOperations.spread;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import lombok.experimental.Accessors;
import nawaman.utils.pipeable.IPipe;
import nawaman.utils.pipeable.Pipe;
import nawaman.utils.pipeable.PipeLine;

@SuppressWarnings("javadoc")
public class StreamPipeableTest {
    
    @Data
    @Accessors(chain=true)
    static class Company implements IPipe<Company> {
        List<Department> departments = new ArrayList<>();
    }
    
    @Data
    @Accessors(chain=true)
    static class Department implements IPipe<Department> {
        List<Person> members = new ArrayList<>();
    }
    
    @Data
    @Accessors(chain=true)
    @AllArgsConstructor
    static class Person implements IPipe<Person> {
        private String name;
    }
    
    @Test
    public void testStreamPipe() {
        val company = new Company();
        val dep1     = new Department();
        val dep2     = new Department();
        company.getDepartments().addAll(asList(dep1, dep2));
        
        dep1.getMembers().addAll(asList(
                new Person("Alice"),
                new Person("Bob"),
                new Person("Chalie")));
        
        dep2.getMembers().addAll(asList(
                new Person("Donald"),
                new Person("Edward"),
                new Person("Frank")));
        
        val r = company.pipe(
                to     (Company   ::getDepartments),
                spread(Department::getMembers),
                map    (Person    ::getName),
                collect(Collectors.toList()),
                to     (Object    ::toString)
        );
        assertEquals("[Alice, Bob, Chalie, Donald, Edward, Frank]", r);
        
        val pipeline = PipeLine
                .startingWith(to     (Company   ::getDepartments))
                        .next(spread(Department::getMembers))
                        .next(map    (Person    ::getName))
                        .next(collect(Collectors.toList()))
                        .next(to     (Object    ::toString))
                .build();
        val r2 = pipeline.apply(company);
        assertEquals("[Alice, Bob, Chalie, Donald, Edward, Frank]", r2);
    }
    
    @Test
    public void testStream() {
        val lengths = Pipe.streamOf("Hello").pipe(
                map(String::length),
                collectToList(), 
                toStr());
        assertEquals("[5]", lengths);
    }
    
    @Test
    public void testStream_reduce() {
        val lengths = Pipe.streamOf("Hello", "world").pipe(
                reduce((a, b)-> a + ", " + b), 
                Object::toString);
        assertEquals("Hello, world", lengths);
    }
    
}
