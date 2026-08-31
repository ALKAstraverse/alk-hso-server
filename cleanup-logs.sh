#!/bin/bash
# Safe cleanup script for HSO Server
# Run via cron to manage old log files only.
# DO NOT DELETE msg_* or other data files as they are required game assets.

APP_DIR="/app"

echo "[$(date)] Starting safe cleanup for HSO Server..."

# Remove log files older than 7 days if any exist
find "$APP_DIR" -name "*.log" -type f -mtime +7 -delete 2>/dev/null
echo "[$(date)] Cleaned old log files (>7 days)"

# Clean any temporary crash dumps or heap dumps if generated
find "$APP_DIR" \( -name "hs_err_pid*.log" -o -name "replay_pid*.log" \) -type f -mtime +3 -delete 2>/dev/null

echo "[$(date)] Cleanup completed"