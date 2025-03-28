
import asyncio
import csv
from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw,VelocityBodyYawspeed,Attitude,AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)

async def run():
    """ Does Offboard control using position NED coordinates. """
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
            print(f"-- Connected to drone!")
            break

    print("Waiting for drone to have a global position estimate...")
    async for health in drone.telemetry.health():
        if health.is_global_position_ok and health.is_home_position_ok:
            print("-- Global position estimate OK")
            break

        # Here is generated code
    radius = float(2)
    speed = float(3)
    await drone.action.arm()
    await drone.action.set_takeoff_altitude(20)
    await drone.action.takeoff()
    await asyncio.sleep(10)
    
    async for position1 in drone.telemetry.position():
        if position1.relative_altitude_m >= 15:
            
            async for position in drone.telemetry.position():
                latitude = position.latitude_deg
                longitude = position.longitude_deg
                altitude = position.absolute_altitude_m
                break  
            await drone.action.do_orbit(radius,speed, OrbitYawBehavior.HOLD_INITIAL_HEADING, latitude, longitude, altitude)
            await asyncio.sleep(10)
    
            break
        else:
            await drone.action.return_to_launch()
            await asyncio.sleep(10)
    
            break
    await drone.action.land()
      # Make sure all lines have only 4 spaces indented

# execute run() function
asyncio.run(run())
