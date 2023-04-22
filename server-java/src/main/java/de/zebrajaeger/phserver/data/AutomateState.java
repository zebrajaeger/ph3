package de.zebrajaeger.phserver.data;

public enum AutomateState {
  STOP_REQUEST, STOPPING, STOPPED, STOPPED_WITH_ERROR,
  STARTED,
  CMD_DELAY, CMD_SHOT, CMD_MOVE,
  APPLY_OFFSET, NORMALIZE_POSITION
}
