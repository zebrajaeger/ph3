package de.zebrajaeger.phserver.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.zebrajaeger.phserver.data.Version;
import de.zebrajaeger.phserver.hardware.HardwareService;
import de.zebrajaeger.phserver.util.StompUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SystemSTOMPController {

  private final static Logger LOG = LoggerFactory.getLogger(SystemSTOMPController.class);

  private final SimpMessagingTemplate template;
  private final HardwareService hardwareService;

  public SystemSTOMPController(SimpMessagingTemplate template, HardwareService hardwareService) {
    this.template = template;
    this.hardwareService = hardwareService;
  }

  @Value("${project.version}")
  private String projectVersion;
  @Value("${git.build.version}")
  private String gitCommitId;
  @Value("${git.commit.id.abbrev}")
  private String gitCommitIdAbbrev;
  @Value("${git.build.time}")
  private String gitBuildTime;
  @Value("${git.commit.time}")
  private String gitCommitTime;
  private Version version;

  @PostConstruct
  public void init() {
    version = new Version(projectVersion, gitCommitId, gitCommitIdAbbrev, gitCommitTime,
        gitBuildTime);
  }

  @MessageMapping("/system/shutdown")
  public void shutdown() throws IOException {
    LOG.info("Shutdown System (STOMP)");
    hardwareService.getSystemDevice().shutdown();
  }

  @MessageMapping("/system/reboot")
  public void reboot() throws IOException {
    LOG.info("Reboot System (STOMP)");
    hardwareService.getSystemDevice().reboot();
  }

  @MessageMapping("/system/restartApp")
  public void restartApp() throws IOException {
    LOG.info("Restart App (STOMP)");
    hardwareService.getSystemDevice().restartApp();
  }

  @MessageMapping("/rpc/system/version")
  public void rpcGetVersion(@Header("correlation-id") String id,
      @Header("reply-to") String destination)
      throws JsonProcessingException {
    StompUtils.rpcSendResponse(template, id, destination, version);
  }
}
