/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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


import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;

/*
 * @test
 * @bug 8380790
 * @summary make sure getAccessibleText() doesn't add DocumentListeners
 * @library /java/awt/regtesthelpers
 * @build PassFailJFrame
 * @run main GetAccessibleTextAddsDocumentListeners
 */

public class GetAccessibleTextAddsDocumentListeners {
    public static void main(String[] args) throws Exception{
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        for (int a = 0; a < 10_000; a++) {
            textPane.getAccessibleContext().getAccessibleText();
        }
        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        int listenerCount = doc.getDocumentListeners().length;
        if (listenerCount > 1000)
            throw new Exception("number of listeners = " + listenerCount);
    }
}
