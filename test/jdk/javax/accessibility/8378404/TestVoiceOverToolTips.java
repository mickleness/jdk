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

import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * @test
 * @key headful
 * @bug 8378404
 * @summary manual test for VoiceOver reading tooltip windows
 * @requires os.family == "mac"
 * @library /java/awt/regtesthelpers
 * @build PassFailJFrame
 * @run main/manual TestVoiceOverToolTips
 */

public class TestVoiceOverToolTips {
    public static void main(String[] args) throws Exception {
        String INSTRUCTIONS = """
                VoiceOver should NOT announce tooltip windows.

                Follow these steps to test the behaviour:

                1. Start the VoiceOver (Press Command + F5) application
                2. Move the mouse over each button until a tooltip appears.

                This test fails if VoiceOver ever says anything like,
                "in system dialog, content is empty, java has new system
                dialog"

                """;

        PassFailJFrame.builder()
                .title("TestVoiceOverToolTips Instruction")
                .instructions(INSTRUCTIONS)
                .columns(40)
                .testUI(TestVoiceOverToolTips::createUI)
                .build()
                .awaitAndCheck();
    }

    private static JFrame createUI() {
        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));

        JButton b1 = new JButton("But");
        b1.setToolTipText("But it wasn't my fault, I was given those beans");

        JButton b2 = new JButton("You");
        b2.setToolTipText("You persuaded me to trade away my cow for beans.");

        rows.add(b1);
        rows.add(b2);

        JFrame frame = new JFrame("JButtons With Tooltips");
        frame.getContentPane().add(rows);
        frame.pack();
        return frame;
    }
}