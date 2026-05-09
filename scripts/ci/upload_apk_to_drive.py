import hashlib
import io
import json
import os
from datetime import datetime, timezone

from google.oauth2 import service_account
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload, MediaIoBaseDownload, MediaIoBaseUpload

SCOPES = ["https://www.googleapis.com/auth/drive"]


def sha256_file(path: str) -> str:
    digest = hashlib.sha256()
    with open(path, "rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def drive_service():
    credentials = service_account.Credentials.from_service_account_file(
        os.environ["GOOGLE_APPLICATION_CREDENTIALS"], scopes=SCOPES
    )
    return build("drive", "v3", credentials=credentials, cache_discovery=False)


def find_file(service, folder_id: str, name: str):
    escaped_name = name.replace("'", "\\'")
    query = (
        f"name = '{escaped_name}' and "
        f"'{folder_id}' in parents and trashed = false"
    )
    response = service.files().list(
        q=query,
        spaces="drive",
        fields="files(id,name,webViewLink)",
        includeItemsFromAllDrives=True,
        supportsAllDrives=True,
        pageSize=1,
    ).execute()
    files = response.get("files", [])
    return files[0] if files else None


def verify_folder_access(service, folder_id: str):
    try:
        info = service.files().get(
            fileId=folder_id,
            fields="id,name,mimeType",
            supportsAllDrives=True,
        ).execute()
    except HttpError as exc:
        status = getattr(exc.resp, "status", None)
        if status == 404:
            raise RuntimeError("Drive folder not found or not shared with the CI account. Check GDRIVE_FOLDER_ID and folder sharing.") from exc
        if status in (401, 403):
            raise RuntimeError("Drive denied folder access for the CI account. Check sharing and Drive API access.") from exc
        raise
    if info.get("mimeType") != "application/vnd.google-apps.folder":
        raise RuntimeError("GDRIVE_FOLDER_ID is not a folder.")


def download_text(service, file_id: str) -> str:
    request = service.files().get_media(fileId=file_id, supportsAllDrives=True)
    buffer = io.BytesIO()
    downloader = MediaIoBaseDownload(buffer, request)
    done = False
    while not done:
        _, done = downloader.next_chunk()
    return buffer.getvalue().decode("utf-8")


def upload_or_replace_text(service, folder_id: str, name: str, text: str):
    existing = find_file(service, folder_id, name)
    media = MediaIoBaseUpload(io.BytesIO(text.encode("utf-8")), mimetype="application/json", resumable=False)
    body = {"name": name, "parents": [folder_id]}
    if existing:
        return service.files().update(
            fileId=existing["id"],
            media_body=media,
            body={"name": name},
            fields="id,name",
            supportsAllDrives=True,
        ).execute()
    return service.files().create(
        body=body,
        media_body=media,
        fields="id,name",
        supportsAllDrives=True,
    ).execute()


def ensure_public_reader(service, file_id: str):
    service.permissions().create(
        fileId=file_id,
        body={"type": "anyone", "role": "reader"},
        supportsAllDrives=True,
    ).execute()


def write_output(name: str, value: str):
    output_path = os.environ.get("GITHUB_OUTPUT")
    if not output_path:
        return
    with open(output_path, "a", encoding="utf-8") as handle:
        handle.write(f"{name}={value}\n")


def main():
    folder_id = os.environ["GDRIVE_FOLDER_ID"]
    apk_path = os.environ["APK_PATH"]
    apk_name = os.environ["APK_NAME"]
    meta_name = os.environ["DRIVE_META_NAME"]
    commit_sha = os.environ["GITHUB_SHA"]
    branch = os.environ["GITHUB_REF_NAME"]
    run_id = os.environ["GITHUB_RUN_ID"]
    repository = os.environ["GITHUB_REPOSITORY"]

    service = drive_service()
    verify_folder_access(service, folder_id)
    apk_hash = sha256_file(apk_path)
    meta = {}

    meta_file = find_file(service, folder_id, meta_name)
    if meta_file:
        try:
            meta = json.loads(download_text(service, meta_file["id"]))
        except Exception:
            meta = {}

    if meta.get("sha256") == apk_hash and meta.get("drive_link"):
        write_output("uploaded", "false")
        write_output("sha256", apk_hash)
        write_output("drive_link", meta["drive_link"])
        write_output("file_name", meta.get("file_name", apk_name))
        return

    timestamp = datetime.now(timezone.utc).strftime("%Y%m%d-%H%M%S")
    short_sha = commit_sha[:7]
    file_name = f"NoveoKotlin-{branch}-{short_sha}-{timestamp}.apk"

    media = MediaFileUpload(apk_path, mimetype="application/vnd.android.package-archive", resumable=False)
    try:
        created = service.files().create(
            body={"name": file_name, "parents": [folder_id]},
            media_body=media,
            fields="id,name,webViewLink",
            supportsAllDrives=True,
        ).execute()
    except HttpError as exc:
        status = getattr(exc.resp, "status", None)
        if status == 404:
            raise RuntimeError("Drive upload target not found. Check GDRIVE_FOLDER_ID and folder sharing.") from exc
        if status in (401, 403):
            raise RuntimeError("Drive rejected upload permission for the CI account.") from exc
        raise

    ensure_public_reader(service, created["id"])
    file_info = service.files().get(
        fileId=created["id"],
        fields="id,name,webViewLink,webContentLink",
        supportsAllDrives=True,
    ).execute()

    drive_link = file_info.get("webContentLink") or file_info.get("webViewLink") or f"https://drive.google.com/file/d/{created['id']}/view"
    run_url = f"https://github.com/{repository}/actions/runs/{run_id}"

    latest_meta = {
        "sha256": apk_hash,
        "file_id": created["id"],
        "file_name": file_info.get("name", file_name),
        "drive_link": drive_link,
        "commit_sha": commit_sha,
        "branch": branch,
        "run_url": run_url,
        "uploaded_at": datetime.now(timezone.utc).isoformat(),
    }
    upload_or_replace_text(service, folder_id, meta_name, json.dumps(latest_meta, indent=2))

    write_output("uploaded", "true")
    write_output("sha256", apk_hash)
    write_output("drive_link", drive_link)
    write_output("file_name", file_info.get("name", file_name))


if __name__ == "__main__":
    main()
