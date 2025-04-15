import numpy as np
import pandas as pd


def generate_z_trajectory_csv():
    z_length = 20
    z_width = 10
    num_segments = 3
    points_per_segment = 50
    duration = 20
    z_height = 5
    total_points = num_segments * points_per_segment
    csv_filename = "z_trajectory.csv"

    t_values = np.linspace(0, duration, total_points)

    px_values = np.concatenate([
        np.linspace(0, z_width, points_per_segment),
        np.linspace(z_width, 0, points_per_segment),
        np.linspace(0, z_width, points_per_segment)
    ])

    py_values = np.concatenate([
        np.linspace(0, z_length / 2, points_per_segment),
        np.linspace(z_length / 2, z_length, points_per_segment),
        np.linspace(z_length, z_length * 1.5, points_per_segment)
    ])

    pz_values = np.full(total_points, -z_height)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(total_points)

    yaw_values = np.zeros(total_points)
    mode_values = np.full(total_points, 70)

    df = pd.DataFrame({
        "idx": range(total_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(total_points),
        "ay": np.zeros(total_points),
        "az": np.zeros(total_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Zigzag trajectory CSV generation completed! File path: {csv_filename}")


def generate_roller_coaster_trajectory_csv():
    num_loops = 2
    track_length = 50
    height = 15
    num_points = 300
    csv_filename = "roller_coaster_trajectory.csv"

    t_values = np.linspace(0, track_length, num_points)

    px_values = t_values
    py_values = 5 * np.sin(2 * np.pi * num_loops * t_values / track_length)
    pz_values = height * (1 - np.cos(2 * np.pi * num_loops * t_values / track_length))

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.gradient(pz_values) / np.gradient(t_values)

    yaw_values = np.zeros(num_points)
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": -pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Roller Coaster trajectory CSV generation completed! File path: {csv_filename}")


def generate_spiral_ascend_trajectory_csv():
    num_turns = 3
    radius = 10
    height = 30
    num_points = 300
    csv_filename = "spiral_ascend_trajectory.csv"
    t_values = np.linspace(0, 1, num_points)

    theta = 2 * np.pi * num_turns * t_values
    px_values = radius * np.cos(theta)
    py_values = radius * np.sin(theta)
    pz_values = height * t_values

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.gradient(pz_values) / np.gradient(t_values)

    yaw_values = np.degrees(theta)
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values * 30,
        "px": px_values,
        "py": py_values,
        "pz": -pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Spiral Ascend trajectory CSV generation completed! File path: {csv_filename}")


def generate_s_shape_trajectory_csv():
    num_points = 300
    duration = 30
    csv_filename = "s_shape_trajectory.csv"
    t_values = np.linspace(0, duration, num_points)

    px_values = np.linspace(0, 30, num_points)
    amplitude = 10
    py_values = amplitude * np.sin(2 * np.pi * t_values / duration)

    z_h = 5
    pz_values = np.full(num_points, -z_h)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(num_points)

    yaw_values = np.degrees(np.arctan2(vy_values, vx_values))
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"S Shape trajectory CSV generation completed! File path: {csv_filename}")


def generate_l_shape_trajectory_csv():
    num_points = 300
    duration = 30
    csv_filename = "l_shape_trajectory.csv"
    t_values = np.linspace(0, duration, num_points)

    half_points = num_points // 2
    px_first = np.linspace(0, 30, half_points)
    py_first = np.full(half_points, 0)
    px_second = np.full(num_points - half_points, 30)
    py_second = np.linspace(0, 30, num_points - half_points)

    px_values = np.concatenate([px_first, px_second])
    py_values = np.concatenate([py_first, py_second])

    z_height = 5
    pz_values = np.full(num_points, -z_height)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(num_points)

    yaw_values = np.degrees(np.arctan2(vy_values, vx_values))
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"L Shape trajectory CSV generation completed! File path: {csv_filename}")


def generate_circle_trajectory_csv():
    num_points = 300
    duration = 30
    csv_filename = "circle_trajectory.csv"
    t_values = np.linspace(0, duration, num_points)

    radius = 15
    theta = np.linspace(0, 2 * np.pi, num_points)
    px_values = radius * np.cos(theta)
    py_values = radius * np.sin(theta)

    z_height = 5
    pz_values = np.full(num_points, -z_height)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(num_points)

    yaw_values = np.degrees(theta + np.pi / 2)
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Circle trajectory CSV generation completed! File path: {csv_filename}")


def generate_triangle_trajectory_csv():
    num_points = 300
    duration = 30
    csv_filename = "triangle_trajectory.csv"
    t_values = np.linspace(0, duration, num_points)

    A = np.array([0, 0])
    B = np.array([30, 0])
    C = np.array([15, 30])

    points_per_segment = num_points // 3
    # A -> B
    seg1_x = np.linspace(A[0], B[0], points_per_segment, endpoint=False)
    seg1_y = np.linspace(A[1], B[1], points_per_segment, endpoint=False)
    # B -> C
    seg2_x = np.linspace(B[0], C[0], points_per_segment, endpoint=False)
    seg2_y = np.linspace(B[1], C[1], points_per_segment, endpoint=False)
    # C -> A
    seg3_x = np.linspace(C[0], A[0], num_points - 2 * points_per_segment)
    seg3_y = np.linspace(C[1], A[1], num_points - 2 * points_per_segment)

    px_values = np.concatenate([seg1_x, seg2_x, seg3_x])
    py_values = np.concatenate([seg1_y, seg2_y, seg3_y])

    z_height = 5
    pz_values = np.full(num_points, -z_height)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(num_points)

    yaw_values = np.degrees(np.arctan2(vy_values, vx_values))
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Triangle trajectory CSV generation completed! File path: {csv_filename}")


def generate_square_trajectory_csv():
    num_points = 300
    duration = 30
    csv_filename = "square_trajectory.csv"
    t_values = np.linspace(0, duration, num_points)

    A = np.array([0, 0])
    B = np.array([30, 0])
    C = np.array([30, 30])
    D = np.array([0, 30])

    points_per_segment = num_points // 4
    # A -> B
    seg1_x = np.linspace(A[0], B[0], points_per_segment, endpoint=False)
    seg1_y = np.linspace(A[1], B[1], points_per_segment, endpoint=False)
    # B -> C
    seg2_x = np.linspace(B[0], C[0], points_per_segment, endpoint=False)
    seg2_y = np.linspace(B[1], C[1], points_per_segment, endpoint=False)
    # C -> D
    seg3_x = np.linspace(C[0], D[0], points_per_segment, endpoint=False)
    seg3_y = np.linspace(C[1], D[1], points_per_segment, endpoint=False)
    # D -> A
    seg4_x = np.linspace(D[0], A[0], num_points - 3 * points_per_segment)
    seg4_y = np.linspace(D[1], A[1], num_points - 3 * points_per_segment)

    px_values = np.concatenate([seg1_x, seg2_x, seg3_x, seg4_x])
    py_values = np.concatenate([seg1_y, seg2_y, seg3_y, seg4_y])

    z_height = 5
    pz_values = np.full(num_points, -z_height)

    vx_values = np.gradient(px_values) / np.gradient(t_values)
    vy_values = np.gradient(py_values) / np.gradient(t_values)
    vz_values = np.zeros(num_points)

    yaw_values = np.degrees(np.arctan2(vy_values, vx_values))
    mode_values = np.full(num_points, 70)

    df = pd.DataFrame({
        "idx": range(num_points),
        "t": t_values,
        "px": px_values,
        "py": py_values,
        "pz": pz_values,
        "vx": vx_values,
        "vy": vy_values,
        "vz": vz_values,
        "ax": np.zeros(num_points),
        "ay": np.zeros(num_points),
        "az": np.zeros(num_points),
        "yaw": yaw_values,
        "mode": mode_values
    })

    df.to_csv(csv_filename, index=False)
    print(f"Square trajectory CSV generation completed! File path: {csv_filename}")


