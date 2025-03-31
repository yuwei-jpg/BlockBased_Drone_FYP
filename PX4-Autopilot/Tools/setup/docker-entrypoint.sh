#!/bin/bash

echo "[docker-entrypoint.sh] Starting entrypoint"
# Start virtual X server in the background
# - DISPLAY default is :99, set in dockerfile
# - Users can override with `-e DISPLAY=` in `docker run` command to avoid
#   running Xvfb and attach their screen
if [[ -x "$(command -v Xvfb)" && "$DISPLAY" == ":99" ]]; then
	echo "[docker-entrypoint.sh] Starting Xvfb"
	Xvfb :99 -screen 0 1600x1200x24+32 &
fi

# Check if the ROS_DISTRO is passed and use it
# to source the ROS environment
if [ -n "${ROS_DISTRO}" ]; then
	echo "[docker-entrypoint.sh] ROS: ${ROS_DISTRO}"
	source "/opt/ros/$ROS_DISTRO/setup.bash"
fi

echo "[docker-entrypoint.sh] Working Directory: ${pwd}"

# Use the LOCAL_USER_ID if passed in at runtime
if [ -n "${LOCAL_USER_ID}" ]; then
	echo "[docker-entrypoint.sh] Starting with: $LOCAL_USER_ID:user"
	# modify existing user's id
	usermod -u $LOCAL_USER_ID user

	# run as user
	# exec gosu user "$@"
	exec "$@"
else
	exec "$@"
fi
