import { fcl } from '../utils/flowConfig';
import * as t from '@onflow/types';
import { logger } from '../utils/logger';
import fs from 'fs';
import path from 'path';
import crypto from 'crypto';
import inviteStore from '../utils/inviteStore';

/**
 * Helper function to read Cadence files
 */
const readCadenceFile = (filename: string): string => {
  const filePath = path.join(__dirname, '../../cadence', filename);
  return fs.readFileSync(filePath, 'utf8');
};

// Note: fcl.authz is used as a placeholder for authorization.
// In production, you'll need to implement proper authorization functions
// using service account private keys or user wallet signatures.
const authz = fcl.authz as any; // Type assertion to bypass FCL type issues

/**
 * Create a new Pod
 * @param name - Name of the pod
 * @param creatorAddress - Address of the pod creator
 * @param role - Role of the creator in the pod
 * @returns Transaction ID
 */
export const createPod = async (
  name: string,
  _creatorAddress: string,
  _role: string
): Promise<{ transactionId: string; podID?: number; joinCode: string }> => {
  try {
    // Generate a short random join code and compute its sha3-256 hash.
    // joinCode: plaintext returned to caller (shown in QR/scan). joinHash stored on-chain.
    const joinCode = crypto.randomBytes(4).toString('hex'); // 8 hex chars
    let joinHash: string;
    try {
      joinHash = crypto.createHash('sha3-256').update(joinCode).digest('hex');
    } catch (err) {
      // Fallback to sha256 if sha3-256 is not available in the runtime OpenSSL.
      joinHash = crypto.createHash('sha256').update(joinCode).digest('hex');
      logger.warn('sha3-256 not supported, falling back to sha256 for joinHash');
    }

    const code = readCadenceFile('transactions/CreatePod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(name, t.String), arg(joinHash, t.String)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    // Wait for the transaction to be sealed and extract the PodCreated event to get podID
    const sealed = await fcl.tx(transactionId).onceSealed();
    let podID: number | undefined = undefined;
    if (sealed && sealed.events) {
      for (const e of sealed.events) {
        // event.type could be like "A.<address>.PodContract.PodCreated"
        if (e.type && e.type.includes('PodCreated')) {
          // event.data.id should hold the UInt64 id
          const rawId = e.data && (e.data as any).id;
          if (rawId !== undefined && rawId !== null) {
            podID = Number(rawId as any);
            break;
          }
        }
      }
    }

    // Persist the plaintext join code in the local invite store for demo/fallback.
    try {
      // Save invites for 7 days by default (604800 seconds)
      inviteStore.saveInvite(joinCode, podID, 60 * 60 * 24 * 7);
    } catch (err) {
      logger.warn('Failed to persist join code to invite store', err);
    }

    logger.info(`Pod created successfully. Transaction ID: ${transactionId}`);
    return { transactionId, podID, joinCode };
  } catch (error) {
    logger.error('Failed to create pod', error);
    throw new Error(`Failed to create pod: ${error}`);
  }
};

/**
 * Join an existing Pod using a join code
 * @param joinCode - The join code for the pod
 * @param signerAddress - Address of the user joining
 * @returns Transaction ID
 */
export const joinPod = async (joinCode: string, signerAddress: string): Promise<string> => {
  try {
    // Compute hash of supplied joinCode (must match storage hash)
    let joinHash: string;
    try {
      joinHash = crypto.createHash('sha3-256').update(joinCode).digest('hex');
    } catch (err) {
      joinHash = crypto.createHash('sha256').update(joinCode).digest('hex');
    }

    // First, check local invite store as a fast fallback/demo store.
    let podID: number | undefined;
    try {
      const invite = inviteStore.getInvite(joinCode);
      if (invite && invite.podID !== undefined) {
        podID = invite.podID;
      }
    } catch (err) {
      logger.warn('Failed to read invite store', err);
    }

    // If not found locally, query on-chain for podID by joinHash
    if (podID === undefined) {
      const lookup = readCadenceFile('scripts/GetPodIDByJoinHash.cdc');
      const podIdResult = await fcl.query({
        cadence: lookup,
        args: (arg, _t) => [arg(joinHash, t.String)],
      });

      if (podIdResult === null || podIdResult === undefined) {
        throw new Error('Invalid join code');
      }

      podID = Number(podIdResult);
    }

    // Call the JoinPod transaction with the resolved podID
    const code = readCadenceFile('transactions/JoinPod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(podID.toString(), t.UInt64)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    // mark the invite as used in the local store (best-effort)
    try {
      inviteStore.markInviteUsed(joinCode);
    } catch (err) {
      logger.warn('Failed to mark invite as used in invite store', err);
    }

    logger.info(`User ${signerAddress} joined pod ${podID} successfully. Transaction ID: ${transactionId}`);
    return transactionId;
  } catch (error) {
    logger.error('Failed to join pod', error);
    throw new Error(`Failed to join pod: ${error}`);
  }
};

/**
 * Leave a Pod
 * @param podID - ID of the pod to leave
 * @param signerAddress - Address of the user leaving
 * @returns Transaction ID
 */
export const leavePod = async (podID: number, signerAddress: string): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/LeavePod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(podID.toString(), t.UInt64)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(
      `User ${signerAddress} left pod ${podID} successfully. Transaction ID: ${transactionId}`
    );
    return transactionId;
  } catch (error) {
    logger.error('Failed to leave pod', error);
    throw new Error(`Failed to leave pod: ${error}`);
  }
};

