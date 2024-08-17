package de.zebrajaeger.phserver.record;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.monitor.StateMachineMonitor;

@Configuration
@EnableStateMachine
public class RecordSMConfig extends StateMachineConfigurerAdapter<States, Events> {
    @Bean
    public StateMachineMonitor<States, Events> stateMachineMonitor() {
        return new Monitor();
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .machineId("RecordSM")
                .autoStartup(true)
                .and().withMonitoring().monitor(stateMachineMonitor());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
                .withStates()
                .initial(States.STOPPED)
                .state(States.STOPPED)

                .state(States.RUNNING)

                .and().withStates()
                .parent(States.RUNNING)
                .initial(States.STARTING)
                .state(States.STARTING)
                .state(States.IDLE)

                .state(States.EXEC_MOVE).state(States.EXEC_MOVE_STOP)
                .state(States.EXEC_MOVE_PAUSE_RUNNING).state(States.EXEC_MOVE_PAUSE_IDLE)

                .state(States.EXEC_SHOT).state(States.EXEC_SHOT_STOP)
                .state(States.EXEC_SHOT_PAUSE_RUNNING).state(States.EXEC_SHOT_PAUSE_IDLE)

                .state(States.EXEC_DELAY).state(States.EXEC_DELAY_STOP)
                .state(States.EXEC_DELAY_PAUSE_RUNNING).state(States.EXEC_DELAY_PAUSE_IDLE)

                .state(States.EXEC_NORMALIZE_POSITION)

                .state(States.EXEC_APPLY_OFFSET)

                .state(States.WRITE_PAPYWIZARD)

                .state(States.STOPPED_SUCCESSFULLY)
                .state(States.STOPPED_WITH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
                // ========= Main SM =========
                // Stopped
                .withExternal().source(States.STOPPED).target(States.RUNNING).event(Events.START_PAUSE)

                // Running
                .and().withExternal().source(States.RUNNING).target(States.STOPPED_WITH_ERROR).event(Events.ERROR)

                // ========= SUB-SM =========
                // START
                .and().withExternal().source(States.STARTING).target(States.IDLE).event(Events.STARTED)

                // IDLE
                .and().withExternal().source(States.IDLE).target(States.WRITE_PAPYWIZARD).event(Events.NO_MORE_COMMANDS)
                .and().withExternal().source(States.IDLE).target(States.EXEC_MOVE).event(Events.MOVE)
                .and().withExternal().source(States.IDLE).target(States.EXEC_DELAY).event(Events.DELAY)
                .and().withExternal().source(States.IDLE).target(States.EXEC_SHOT).event(Events.SHOT)
                .and().withExternal().source(States.IDLE).target(States.EXEC_APPLY_OFFSET).event(Events.APPLY_OFFSET)
                .and().withExternal().source(States.IDLE).target(States.EXEC_NORMALIZE_POSITION).event(Events.NORMALIZE_POSITION)
                .and().withExternal().source(States.IDLE).target(States.STOPPED_WITH_ERROR).event(Events.STOP)

                // Move
                .and().withExternal().source(States.EXEC_MOVE).target(States.IDLE).event(Events.MOVE_DONE)
                .and().withExternal().source(States.EXEC_MOVE).target(States.EXEC_MOVE_STOP).event(Events.STOP)
                .and().withExternal().source(States.EXEC_MOVE_STOP).target(States.STOPPED_WITH_ERROR).event(Events.MOVE_DONE)
                .and().withExternal().source(States.EXEC_MOVE).target(States.EXEC_MOVE_PAUSE_RUNNING).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_MOVE_PAUSE_RUNNING).target(States.EXEC_MOVE).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_MOVE_PAUSE_RUNNING).target(States.EXEC_MOVE_PAUSE_IDLE).event(Events.MOVE_DONE)
                .and().withExternal().source(States.EXEC_MOVE_PAUSE_IDLE).target(States.IDLE).event(Events.START_PAUSE)

                // Delay
                .and().withExternal().source(States.EXEC_DELAY).target(States.IDLE).event(Events.DELAY_DONE)
                .and().withExternal().source(States.EXEC_DELAY).target(States.EXEC_DELAY_STOP).event(Events.STOP)
                .and().withExternal().source(States.EXEC_DELAY_STOP).target(States.STOPPED_WITH_ERROR).event(Events.DELAY_DONE)
                .and().withExternal().source(States.EXEC_DELAY).target(States.EXEC_DELAY_PAUSE_RUNNING).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_DELAY_PAUSE_RUNNING).target(States.EXEC_DELAY).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_DELAY_PAUSE_RUNNING).target(States.EXEC_DELAY_PAUSE_IDLE).event(Events.DELAY_DONE)
                .and().withExternal().source(States.EXEC_DELAY_PAUSE_IDLE).target(States.IDLE).event(Events.START_PAUSE)

                // Shot
                .and().withExternal().source(States.EXEC_SHOT).target(States.IDLE).event(Events.SHOT_DONE)
                .and().withExternal().source(States.EXEC_SHOT).target(States.EXEC_SHOT_STOP).event(Events.STOP)
                .and().withExternal().source(States.EXEC_SHOT_STOP).target(States.STOPPED_WITH_ERROR).event(Events.SHOT_DONE)
                .and().withExternal().source(States.EXEC_SHOT).target(States.EXEC_SHOT_PAUSE_RUNNING).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_SHOT_PAUSE_RUNNING).target(States.EXEC_SHOT).event(Events.START_PAUSE)
                .and().withExternal().source(States.EXEC_SHOT_PAUSE_RUNNING).target(States.EXEC_SHOT_PAUSE_IDLE).event(Events.SHOT_DONE)
                .and().withExternal().source(States.EXEC_SHOT_PAUSE_IDLE).target(States.IDLE).event(Events.START_PAUSE)

                // Offset
                .and().withExternal().source(States.EXEC_APPLY_OFFSET).target(States.IDLE).event(Events.APPLY_OFFSET_DONE)
                .and().withExternal().source(States.EXEC_APPLY_OFFSET).target(States.EXEC_APPLY_OFFSET_STOP).event(Events.STOP)
                .and().withExternal().source(States.EXEC_APPLY_OFFSET_STOP).target(States.STOPPED_WITH_ERROR).event(Events.APPLY_OFFSET_DONE)

                // Normalize Position
                .and().withExternal().source(States.EXEC_NORMALIZE_POSITION).target(States.IDLE).event(Events.NORMALIZE_POSITION_DONE)
                .and().withExternal().source(States.EXEC_NORMALIZE_POSITION).target(States.EXEC_NORMALIZE_POSITION_STOP).event(Events.STOP)
                .and().withExternal().source(States.EXEC_NORMALIZE_POSITION_STOP).target(States.STOPPED_WITH_ERROR).event(Events.NORMALIZE_POSITION_DONE)

                // Papywizard
                .and().withExternal().source(States.WRITE_PAPYWIZARD).target(States.STOPPED_SUCCESSFULLY).event(Events.SAVE_PAPYWIZARD_DONE)

                // Success
                .and().withExternal().source(States.STOPPED_SUCCESSFULLY).target(States.STOPPED).event(Events.DONE)

                // Error
                .and().withExternal().source(States.STOPPED_WITH_ERROR).target(States.STOPPED).event(Events.DONE);
    }
}
