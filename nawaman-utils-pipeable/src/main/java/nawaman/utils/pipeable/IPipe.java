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

/**
 * Classes implementing this interface can pipe.
 * 
 * This interface extends Pipeable and return itself as the wrapped data.
 * This will allow any class to easily implements Pipeable by simply implement this interface.
 * Make sure that the TYPE is the class that implement this.
 * 
 * @param <TYPE> the type of data that this pipeable is holding.
 * 
 * @author NawaMan -- nawaman@dssb.io
 */
public interface IPipe<TYPE extends Pipeable<TYPE>> extends Pipeable<TYPE> {
    
    @SuppressWarnings("unchecked")
    public default TYPE _data() {
        return (TYPE)this;
    }
    
}
