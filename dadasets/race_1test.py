import asyncio
import csv
from mavsdk import System
from mavsdk.offboard import AttitudeRate, VelocityBodyYawspeed, OffboardError, PositionNedYaw, VelocityNedYaw, \
    AccelerationNed
from mavsdk.telemetry import LandedState


def get_current_imu_data(imu_data, time):
    valid_data = [data for data in imu_data if data[0] >= time]  # 选择下一个时间点的数据
    return valid_data[0] if valid_data else None


async def run():
    drone = System()
    await drone.connect(system_address="udp://:14540")

    print("Waiting for drone to connect...")
    async for state in drone.core.connection_state():
        if state.is_connected:
            print("-- Connected to drone!")
            break

    print("Waiting for global position estimate...")
    async for health in drone.telemetry.health():
        if health.is_global_position_ok and health.is_home_position_ok:
            print("-- Global position estimate OK")
            break

    print("-- Arming")
    await drone.action.arm()

    print("-- Setting initial setpoint")
    try:
        await drone.offboard.set_attitude_rate(AttitudeRate(0.0, 0.0, 0.0, 0))
        await drone.offboard.set_velocity_body(VelocityBodyYawspeed(0.0, 0.0, 0.0, 0.0))
    except OffboardError as error:
        print(f"Setting initial setpoint failed: {error}")
        return

    print("-- Starting offboard mode")
    try:
        await drone.offboard.start()
    except OffboardError as error:
        print(f"Starting offboard mode failed: {error}")
        print("-- Disarming")
        await drone.action.disarm()
        return

    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, -40, 0))
    await asyncio.sleep(10)

    imu_data = []

    # 读取 IMU 数据
    imu_file = "imu.csv"
    with open(imu_file, newline="") as csvfile:
        reader = csv.reader(csvfile, delimiter=' ')
        next(reader)  # 跳过标题行
        for row in reader:
            ts, wx, wy, wz, ax, ay, az = map(float, row)
            imu_data.append((ts, wx, wy, wz, ax, ay, az))

    print("-- Executing IMU-based attitude & acceleration control")

    i = 0  # 设定索引
    while i < len(imu_data):
        current_imu = imu_data[i]
        ts, wx, wy, wz, ax, ay, az = current_imu

        print(f"Applying wx: {wx}, wy: {wy}, wz: {wz} | ax: {ax}, ay: {ay}, az: {az}")

        # 更新角速度
        await drone.offboard.set_attitude_rate(
            AttitudeRate(wx, wy, wz, 0.6)
        )

        # 更新线速度
        await drone.offboard.set_acceleration_ned(AccelerationNed(ax, ay, az))
        # await drone.offboard.set_velocity_ned(VelocityNedYaw(ax * 0.05, ay * 0.05, az * 0.05, wz))
        # await drone.offboard.set_velocity_body(
        #     VelocityBodyYawspeed(ax * 0.05, ay * 0.05, az * 0.05, wz)
        # )

        # 控制时间步长
        await asyncio.sleep(0.0085)

        i += 1  # 递增索引，确保切换到下一个数据点

    print("-- IMU-based control completed")

    print("-- Landing")
    await drone.action.land()

    async for state in drone.telemetry.landed_state():
        if state == LandedState.ON_GROUND:
            break

    print("-- Stopping offboard mode")
    try:
        await drone.offboard.stop()
    except Exception as error:
        print(f"Stopping offboard mode failed: {error}")

    print("-- Disarming")
    await drone.action.disarm()


if __name__ == "__main__":
    asyncio.run(run())
