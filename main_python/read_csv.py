import csv
import asyncio
from mavsdk.offboard import PositionNedYaw, VelocityNedYaw


def get_current_waypoint(waypoints, time):
    return next((wp for wp in waypoints if time <= wp[0]), None)


async def execute_trajectory(csv_file, initial_mode, time_step, drone):
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

    waypoints = []
    # 读取 CSV 文件
    with open(csv_file, newline="") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            waypoints.append((
                float(row["t"]),
                float(row["px"]),
                float(row["py"]),
                float(row["pz"]),
                float(row["vx"]),
                float(row["vy"]),
                float(row["vz"]),
                float(row["ax"]),
                float(row["ay"]),
                float(row["az"]),
                int(row["mode"])
            ))

    print(f"-- Executing trajectory from {csv_file}")
    total_duration = waypoints[-1][0]
    t = 0
    last_mode = initial_mode

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

        await asyncio.sleep(time_step)
        t += time_step


async def execute_trajectory_other(csv_file, drone):
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

    waypoints = []
    # 读取 CSV 文件
    with open(csv_file, newline="") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            waypoints.append((
                float(row["t"]),
                float(row["px"]),
                float(row["py"]),
                float(row["pz"]),
                float(row["vx"]),
                float(row["vy"]),
                float(row["vz"]),
                float(row["ax"]),
                float(row["ay"]),
                float(row["az"]),
                int(row["mode"])
            ))

    print(f"-- Executing trajectory from {csv_file}")
    total_duration = waypoints[-1][0]
    t = 0
    last_mode = 0

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
