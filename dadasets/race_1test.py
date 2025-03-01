import asyncio
import csv
from mavsdk import System
from mavsdk.offboard import AttitudeRate, VelocityBodyYawspeed, OffboardError, PositionNedYaw
from mavsdk.telemetry import LandedState


# 读取 IMU 数据
def get_current_imu_data(imu_data, time):
    return next((data for data in imu_data if time <= data[0]), None)


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
    startSetpoint = PositionNedYaw(0.0, 0.0, 0.0, 0.0)
    await drone.offboard.set_position_ned(startSetpoint)


    print("-- Starting offboard mode")
    try:
        await drone.offboard.start()
    except OffboardError as error:
        print(f"Starting offboard mode failed: {error}")
        print("-- Disarming")
        await drone.action.disarm()
        return

    await drone.offboard.set_position_ned(PositionNedYaw(0,0,-10,0))
    await asyncio.sleep(10)

    imu_data = []

    # 读取 IMU 数据
    imu_file = "imu.txt"
    with open(imu_file, newline="") as csvfile:
        reader = csv.reader(csvfile, delimiter=' ')
        next(reader)  # 跳过标题行
        for row in reader:
            ts, wx, wy, wz, ax, ay, az = map(float, row)
            imu_data.append((ts, wx, wy, wz, ax, ay, az))

    print("-- Executing IMU-based attitude & acceleration control")
    total_duration = imu_data[-1][0]
    t = 0
    vx, vy, vz = 0.0, 0.0, 0.0  # 初始速度

    while t <= total_duration:
        current_imu = get_current_imu_data(imu_data, t)
        if current_imu is None:
            break

        _, wx, wy, wz, ax, ay, az = current_imu  # 读取当前时间点的 IMU 数据

        print(f"Applying wx: {wx}, wy: {wy}, wz: {wz} | ax: {ax}, ay: {ay}, az: {az}")

        # 计算新的速度，假设每个时间步长 Δt = 0.05s
        dt = 0.05
        vx += ax * dt
        vy += ay * dt
        vz += az * dt

        # 设定无人机的角速度（旋转控制）
        await drone.offboard.set_attitude_rate(
            AttitudeRate(wx, wy, wz, 0.6)
        )
        await asyncio.sleep(dt)

        # 设定无人机的线速度（基于加速度计算）
        await drone.offboard.set_velocity_body(
            VelocityBodyYawspeed(vx, vy, vz, wz)
        )

        # 控制时间步长
        await asyncio.sleep(dt)
        t += dt

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
