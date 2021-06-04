import {InjectableRxStompRPCConfig, RxStompService} from '@stomp/ng2-stompjs';

export const myRxStompRtcConfig: InjectableRxStompRPCConfig = {
  replyQueueName: `/topic/rpc-replies`,

  setupReplyQueue: (replyQueueName: string, stompService: RxStompService) => {
    return stompService.watch(replyQueueName);
  },
};
