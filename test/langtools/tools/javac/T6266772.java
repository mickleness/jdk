/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug     6266772
 * @summary javac crashes, assertion failure in Lower.java
 * @author  Peter von der Ahé
 */

public class T6266772 {
    static class Nested {
        Nested (Object o) {}
    }

    class Inner extends Nested {
        Inner() {
            super(new Object() { { s(); } });
        }
    }

    void s() {
        calledS = true;
        if (getClass() != T6266772.class)
            throw new AssertionError(getClass());
    }

    static boolean calledS = false;

    public static void main(String[] args) {
        new T6266772().new Inner();
        if (!calledS)
            throw new AssertionError("Didn't call s");
    }
}
