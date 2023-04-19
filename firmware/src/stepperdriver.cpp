#include "stepperdriver.h"

//------------------------------------------------------------------------------
StepperDriver::StepperDriver()
    : tmc429_()
//------------------------------------------------------------------------------
{
  memset(&status_, 0, sizeof(struct TMC429::Status));
}

//------------------------------------------------------------------------------
bool StepperDriver::setup(uint8_t pinCS, uint8_t clockMHz, Limit_t limits[3])
//------------------------------------------------------------------------------
{
  tmc429_.setup(pinCS, clockMHz);
  if (tmc429_.communicating()) {
    tmc429_.initialize();
    tmc429_.disableRightSwitches();
    tmc429_.setSwitchesActiveLow();

    for (uint8_t i = 0; i < 3; ++i) {
      initMotor(i, &(limits[i]));
    }

    return true;
  }
  return false;
}

//------------------------------------------------------------------------------
void StepperDriver::loop()
//------------------------------------------------------------------------------
{
  // read Status
  status_ = tmc429_.getStatus();
  steppers_[0].isAtTargetPos = status_.at_target_position_0;
  steppers_[0].pos.int32 = tmc429_.getActualPosition(0);
  steppers_[0].speed.int16 = tmc429_.getActualVelocityInHz(0);
  steppers_[1].isAtTargetPos = status_.at_target_position_1;
  steppers_[1].pos.int32 = tmc429_.getActualPosition(1);
  steppers_[1].speed.int16 = tmc429_.getActualVelocityInHz(1);
  steppers_[2].isAtTargetPos = status_.at_target_position_2;
  steppers_[2].pos.int32 = tmc429_.getActualPosition(2);
  steppers_[2].speed.int16 = tmc429_.getActualVelocityInHz(2);

  // Set Limit
  if (cmd_limit_axis_0_available) {
    tmc429_.setLimitsInHz(0, cmd_limit_axis_0_value.velocityMinHz, cmd_limit_axis_0_value.velocityMaxHz,
                          cmd_limit_axis_0_value.acceleration_max_hz_per_s);
  }
  if (cmd_limit_axis_1_available) {
    tmc429_.setLimitsInHz(1, cmd_limit_axis_1_value.velocityMinHz, cmd_limit_axis_1_value.velocityMaxHz,
                          cmd_limit_axis_1_value.acceleration_max_hz_per_s);
  }
  if (cmd_limit_axis_2_available) {
    tmc429_.setLimitsInHz(2, cmd_limit_axis_2_value.velocityMinHz, cmd_limit_axis_2_value.velocityMaxHz,
                          cmd_limit_axis_2_value.acceleration_max_hz_per_s);
  }

  // Set Velocity
  if (cmd_velocity_axis_0_available) {
    // Serial.println("cmd_velocity_axis_0_available");
    // Serial.println(cmd_velocity_axis_0_velocity.int32);
    tmc429_.setVelocityMode(0);
    tmc429_.setTargetVelocityInHz(0, cmd_velocity_axis_0_velocity.int32);
    cmd_velocity_axis_0_available = false;
  }
  if (cmd_velocity_axis_1_available) {
    // Serial.print("cmd_velocity_axis_1_available: ");
    // Serial.println(cmd_velocity_axis_1_velocity.int32);
    tmc429_.setVelocityMode(1);
    tmc429_.setTargetVelocityInHz(1, cmd_velocity_axis_1_velocity.int32);
    cmd_velocity_axis_1_available = false;
  }
  if (cmd_velocity_axis_2_available) {
    // Serial.print("cmd_velocity_axis_2_available: ");
    // Serial.println(cmd_velocity_axis_2_velocity.int32);
    tmc429_.setVelocityMode(2);
    tmc429_.setTargetVelocityInHz(2, cmd_velocity_axis_2_velocity.int32);
    cmd_velocity_axis_2_available = false;
  }

  // Set Position
  if (cmd_pos_axis_0_available) {
    // Serial.println("cmd_pos_axis_0_available");
    // Serial.println(cmd_pos_axis_0_pos.int32);
    tmc429_.setSoftMode(0);
    tmc429_.setTargetPosition(0, cmd_pos_axis_0_pos.int32);

    cmd_pos_axis_0_available = false;
  }
  if (cmd_pos_axis_1_available) {
    // Serial.print("cmd_pos_axis_1_available: ");
    // Serial.println(cmd_pos_axis_1_pos.int32);
    tmc429_.setSoftMode(1);
    tmc429_.setTargetPosition(1, cmd_pos_axis_1_pos.int32);
    cmd_pos_axis_1_available = false;
  }
  if (cmd_pos_axis_2_available) {
    // Serial.print("cmd_pos_axis_2_available: ");
    // Serial.println(cmd_pos_axis_2_pos.int32);
    tmc429_.setSoftMode(2);
    tmc429_.setTargetPosition(2, cmd_pos_axis_2_pos.int32);
    cmd_pos_axis_2_available = false;
  }

  // Set Actual Position
  if (cmd_actual_pos_axis_0_available) {
    // Serial.println("cmd_actual_pos_axis_0_available");
    // Serial.println(cmd_actual_pos_axis_0_pos.int32);
    tmc429_.setActualPosition(0, cmd_actual_pos_axis_0_pos.int32);

    cmd_actual_pos_axis_0_available = false;
  }
  if (cmd_actual_pos_axis_1_available) {
    // Serial.print("cmd_actual_pos_axis_1_available: ");
    // Serial.println(cmd_actual_pos_axis_1_pos.int32);
    tmc429_.setActualPosition(1, cmd_actual_pos_axis_1_pos.int32);
    cmd_actual_pos_axis_1_available = false;
  }
  if (cmd_actual_pos_axis_2_available) {
    // Serial.print("cmd_actual_pos_axis_2_available: ");
    // Serial.println(cmd_actual_pos_axis_2_pos.int32);
    tmc429_.setActualPosition(2, cmd_actual_pos_axis_2_pos.int32);
    cmd_actual_pos_axis_2_available = false;
  }

  // Reset Position
  if (cmd_reset_pos_available) {
    tmc429_.setActualPosition(0, 0);
    tmc429_.setTargetPosition(0, 0);
    tmc429_.setActualPosition(1, 0);
    tmc429_.setTargetPosition(1, 0);
    tmc429_.setActualPosition(2, 0);
    tmc429_.setTargetPosition(2, 0);
    cmd_reset_pos_available = false;
  }

  // Stop All
  if (cmd_stop_all_available) {
    tmc429_.stopAll();
    cmd_stop_all_available = false;
  }
}

