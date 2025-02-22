import time
from mavsdk.offboard import Attitude, VelocityNedYaw

import asyncio
import math

MAX_ANGLE = 35
BASE_THRUST = 0.7
THRUST_AMPLITUDE = 0.15


async def roller_coaster(drone, duration, rotation_speed, altitude):
    """
    Achieve continuous 360-degree roll and pitch coupling motion of the drone

    drone: drone object
    duration: total duration time
    rotation_speed: rotating speed
    altitude: height
    """
    start_time = time.time()
    phase_shift = math.pi / 2

    while time.time() - start_time < duration:
        # Calculate the current phase (based on cumulative time rather than fixed intervals)
        t = time.time() - start_time
        angular_progress = math.radians(rotation_speed * t)

        # Generate parameters for a figure-eight trajectory in three-dimensional space.
        roll_angle = math.degrees(math.sin(angular_progress)) * 45
        roll_angle = max(min(roll_angle, MAX_ANGLE), -MAX_ANGLE)
        pitch_angle = math.degrees(math.sin(angular_progress + phase_shift)) * 45
        pitch_angle = max(min(pitch_angle, MAX_ANGLE), -MAX_ANGLE)
        yaw_rate = 15 * math.sin(angular_progress / 2)

        # Construct combined control commands (attitude + lift)
        thrust = BASE_THRUST + THRUST_AMPLITUDE * math.sin(angular_progress)
        # thrust = 0.6 + 0.2 * math.sin(angular_progress)
        attitude_command = Attitude(
            roll_angle,
            pitch_angle,
            yaw_rate * t,
            thrust
        )

        await drone.offboard.set_attitude(attitude_command)

        current_alt = await get_current_altitude(drone)
        alt_error = altitude - current_alt
        vertical_thrust = pid_controller(alt_error)
        await drone.offboard.set_velocity_ned(
            VelocityNedYaw(0, 0, vertical_thrust, 0)
        )

        await asyncio.sleep(0.01)


INTEGRAL_LIMIT = 0.3


async def pid_controller(error, kp=0.8, ki=0.02, kd=0.15):
    """
    Simple PID height control
    """
    global integral, prev_error
    integral += error * 0.01
    integral = max(min(integral, INTEGRAL_LIMIT), -INTEGRAL_LIMIT)
    derivative = (error - prev_error) / 0.01
    prev_error = error
    return kp * error + ki * integral + kd * derivative


async def get_current_altitude(drone):
    """ Get the height of the drone relative to the takeoff point (unit: meters)"""
    try:
        # Obtain complete positioning information (requires MAVSDK version 0.8.0 or higher)
        async for position in drone.telemetry.position():
            return position.relative_altitude_m

    except Exception as e:
        print(f"Height acquisition failed: {str(e)}")

        return 0.0

# Full 360-degree roll and pitch simulation
# for t in range(time_interval):
#     # Calculate the roll and pitch to create a smooth circular motion
#     roll_angle = 90 * math.sin(2 * math.pi * t / time_interval)  # Roll on X axis
#     pitch_angle = 90 * math.sin(2 * math.pi * t / time_interval)  # Pitch on Y axis
#     yaw_angle = 0  # Maintain yaw constant (facing the same direction)
#
#     # Set the attitude
#     await drone.offboard.set_attitude(Attitude(roll_angle, pitch_angle, yaw_angle, 0))
#     await asyncio.sleep(1)
#
#     # Set the attitude rate for a smooth transition
#     await drone.offboard.set_attitude_rate(AttitudeRate(0, 0, speed, 0))
#     await asyncio.sleep(0.1)
#
#     # Maintain the fixed altitude during the loop
#     await drone.offboard.set_position_ned(PositionNedYaw(0, 0, -z_position, 0))
#     await asyncio.sleep(10)
#
#     # Wait for the next time step
#     await asyncio.sleep(0.1)
