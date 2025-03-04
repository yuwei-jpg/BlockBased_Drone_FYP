import asyncio
import csv
from mavsdk import System
from mavsdk.offboard import PositionNedYaw, VelocityNedYaw
from mavsdk.offboard import OffboardError
from mavsdk.telemetry import LandedState


def get_current_waypoint(waypoints, time):
    return next((wp for wp in waypoints if time <= wp[0]), None)


async def log_position_velocity(drone, filename="mavsdk_position_velocity.csv"):
    """ 监听 MAVSDK 的 PositionVelocityNed 数据并存入 CSV 文件 """
    file_path = f"{filename}"

    with open(file_path, mode="w", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["timestamp", "north_m", "east_m", "down_m", "vx_m_s", "vy_m_s", "vz_m_s"])
        async for data in drone.telemetry.position_velocity_ned():
            timestamp = asyncio.get_event_loop().time()  # 获取当前时间戳
            writer.writerow([
                timestamp,
                data.position.north_m, data.position.east_m, data.position.down_m,
                data.velocity.north_m_s, data.velocity.east_m_s, data.velocity.down_m_s
            ])
            print(
                f"Logged - Pos: N:{data.position.north_m:.3f}, E:{data.position.east_m:.3f}, D:{data.position.down_m:.3f} | "
                f"Vel: Vx:{data.velocity.north_m_s:.3f}, Vy:{data.velocity.east_m_s:.3f}, Vz:{data.velocity.down_m_s:.3f}")

    return file_path


# async def log_real_position(drone): """ 实时打印 MAVSDK 反馈的无人机位置 """ async for position in drone.telemetry.position():
# print( f"Real Position - Lat: {position.latitude:.6f}, Lon: {position.longitude:.6f},
# Alt: {position.absolute_altitude:.2f}")


async def run():
    mode_descriptions = {
        0: "On the ground",
        10: "Initial climbing state",
        20: "Initial holding after climb",
        30: "Moving to start point",
        40: "Holding at start point",
        50: "Moving to maneuvering start point",
        60: "Holding at maneuver start point",
        70: "Maneuvering (trajectory)",
        80: "Holding at the end of the trajectory coordinate",
        90: "Returning to home coordinate",
        100: "Landing"
    }

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
        print(f"Starting offboard mode failed: {error._result.result}")
        print("-- Disarming")
        await drone.action.disarm()
        return
    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, -5, 0))
    await asyncio.sleep(4)
    waypoints = []

    # read the csv file
    csv_file = "z_trajectory.csv"
    with open(csv_file, newline="") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            waypoints.append((float(row["t"]),
                              float(row["px"]),
                              float(row["py"]),
                              float(row["pz"]),
                              float(row["vx"]),
                              float(row["vy"]),
                              float(row["vz"]),
                              float(row["ax"]),
                              float(row["ay"]),
                              float(row["az"]),
                              int(row["mode"])))

    print("-- Executing Z trajectory")
    total_duration = waypoints[-1][0]
    t = 0
    last_mode = 0

    # 监听无人机实际位置
    position_task = asyncio.create_task(log_position_velocity(drone))

    while t <= total_duration:
        current_waypoint = get_current_waypoint(waypoints, t)
        if current_waypoint is None:
            break

        position = current_waypoint[1:4]  # (px, py, pz)
        velocity = current_waypoint[4:7]  # (vx, vy, vz)
        mode_code = current_waypoint[-1]

        if last_mode != mode_code:
            print(f" Mode: {mode_code}, {mode_descriptions.get(mode_code, 'Unknown Mode')}")
            last_mode = mode_code

        await drone.offboard.set_position_velocity_ned(
            PositionNedYaw(*position, 0),
            VelocityNedYaw(*velocity, 0)
        )

        time_step = 0.1
        await asyncio.sleep(time_step)
        t += time_step

    print("-- Z trajectory completed")

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

    # 停止记录位置信息
    position_task.cancel()


if __name__ == "__main__":
    asyncio.run(run())
