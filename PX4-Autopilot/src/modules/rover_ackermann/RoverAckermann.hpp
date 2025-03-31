/****************************************************************************
 *
 *   Copyright (c) 2024 PX4 Development Team. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 3. Neither the name PX4 nor the names of its contributors may be
 *    used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 ****************************************************************************/

#pragma once

// PX4 includes
#include <px4_platform_common/px4_config.h>
#include <px4_platform_common/defines.h>
#include <px4_platform_common/module.h>
#include <px4_platform_common/module_params.h>
#include <px4_platform_common/px4_work_queue/ScheduledWorkItem.hpp>
#include <lib/pure_pursuit/PurePursuit.hpp>

// uORB includes
#include <uORB/Publication.hpp>
#include <uORB/Subscription.hpp>
#include <uORB/topics/manual_control_setpoint.h>
#include <uORB/topics/parameter_update.h>
#include <uORB/topics/vehicle_status.h>
#include <uORB/topics/rover_ackermann_setpoint.h>
#include <uORB/topics/vehicle_local_position.h>
#include <uORB/topics/vehicle_attitude.h>


// Standard library includes
#include <math.h>
#include <matrix/matrix/math.hpp>
#include <lib/mathlib/math/filter/AlphaFilter.hpp>

// Local includes
#include "RoverAckermannGuidance/RoverAckermannGuidance.hpp"
#include "RoverAckermannControl/RoverAckermannControl.hpp"

using namespace time_literals;

// Constants
static constexpr float STICK_DEADZONE =
	0.1f; // [0, 1] Percentage of stick input range that will be interpreted as zero around the stick centered value
static constexpr float SPEED_THRESHOLD =
	0.1f; // [m/s] The minimum threshold for the speed measurement not to be interpreted as zero

class RoverAckermann : public ModuleBase<RoverAckermann>, public ModuleParams,
	public px4::ScheduledWorkItem
{
public:
	/**
	 * @brief Constructor for RoverAckermann
	 */
	RoverAckermann();
	~RoverAckermann() override = default;

	/** @see ModuleBase */
	static int task_spawn(int argc, char *argv[]);

	/** @see ModuleBase */
	static int custom_command(int argc, char *argv[]);

	/** @see ModuleBase */
	static int print_usage(const char *reason = nullptr);

	bool init();


protected:
	void updateParams() override;

private:
	void Run() override;

	/**
	 * @brief Update uORB subscriptions.
	 */
	void updateSubscriptions();

	// uORB subscriptions
	uORB::Subscription _manual_control_setpoint_sub{ORB_ID(manual_control_setpoint)};
	uORB::Subscription _parameter_update_sub{ORB_ID(parameter_update)};
	uORB::Subscription _vehicle_status_sub{ORB_ID(vehicle_status)};
	uORB::Subscription _vehicle_local_position_sub{ORB_ID(vehicle_local_position)};
	uORB::Subscription _vehicle_attitude_sub{ORB_ID(vehicle_attitude)};

	// uORB publications
	uORB::Publication<rover_ackermann_setpoint_s> _rover_ackermann_setpoint_pub{ORB_ID(rover_ackermann_setpoint)};

	// Class instances
	RoverAckermannGuidance _ackermann_guidance{this};
	RoverAckermannControl _ackermann_control{this};
	PurePursuit _posctl_pure_pursuit{this}; // Pure pursuit library

	// Variables
	matrix::Quatf _vehicle_attitude_quaternion{};
	int _nav_state{0};
	float _vehicle_forward_speed{0.f};
	float _vehicle_yaw{0.f};
	bool _armed{false};
	bool _course_control{false};
	Vector2f _pos_ctl_course_direction{};
	Vector2f _pos_ctl_start_position_ned{};
	Vector2f _curr_pos_ned{};
	float _vehicle_lateral_acceleration{0.f};
	AlphaFilter<float> _ax_filter;
	AlphaFilter<float> _ay_filter;
	AlphaFilter<float> _az_filter;

	// Parameters
	DEFINE_PARAMETERS(
		(ParamFloat<px4::params::RA_WHEEL_BASE>) _param_ra_wheel_base,
		(ParamFloat<px4::params::RA_MAX_STR_ANG>) _param_ra_max_steer_angle,
		(ParamFloat<px4::params::RA_MAX_SPEED>) _param_ra_max_speed,
		(ParamFloat<px4::params::RA_MAX_LAT_ACCEL>) _param_ra_max_lat_accel,
		(ParamFloat<px4::params::PP_LOOKAHD_MAX>) _param_pp_lookahd_max

	)

};