//------------------------------------------------------------------------------
void StepperDriver::setPos(u8_t axisIndex, const u32_t &value)
//------------------------------------------------------------------------------
{
  if (axisIndex.uint8 == 0) {
    cmd_pos_axis_0_available = true;
    cmd_pos_axis_0_pos.uint32 = value.uint32;
  }
  if (axisIndex.uint8 == 1) {
    cmd_pos_axis_1_available = true;
    cmd_pos_axis_1_pos.uint32 = value.uint32;
  }
  if (axisIndex.uint8 == 2) {
    cmd_pos_axis_2_available = true;
    cmd_pos_axis_2_pos.uint32 = value.uint32;
  }
}

//------------------------------------------------------------------------------
void StepperDriver::setActualPos(u8_t axisIndex, const u32_t &value)
//------------------------------------------------------------------------------
{
  if (axisIndex.uint8 == 0) {
    cmd_actual_pos_axis_0_available = true;
    cmd_actual_pos_axis_0_pos.uint32 = value.uint32;
  }
  if (axisIndex.uint8 == 1) {
    cmd_actual_pos_axis_1_available = true;
    cmd_actual_pos_axis_1_pos.uint32 = value.uint32;
  }
  if (axisIndex.uint8 == 2) {
    cmd_actual_pos_axis_2_available = true;
    cmd_actual_pos_axis_2_pos.uint32 = value.uint32;
  }
}

//------------------------------------------------------------------------------
void StepperDriver::setVelocity(u8_t axisIndex, const u32_t &value)
//------------------------------------------------------------------------------
{
  if (axisIndex.uint8 == 0) {
    cmd_velocity_axis_0_available = true;
    cmd_velocity_axis_0_velocity.uint32 = value.uint32;
    
  } else if (axisIndex.uint8 == 1) {
    cmd_velocity_axis_1_available = true;
    cmd_velocity_axis_1_velocity.uint32 = value.uint32;

  } else if (axisIndex.uint8 == 2) {
    cmd_velocity_axis_2_available = true;
    cmd_velocity_axis_2_velocity.uint32 = value.uint32;
  }
}