/**
 * Get all Pods
 * @returns Array of all pods
 */
export const getAllPods = async (): Promise<unknown> => {
  try {
    const code = readCadenceFile('scripts/GetAllPods.cdc');

    const result = await fcl.query({
      cadence: code,
      args: (_arg, _t) => [],
    });

    logger.info('Retrieved all pods successfully');
    return result;
  } catch (error) {
    logger.error('Failed to get all pods', error);
    throw new Error(`Failed to get all pods: ${error}`);
  }
};

/**
 * Get details of a specific Pod
 * @param podID - ID of the pod
 * @returns Pod details
 */
export const getPodDetails = async (podID: number): Promise<unknown> => {
  try {
    const code = readCadenceFile('scripts/GetPodDetails.cdc');

    const result = await fcl.query({
      cadence: code,
      args: (arg, _t) => [arg(podID.toString(), t.UInt64)],
    });

    logger.info(`Retrieved pod ${podID} details successfully`);
    return result;
  } catch (error) {
    logger.error(`Failed to get pod ${podID} details`, error);
    throw new Error(`Failed to get pod details: ${error}`);
  }
};

/**
 * Transfer tokens between Pods
 * @param senderPodID - ID of the sender pod
 * @param receiverPodID - ID of the receiver pod
 * @param amount - Amount to transfer
 * @returns Transaction ID
 */
export const transferBetweenPods = async (
  senderPodID: number,
  receiverPodID: number,
  amount: number
): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/TransferBetweenPods.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [
        arg(senderPodID.toString(), t.UInt64),
        arg(receiverPodID.toString(), t.UInt64),
        arg(amount.toFixed(8), t.UFix64),
      ],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(
      `Transfer from pod ${senderPodID} to pod ${receiverPodID} successful. Transaction ID: ${transactionId}`
    );
    return transactionId;
  } catch (error) {
    logger.error('Failed to transfer between pods', error);
    throw new Error(`Failed to transfer between pods: ${error}`);
  }
};

/**
 * Get Pods by User Address
 * @param userAddress - Address of the user
 * @returns Array of pods the user is a member of
 */
export const getPodsByAddress = async (userAddress: string): Promise<unknown> => {
  try {
    const code = readCadenceFile('scripts/GetPodsByAddress.cdc');

    const result = await fcl.query({
      cadence: code,
      args: (arg, _t) => [arg(userAddress, t.Address)],
    });

    logger.info(`Retrieved pods for address ${userAddress} successfully`);
    return result;
  } catch (error) {
    logger.error(`Failed to get pods for address ${userAddress}`, error);
    throw new Error(`Failed to get pods by address: ${error}`);
  }
};

/**
 * Deposit tokens to a Pod
 * @param podID - ID of the pod
 * @param amount - Amount to deposit
 * @param signerAddress - Address of the depositor
 * @returns Transaction ID
 */
export const depositToPod = async (
  podID: number,
  amount: number,
  signerAddress: string
): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/DepositToPod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [
        arg(podID.toString(), t.UInt64),
        arg(amount.toFixed(8), t.UFix64),
      ],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(
      `Deposit of ${amount} to pod ${podID} from ${signerAddress} successful. Transaction ID: ${transactionId}`
    );
    return transactionId;
  } catch (error) {
    logger.error('Failed to deposit to pod', error);
    throw new Error(`Failed to deposit to pod: ${error}`);
  }
};

/**
 * Distribute Pod Funds to Members
 * @param podID - ID of the pod
 * @param signerAddress - Address of the member initiating distribution
 * @returns Transaction ID
 */
export const distributePodFunds = async (
  podID: number,
  signerAddress: string
): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/DistributePodFundsToMembers.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(podID.toString(), t.UInt64)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(
      `Distribution of pod ${podID} funds initiated by ${signerAddress}. Transaction ID: ${transactionId}`
    );
    return transactionId;
  } catch (error) {
    logger.error('Failed to distribute pod funds', error);
    throw new Error(`Failed to distribute pod funds: ${error}`);
  }
};

/**
 * Execute Pod Disbursement to Sinks
 * @param podID - ID of the pod
 * @param triggerType - "manual" or "flasher"
 * @param signerAddress - Address of the member initiating disbursement
 * @returns Transaction ID
 */
export const executePodDisbursement = async (
  podID: number,
  triggerType: string,
  signerAddress: string
): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/ExecutePodDisbursement.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(podID.toString(), t.UInt64), arg(triggerType, t.String)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(
      `Disbursement of pod ${podID} executed (${triggerType}) by ${signerAddress}. Transaction ID: ${transactionId}`
    );
    return transactionId;
  } catch (error) {
    logger.error('Failed to execute pod disbursement', error);
    throw new Error(`Failed to execute pod disbursement: ${error}`);
  }
};

/**
 * Get Flow Token Balance of an Account
 * @param address - Address to check balance for
 * @returns Flow token balance as a number
 */
export const getFlowBalance = async (address: string): Promise<number> => {
  try {
    const code = readCadenceFile('scripts/GetFlowBalance.cdc');

    const balance = await fcl.query({
      cadence: code,
      args: (arg, _t) => [arg(address, t.Address)],
    });

    logger.info(`Retrieved Flow balance for address ${address}: ${balance}`);
    return Number(balance);
  } catch (error) {
    logger.error(`Failed to get Flow balance for address ${address}`, error);
    throw new Error(`Failed to get Flow balance: ${error}`);
  }
};
