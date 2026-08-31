#!/bin/bash
# Backup HSO Server (src + config + data + sql dump) -> orcale-s3:Standard-10GB
# Chay dinh ky moi ngay, giu 7 ban moi nhat (xoay vong), tu dong xoa ban backup cu tren S3
set -u

REMOTE="orcale-s3:Standard-10GB"
KEEP_DAILY=7
TMP_DIR="/tmp/opencode/hso-backup"
HSO_DIR="/home/ubuntu/hso-server"
LOG_DIR="/home/ubuntu/opencode-data/.logs"
STAMP="$(date +%Y%m%d-%H%M%S)"
NAME="hso-backup-${STAMP}.tar.gz"
SQL_TEMP="${TMP_DIR}/hso2_dump_${STAMP}.sql"

mkdir -p "$TMP_DIR" "$LOG_DIR"

# Chong chay trung lap
exec 9>/tmp/opencode/hso-s3-backup.lock
flock -n 9 || { echo "[$(date '+%F %T')] SKIP: Job dang chay"; exit 0; }

echo "[$(date '+%F %T')] === Start: $NAME ==="

# 1. Dump MySQL Database hso2
echo "[$(date '+%F %T')] Dumping database hso2..."
if ! mysqldump -h 10.0.1.182 -u "cucpro12@gmail.com" -p'VZPeIe=20RJV59={' \
    --single-transaction --quick --set-gtid-purged=OFF hso2 > "$SQL_TEMP" 2>/dev/null; then
    echo "[$(date '+%F %T')] RESULT: FAIL - mysqldump"
    rm -f "$SQL_TEMP"
    exit 1
fi

# 2. Tao file tar.gz gom toan bo hso-server (loai tru target/build cache, file hso.sql cu) va file SQL dump moi
echo "[$(date '+%F %T')] Creating archive..."
if ! tar -czf "$TMP_DIR/$NAME" \
    --exclude='./target' \
    --exclude='./.git' \
    -C "$HSO_DIR" . \
    -C "$TMP_DIR" "$(basename "$SQL_TEMP")"; then
    echo "[$(date '+%F %T')] RESULT: FAIL - tar"
    rm -f "$SQL_TEMP" "$TMP_DIR/$NAME"
    exit 1
fi

rm -f "$SQL_TEMP"
SIZE=$(du -h "$TMP_DIR/$NAME" | cut -f1)

# 3. Upload ban moi len S3 (Standard-10GB)
echo "[$(date '+%F %T')] Uploading to $REMOTE ($SIZE)..."
if ! rclone copy "$TMP_DIR/$NAME" "$REMOTE"; then
    echo "[$(date '+%F %T')] RESULT: FAIL - upload"
    rm -f "$TMP_DIR/$NAME"
    exit 1
fi

# 4. Xoay vong: chi giu KEEP_DAILY ban moi nhat tren S3
echo "[$(date '+%F %T')] Rotating backups (keeping last $KEEP_DAILY) on $REMOTE..."
rclone lsf "$REMOTE" --files-only 2>/dev/null | grep '^hso-backup-' | sort | head -n -"$KEEP_DAILY" | while read -r old_file; do
    [[ -z "$old_file" ]] && continue
    rclone deletefile "$REMOTE/$old_file" >/dev/null 2>&1
    echo "[$(date '+%F %T')] Rotated old backup: $old_file"
done

# Xoa file tam local
rm -f "$TMP_DIR/$NAME"
rm -f /home/ubuntu/.cache/status/s3-size.cache 2>/dev/null || true

echo "[$(date '+%F %T')] RESULT: OK - $NAME ($SIZE) -> $REMOTE (Giu $KEEP_DAILY ban moi nhat)"
