package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.hardware.HardwareService;
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
    private final HardwareService hardwareService;
    private final CommonService commonService;


    public SystemSTOMPController(SimpMessagingTemplate template, HardwareService hardwareService, CommonService commonService) {
        this.template = template;
        this.hardwareService = hardwareService;
        this.commonService = commonService;
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
    public void rpcGetVersion(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        StompUtils.rpcSendResponse(template, id, destination, commonService.getVersion());
    }
}
