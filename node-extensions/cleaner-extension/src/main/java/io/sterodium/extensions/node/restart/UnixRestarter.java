package io.sterodium.extensions.node.restart;

import com.sun.akuma.Daemon;
import com.sun.akuma.JavaVMArguments;
import com.sun.jna.StringArray;

import java.io.File;
import java.io.IOException;

import static io.sterodium.extensions.node.restart.GNUCLibrary.LIBC;

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
 * @see <a href="https://git.io/vMSol">Jenkins Unix slave restarter</a>
 */
public class UnixRestarter implements Restarter {

    private JavaVMArguments args;

    @Override
    public boolean isApplicable() {
        if (File.pathSeparatorChar != ':') {
            return false;
        }

        try {
            args = JavaVMArguments.current();

            // Make sure all the relevant classes are loaded now
            LIBC.getdtablesize();
            int v = LIBC.fcntl(99999, GNUCLibrary.F_GETFD);
            LIBC.fcntl(99999, GNUCLibrary.F_SETFD, v);

            Daemon.getCurrentExecutable();
            LIBC.execv("positively/no/such/executable",
                    new StringArray(new String[]{"a", "b", "c"}));

            return true;
        } catch (UnsupportedOperationException | LinkageError | IOException e) {
            return false;
        }
    }

    @Override
    public void restart() throws IOException {
        int sz = LIBC.getdtablesize();
        for (int i = 3; i < sz; i++) {
            int flags = LIBC.fcntl(i, GNUCLibrary.F_GETFD);
            if (flags < 0) {
                continue;
            }
            LIBC.fcntl(i, GNUCLibrary.F_SETFD, flags | GNUCLibrary.FD_CLOEXEC);
        }

        String exe = Daemon.getCurrentExecutable();
        LIBC.execv(exe, new StringArray(args.toArray(new String[args.size()])));
    }

}
