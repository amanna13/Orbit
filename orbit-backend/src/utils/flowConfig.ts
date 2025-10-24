import * as fcl from '@onflow/fcl';
import dotenv from 'dotenv';

// Load environment variables
dotenv.config();

// Configure FCL
fcl.config({
  'accessNode.api': process.env.FLOW_ACCESS_NODE,
  'discovery.wallet': process.env.FCL_WALLET_DISCOVERY,
  '0xFungibleToken': '0xee82856bf20e2aa6', // emulator FT address
  '0xFlowToken': '0x0ae53cb6e3f42a79', // emulator FlowToken address
  '0xForte': '0xf8d6e0586b0a20c7', // your contract deployer address (update if needed)
});

/**
 * Get the latest block from Flow blockchain
 * @returns Promise with the latest block information
 */
export const getLatestBlock = async (): Promise<unknown> => {
  const response = await fcl.send([fcl.getBlock(true)]);
  return fcl.decode(response);
};

// Export the configured fcl instance
export { fcl };
