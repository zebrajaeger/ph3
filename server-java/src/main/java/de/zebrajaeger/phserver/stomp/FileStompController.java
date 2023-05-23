package de.zebrajaeger.phserver.stomp;

import de.zebrajaeger.phserver.service.FileService;
import de.zebrajaeger.phserver.util.StompUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Collection;

@Controller
public class FileStompController {
    private final SimpMessagingTemplate template;
    private final FileService fileService;

    public FileStompController(SimpMessagingTemplate template, FileService fileService) {
        this.template = template;
        this.fileService = fileService;
    }

    @MessageMapping("/rpc/files/papywizard/files")
    public void getPapywizardFilesRpc(@Header("correlation-id") String id, @Header("reply-to") String destination) {
        final Collection<String> fileNames = fileService.getPapywizardFileNamesOrderedByTimestamp(50);
        StompUtils.rpcSendResponse(template, id, destination, fileNames);
    }

    @MessageMapping("/rpc/files/papywizard/download")
    public void getPapywizardFilesRpc(
            @Header("correlation-id") String id,
            @Header("reply-to") String destination,
            @Payload String fileName) {
        final String s = fileService.readStringFile(fileName);
        StompUtils.rpcSendResponse(template, id, destination, s);
    }
}
