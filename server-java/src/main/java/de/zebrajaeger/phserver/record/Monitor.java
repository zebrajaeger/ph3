package de.zebrajaeger.phserver.record;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.monitor.AbstractStateMachineMonitor;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public class Monitor extends AbstractStateMachineMonitor<States, Events> {
    @Override
    public void transition(StateMachine<States, Events> stateMachine, Transition<States, Events> transition, long duration) {
//        String s = transition.getSource() == null ? "<null>" : transition.getSource().getId().toString();
//        String t = transition.getTarget() == null ? "<null>" : transition.getTarget().getId().toString();
//        log.info("SM T: {} -> {}, {}", s, t, duration);
    }

    @Override
    public void action(StateMachine<States, Events> stateMachine, Function<StateContext<States, Events>, Mono<Void>> action, long duration) {
//        log.info("SM A: {}, {}", action, duration);
    }
}
