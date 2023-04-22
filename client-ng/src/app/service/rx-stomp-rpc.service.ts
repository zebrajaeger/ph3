import {Injectable} from '@angular/core';
import {RxStomp, RxStompRPC, RxStompRPCConfig} from '@stomp/rx-stomp';
import {RxStompService} from "./rx-stomp.service";

@Injectable({
  providedIn: 'root',
})
export class RxStompRPCService extends RxStompRPC {

  constructor(rxStompService: RxStompService) {
    const rxStompRPCConfig: RxStompRPCConfig = {
      replyQueueName: `/topic/rpc-replies`,

      setupReplyQueue: (replyQueueName: string, stompService: RxStomp) => {
        return stompService.watch(replyQueueName);
      },
    };
    super(rxStompService, rxStompRPCConfig);
  }
}
