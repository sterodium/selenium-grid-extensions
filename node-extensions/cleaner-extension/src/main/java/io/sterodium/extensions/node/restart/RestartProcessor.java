package io.sterodium.extensions.node.restart;

public class RestartProcessor {

    private Restarter[] restarters = new Restarter[]{
        new UnixRestarter(),
        new WindowsRestarter(),
    };

    public void restart() throws Exception {
        for (Restarter restarter : restarters) {
            if (restarter.isApplicable()) {
                restarter.restart();
                return;
            }
        }

        throw new UnsupportedOperationException("restart is not supported");
    }

}
