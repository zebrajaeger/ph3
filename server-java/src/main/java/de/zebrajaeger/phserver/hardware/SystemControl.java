package de.zebrajaeger.phserver.hardware;

import java.io.IOException;

public interface SystemControl {

  void shutdown() throws IOException;

  void reboot() throws IOException;

  void restartApp() throws IOException;
}
