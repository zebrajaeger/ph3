#pragma once

#include <Arduino.h>
#include "tmc429.h"

#include "types.h"

class StepperDriver {
 public:
  typedef struct {
    uint32_t velocityMinHz;
    uint32_t velocityMaxHz;
    uint32_t acceleration_max_hz_per_s;
  } Limit_t;

  typedef struct {
    u32_t pos;
    u16_t speed;
    bool isAtTargetPos;
  } Stepper_t;

  typedef union {
    struct {
      uint8_t atTargetPosition0 : 1;
      uint8_t isMoving0 : 1;
      uint8_t atTargetPosition1 : 1;
      uint8_t isMoving1 : 1;
      uint8_t atTargetPosition2 : 1;
      uint8_t isMoving2 : 1;
    } fields;
    u8_t u8;
  } MovementStatus_t;

  StepperDriver();
  bool setup(uint8_t pinCS, uint8_t clockMHz, Limit_t limits[3]);
  void loop();

  void setLimit(u8_t axisIndex, const Limit_t &limit);
  void setPos(u8_t axisIndex, const u32_t &value);
  void setVelocity(u8_t axisIndex, const u32_t &value);
  void resetAllPos();
  void stopAll();
  TMC429::Status getStatus();

  const Stepper_t &getStepper(const uint8_t axisIndex) const;
  MovementStatus_t getMovementStatus();
  bool isMoving();
  void statistic();

 protected:
  bool isCommunicating();
  void initMotor(uint8_t axisIndex, Limit_t *limit);
  void setActualPos(u8_t axisIndex, u32_t position);
  void resetPos(u8_t axisIndex);

 private:
  TMC429 tmc429_;
  Stepper_t steppers_[3];
  TMC429::Status status_;

  // TODO refactor me
  volatile bool cmd_velocity_axis_0_available;
  volatile u32_t cmd_velocity_axis_0_velocity;
  volatile bool cmd_pos_axis_0_available;
  volatile u32_t cmd_pos_axis_0_pos;
  volatile bool cmd_limit_axis_0_available;
  volatile Limit_t cmd_limit_axis_0_value;

  // TODO refactor me
  volatile bool cmd_velocity_axis_1_available;
  volatile u32_t cmd_velocity_axis_1_velocity;
  volatile bool cmd_pos_axis_1_available;
  volatile u32_t cmd_pos_axis_1_pos;
  volatile bool cmd_limit_axis_1_available;
  volatile Limit_t cmd_limit_axis_1_value;

  volatile bool cmd_reset_pos_available;
  volatile bool cmd_stop_all_available;
};