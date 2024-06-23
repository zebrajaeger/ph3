package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.hardware.systemcontrol.SystemControl;
import de.zebrajaeger.phserver.service.CommonService;
import de.zebrajaeger.phserver.util.StompUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class SystemSTOMPController {

    private final static Logger LOG = LoggerFactory.getLogger(SystemSTOMPController.class);

    private final SimpMessagingTemplate template;
    private final CommonService commonService;
    private final SystemControl systemControl;

    public SystemSTOMPController(SimpMessagingTemplate template, CommonService commonService, SystemControl systemControl) {
        this.template = template;
        this.commonService = commonService;
        this.systemControl = systemControl;
    }

    @MessageMapping("/system/shutdown")
    public void shutdown() throws IOException {
        LOG.info("Shutdown System (STOMP)");
        systemControl.shutdown();
    }

    @MessageMapping("/system/reboot")
    public void reboot() throws IOException {
        LOG.info("Reboot System (STOMP)");
        systemControl.reboot();
    }

    @MessageMapping("/system/restartApp")
    public void restartApp() throws IOException {
        LOG.info("Restart App (STOMP)");
        systemControl.restartApp();
    }

    @MessageMapping("/rpc/system/version")
    public void rpcGetVersion(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, commonService.getVersion());
    }
}
