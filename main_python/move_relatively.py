# drone_movement.py
import asyncio
import math


async def move_relative(drone, forward, right, up, yaw=0):
    """
    将相对位移（前进/右移/上升）转换为 GPS 坐标并执行飞行
    :param drone: MAVSDK System() 实例
    :param forward: 向前移动的距离（米）
    :param right: 向右移动的距离（米）
    :param up: 上升的高度（米）
    :param yaw: 机头方向（默认 0 度，即朝北）
    """

    # 获取当前无人机 GPS 位置
    async for position in drone.telemetry.position():
        current_lat = position.latitude_deg
        current_lon = position.longitude_deg
        current_alt = position.absolute_altitude_m
        break  # 只获取一次

    # 地球半径（单位：米）
    R = 6378137

    # 计算新的纬度
    new_lat = current_lat + (forward / 111320) * 100000

    # 计算新的经度
    new_lon = current_lon + (right / (111320 * math.cos(math.radians(current_lat)))) * 100000

    # 计算新的高度
    new_alt = current_alt + up

    print(f"移动前: {current_lat}, {current_lon}, {current_alt}")
    print(f"移动后: {new_lat}, {new_lon}, {new_alt}")

    # 发送新的 GPS 坐标到无人机
    await drone.action.goto_location(new_lat, new_lon, new_alt, yaw)
    await asyncio.sleep(10)
