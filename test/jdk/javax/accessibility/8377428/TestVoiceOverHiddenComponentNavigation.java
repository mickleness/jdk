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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * @test
 * @key headful
 * @bug 8377428
 * @summary manual test for VoiceOver reading hidden components
 * @requires os.family == "mac"
 * @library /java/awt/regtesthelpers
 * @build PassFailJFrame
 * @run main/manual TestVoiceOverHiddenComponentNavigation
 */

public class TestVoiceOverHiddenComponentNavigation {
    public static void main(String[] args) throws Exception {
        String INSTRUCTIONS = """
                Test UI contains a JButton inside an invisible panel.

                Follow these steps to test the behaviour:

                1. Start the VoiceOver (Press Command + F5) application
                2. Move VoiceOver cursor to test window if necessary
                3. Press CTRL + ALT + SHIFT + UP once
                4. Press CTRL + ALT + LEFT to move the VoiceOver cursor back
                5. Repeat step 3 until you reach the "Close" button.

                If VoiceOver ever references a "Hidden Button": then this test
                fails.
                """;

        PassFailJFrame.builder()
                .title("TestVoiceOverHiddenComponentNavigation Instruction")
                .instructions(INSTRUCTIONS)
                .columns(40)
                .testUI(TestVoiceOverHiddenComponentNavigation::createUI)
                .build()
                .awaitAndCheck();
    }

    private static JFrame createUI() {
        JFrame frame = new JFrame("A Frame with hidden JButton");
        JButton button = new JButton("Hidden Button");
        JPanel panel = new JPanel();
        panel.add(button);
        panel.setVisible(false);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(new JLabel("Label"), BorderLayout.SOUTH);
        frame.setSize(200, 100);
        return frame;
    }
}