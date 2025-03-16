# drone_movement.py
import asyncio
import math


async def move_relative(drone, forward, right, up, yaw=0):
    """
    Convert relative displacement (forward/right/up) to GPS coordinates and perform flight
    :param drone: MAVSDK System() instance
    :param forward: Distance to move forward (meters)
    :param right: Distance to move right (meters)
    :param up: Height to rise (meters)
    :param yaw: Head direction (default 0 degrees, i.e. north)

    """

    async for position in drone.telemetry.position():
        current_lat = position.latitude_deg
        current_lon = position.longitude_deg
        current_alt = position.absolute_altitude_m
        break

    R = 6378137

    new_lat = current_lat + (forward / 111320) * 100000

    new_lon = current_lon + (right / (111320 * math.cos(math.radians(current_lat)))) * 100000

    new_alt = current_alt + up

    print(f"移动前: {current_lat}, {current_lon}, {current_alt}")
    print(f"移动后: {new_lat}, {new_lon}, {new_alt}")

    await drone.action.goto_location(new_lat, new_lon, new_alt, yaw)
    await asyncio.sleep(10)
