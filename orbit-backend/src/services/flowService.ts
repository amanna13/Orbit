import * as fcl from '@onflow/fcl';
import { config } from '@onflow/fcl';
import dotenv from 'dotenv';

dotenv.config();

/**
 * Initialize Flow Client Library (FCL) configuration
 */
export const initializeFCL = (): void => {
  config({
    'accessNode.api': process.env.FLOW_ACCESS_NODE || 'http://127.0.0.1:8888',
    'discovery.wallet': process.env.FCL_WALLET_DISCOVERY || 'http://localhost:8701/fcl/authn',
    'app.detail.title': 'Orbit Backend',
    'app.detail.icon': 'https://placekitten.com/g/200/200',
  });
};

/**
 * Get the current block height from Flow blockchain
 */
export const getCurrentBlock = async (): Promise<unknown> => {
  try {
    const block = await fcl.block();
    return block;
  } catch (error) {
    console.error('Error fetching current block:', error);
    throw error;
  }
};

/**
 * Get account information for a given Flow address
 */
export const getAccount = async (address: string): Promise<unknown> => {
  try {
    const account = await fcl.account(address);
    return account;
  } catch (error) {
    console.error(`Error fetching account ${address}:`, error);
    throw error;
  }
};

/**
 * Execute a Cadence script on Flow blockchain
 */
export const executeScript = async (code: string, args: unknown[] = []): Promise<unknown> => {
  try {
    const result = await fcl.query({
      cadence: code,
      args: (_arg: unknown, _t: unknown) => args,
    });
    return result;
  } catch (error) {
    console.error('Error executing script:', error);
    throw error;
  }
};