//------------------------------------------------------------------------------
void StepperDriver::resetAllPos()
//------------------------------------------------------------------------------
{
  cmd_reset_pos_available = true;
}

//------------------------------------------------------------------------------
void StepperDriver::stopAll()
//------------------------------------------------------------------------------
{
  cmd_stop_all_available = true;
}

//------------------------------------------------------------------------------
const StepperDriver::Stepper_t &StepperDriver::getStepper(const uint8_t axisIndex) const
//------------------------------------------------------------------------------
{
  return steppers_[axisIndex];
}

//------------------------------------------------------------------------------
TMC429::Status StepperDriver::getStatus()
//------------------------------------------------------------------------------
{
  return status_;
}

//------------------------------------------------------------------------------
StepperDriver::MovementStatus_t StepperDriver::getMovementStatus()
//------------------------------------------------------------------------------
{
  MovementStatus_t res;
  memset(&res, 0, sizeof res);

  res.fields.atTargetPosition0 = status_.at_target_position_0;
  res.fields.isMoving0 = steppers_[0].speed.uint16 != 0;
  res.fields.atTargetPosition1 = status_.at_target_position_1;
  res.fields.isMoving1 = steppers_[1].speed.uint16 != 0;
  res.fields.atTargetPosition2 = status_.at_target_position_2;
  res.fields.isMoving2 = steppers_[2].speed.uint16 != 0;

  return res;
}

//------------------------------------------------------------------------------
bool StepperDriver::isMoving()
//------------------------------------------------------------------------------
{
  return steppers_[0].speed.uint16 != 0 || steppers_[1].speed.uint16 != 0 || steppers_[2].speed.uint16 != 0;
}

//------------------------------------------------------------------------------
void StepperDriver::statistic()
//------------------------------------------------------------------------------
{
  Serial.print("Steppers: {0:");
  Serial.print(steppers_[0].pos.int32);
  Serial.print(", 1:");
  Serial.print(steppers_[1].pos.int32);
  Serial.print(", 2:");
  Serial.print(steppers_[2].pos.int32);
  Serial.println("}");
}

//------------------------------------------------------------------------------
bool StepperDriver::isCommunicating()
//------------------------------------------------------------------------------
{
  return tmc429_.communicating();
}

//------------------------------------------------------------------------------
void StepperDriver::initMotor(uint8_t axisIndex, Limit_t *limit)
//------------------------------------------------------------------------------
{
  tmc429_.stop(axisIndex);  // velocity mode, speed 0

  tmc429_.setLimitsInHz(axisIndex, limit->velocityMinHz, limit->velocityMaxHz, limit->acceleration_max_hz_per_s);

  tmc429_.setActualPosition(axisIndex, 0);
  tmc429_.setTargetPosition(axisIndex, 0);

  tmc429_.disableLeftSwitchStop(axisIndex);
  tmc429_.disableRightSwitchStop(axisIndex);
  tmc429_.disableSwitchSoftStop(axisIndex);

  tmc429_.setSoftMode(axisIndex);
}

//------------------------------------------------------------------------------
void StepperDriver::setLimit(u8_t axisIndex, const Limit_t &limit)
//------------------------------------------------------------------------------
{
  if (axisIndex.uint8 == 0) {
    cmd_limit_axis_0_available = true;
    cmd_limit_axis_0_value.velocityMinHz = limit.velocityMinHz;
    cmd_limit_axis_0_value.velocityMaxHz = limit.velocityMaxHz;
    cmd_limit_axis_0_value.acceleration_max_hz_per_s = limit.acceleration_max_hz_per_s;
  } else if (axisIndex.uint8 == 1) {
    cmd_limit_axis_1_available = true;
    cmd_limit_axis_1_value.velocityMinHz = limit.velocityMinHz;
    cmd_limit_axis_1_value.velocityMaxHz = limit.velocityMaxHz;
    cmd_limit_axis_1_value.acceleration_max_hz_per_s = limit.acceleration_max_hz_per_s;
  } else if (axisIndex.uint8 == 2) {
    cmd_limit_axis_2_available = true;
    cmd_limit_axis_2_value.velocityMinHz = limit.velocityMinHz;
    cmd_limit_axis_2_value.velocityMaxHz = limit.velocityMaxHz;
    cmd_limit_axis_2_value.acceleration_max_hz_per_s = limit.acceleration_max_hz_per_s;
  }
}
