import time
from mavsdk.offboard import Attitude, VelocityNedYaw

import asyncio
import math

MAX_ANGLE = 35
BASE_THRUST = 0.7  # 悬停时推力值（需实测调整）
THRUST_AMPLITUDE = 0.15  # 振荡幅度


async def roller_coaster(drone, duration, rotation_speed, altitude):
    """
    Achieve continuous 360-degree roll and pitch coupling motion of the drone

    drone: 无人机实例
    duration: 总持续时间（秒）
    rotation_speed: 旋转速度（度/秒）
    altitude: 飞行高度（米）
    """
    start_time = time.time()
    phase_shift = math.pi / 2  # 横滚和俯仰的相位差

    while time.time() - start_time < duration:

        # # 实时获取姿态角监控
        # attitude = await drone.telemetry.altitude()
        # if abs(math.degrees(attitude.pitch_rad)) > 40 or abs(math.degrees(attitude.roll_rad)) > 40:
        #     print("姿态角超限！执行紧急降落")
        #     await drone.action.land()
        #     return
        #
        # # 监控高度异常
        # current_alt = await get_current_altitude(drone)
        # if current_alt < 1.0:  # 离地高度低于1米时终止
        #     print("高度过低！终止程序")
        #     await drone.action.land()
        #     return

        # 计算当前相位（基于时间累积而非固定间隔）
        t = time.time() - start_time
        angular_progress = math.radians(rotation_speed * t)

        # 生成三维空间8字轨迹参数
        roll_angle = math.degrees(math.sin(angular_progress)) * 45  # X轴横滚 ±45°
        roll_angle = max(min(roll_angle, MAX_ANGLE), -MAX_ANGLE)  # 限制在±35°内
        pitch_angle = math.degrees(math.sin(angular_progress + phase_shift)) * 45  # Y轴俯仰 ±45°
        pitch_angle = max(min(pitch_angle, MAX_ANGLE), -MAX_ANGLE)
        yaw_rate = 15 * math.sin(angular_progress / 2)  # 偏航速率变化

        # # 创建四元数姿态控制指令（更精确的空间定位）
        # q = Quaternion.from_euler(
        #     math.radians(roll_angle),
        #     math.radians(pitch_angle),
        #     math.radians(yaw_rate * t)
        # )

        # 构建组合控制指令（姿态+升力）
        thrust = BASE_THRUST + THRUST_AMPLITUDE * math.sin(angular_progress)
        # thrust = 0.6 + 0.2 * math.sin(angular_progress)  # 动态推力补偿
        attitude_command = Attitude(
            roll_angle,
            pitch_angle,
            yaw_rate * t,
            thrust
        )

        # 发送组合控制指令
        await drone.offboard.set_attitude(attitude_command)

        # 高度保持PID控制（100Hz高频更新）
        current_alt = await get_current_altitude(drone)
        alt_error = altitude - current_alt
        vertical_thrust = pid_controller(alt_error)  # 需要实现PID控制器
        await drone.offboard.set_velocity_ned(
            VelocityNedYaw(0, 0, vertical_thrust, 0)
        )

        await asyncio.sleep(0.01)  # 保持10ms控制周期


INTEGRAL_LIMIT = 0.3


async def pid_controller(error, kp=0.8, ki=0.02, kd=0.15):
    """
    简单PID高度控制器
    """
    global integral, prev_error
    integral += error * 0.01  # 积分项
    integral = max(min(integral, INTEGRAL_LIMIT), -INTEGRAL_LIMIT)
    derivative = (error - prev_error) / 0.01  # 微分项
    prev_error = error
    return kp * error + ki * integral + kd * derivative


# # 使用独立任务处理高度控制
# async def altitude_control_task(drone, target_alt):
#     while True:
#         current_alt = await get_current_altitude(drone)
#         alt_error = target_alt - current_alt
#         vertical_thrust = await pid_controller(alt_error)
#         await drone.offboard.set_velocity_ned(VelocityNedYaw(0, 0, vertical_thrust, 0))
#         await asyncio.sleep(0.02)  # 50Hz更新


async def get_current_altitude(drone):
    """获取无人机相对起降点的高度（单位：米）"""
    try:
        # 获取完整的定位信息（需要MAVSDK 0.8.0+版本）
        async for position in drone.telemetry.position():
            # 返回相对高度（不受地面气压变化影响）
            return position.relative_altitude_m

        # 备用方案：如果流式接口不可用
        # position = await drone.telemetry.get_position()
        # return position.relative_altitude_m

    except Exception as e:
        print(f"高度获取失败: {str(e)}")
        # 返回安全默认值或重新抛出异常
        return 0.0  # 根据实际需求调整

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
