import { fcl } from '../utils/flowConfig';
import * as t from '@onflow/types';
import { logger } from '../utils/logger';
import fs from 'fs';
import path from 'path';

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
  creatorAddress: string,
  role: string
): Promise<string> => {
  try {
    const code = readCadenceFile('transactions/createPod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [
        arg(name, t.String),
        arg(creatorAddress, t.Address),
        arg(role, t.String),
      ],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(`Pod created successfully. Transaction ID: ${transactionId}`);
    return transactionId;
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
    const code = readCadenceFile('transactions/JoinPod.cdc');

    const transactionId = await fcl.mutate({
      cadence: code,
      args: (arg, _t) => [arg(joinCode, t.String)],
      proposer: authz,
      payer: authz,
      authorizations: [authz],
      limit: 9999,
    });

    logger.info(`User ${signerAddress} joined pod successfully. Transaction ID: ${transactionId}`);
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
