package io.sterodium.extensions.node.restart;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.StringArray;

public interface GNUCLibrary extends Library {

    int getdtablesize();
    int fcntl(int fd, int command);
    int fcntl(int fd, int command, int flags);
    int execv(String path, StringArray args);

    int F_GETFD = 1;
    int F_SETFD = 2;
    int FD_CLOEXEC = 1;

    GNUCLibrary LIBC = (GNUCLibrary) Native.loadLibrary("c", GNUCLibrary.class);

}
