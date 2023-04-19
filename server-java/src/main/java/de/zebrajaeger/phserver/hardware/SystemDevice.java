package de.zebrajaeger.phserver.hardware;

import java.io.IOException;

public interface SystemDevice {
  void shutdown() throws IOException;
  void reboot() throws IOException;
}
