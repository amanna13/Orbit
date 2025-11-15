import fs from 'fs';
import path from 'path';

type InviteRecord = {
  joinCode: string;
  podID?: number;
  createdAt: string;
  expiresAt?: string | null;
  used?: boolean;
  usedAt?: string | null;
};

const DATA_DIR = path.join(__dirname, '../../data');
const FILE_PATH = path.join(DATA_DIR, 'invites.json');

function ensureDataFile(): void {
  if (!fs.existsSync(DATA_DIR)) {
    fs.mkdirSync(DATA_DIR, { recursive: true });
  }

  if (!fs.existsSync(FILE_PATH)) {
    fs.writeFileSync(FILE_PATH, JSON.stringify({ invites: {} }, null, 2), 'utf8');
  }
}

function readAll(): Record<string, InviteRecord> {
  ensureDataFile();
  const raw = fs.readFileSync(FILE_PATH, 'utf8');
  try {
    const parsed = JSON.parse(raw);
    return parsed.invites || {};
  } catch (err) {
    // If file corrupted, overwrite with empty store
    fs.writeFileSync(FILE_PATH, JSON.stringify({ invites: {} }, null, 2), 'utf8');
    return {};
  }
}

function writeAll(invites: Record<string, InviteRecord>): void {
  ensureDataFile();
  const payload = { invites };
  fs.writeFileSync(FILE_PATH, JSON.stringify(payload, null, 2), 'utf8');
}

export function saveInvite(joinCode: string, podID?: number, ttlSeconds?: number): InviteRecord {
  const invites = readAll();
  const now = new Date();
  const expiresAt = ttlSeconds ? new Date(now.getTime() + ttlSeconds * 1000).toISOString() : null;
  const rec: InviteRecord = {
    joinCode,
    podID,
    createdAt: now.toISOString(),
    expiresAt,
    used: false,
    usedAt: null,
  };
  invites[joinCode] = rec;
  writeAll(invites);
  return rec;
}

export function getInvite(joinCode: string): InviteRecord | null {
  const invites = readAll();
  const rec = invites[joinCode];
  if (!rec) return null;
  // check expiry
  if (rec.expiresAt) {
    const now = new Date();
    if (new Date(rec.expiresAt) < now) {
      // expired: delete and return null
      deleteInvite(joinCode);
      return null;
    }
  }
  return rec;
}

export function markInviteUsed(joinCode: string): InviteRecord | null {
  const invites = readAll();
  const rec = invites[joinCode];
  if (!rec) return null;
  rec.used = true;
  rec.usedAt = new Date().toISOString();
  invites[joinCode] = rec;
  writeAll(invites);
  return rec;
}

export function deleteInvite(joinCode: string): boolean {
  const invites = readAll();
  if (!invites[joinCode]) return false;
  delete invites[joinCode];
  writeAll(invites);
  return true;
}

export function listInvites(): InviteRecord[] {
  const invites = readAll();
  return Object.values(invites);
}

export default {
  saveInvite,
  getInvite,
  markInviteUsed,
  deleteInvite,
  listInvites,
};
