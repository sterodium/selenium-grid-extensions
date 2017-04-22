package io.sterodium.extensions.node.restart;

import java.io.IOException;

/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2004 Kohsuke Kawaguchi, Sun Microsystems Inc., and a number of other contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @see <a href="https://git.io/vMSoZ">Jenkins Windows slave restarter</a>
 */
public class WindowsRestarter implements Restarter {

    private String exe;

    @Override
    public boolean isApplicable() {
        exe = System.getenv("WINSW_EXECUTABLE");
        if (exe == null) {
            return false;
        }
        try {
            return exec("status") == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Override
    public void restart() throws IOException, InterruptedException {
        exec("restart!");
    }


    private int exec(String cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(exe, cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.getOutputStream().close();

        return p.waitFor();
    }

}
