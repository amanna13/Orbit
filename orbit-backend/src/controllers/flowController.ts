import { Request, Response } from 'express';
import { getCurrentBlock, getAccount } from '../services/flowService';
import { logger } from '../utils/logger';

/**
 * Get current Flow blockchain block information
 */
export const getBlockInfo = async (_req: Request, res: Response): Promise<void> => {
  try {
    const block = await getCurrentBlock();
    logger.info('Successfully fetched current block');
    res.status(200).json({
      success: true,
      data: block,
    });
  } catch (error) {
    logger.error('Failed to fetch block info', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch block information',
    });
  }
};

/**
 * Get Flow account information by address
 */
export const getAccountInfo = async (req: Request, res: Response): Promise<void> => {
  try {
    const { address } = req.params;

    if (!address) {
      res.status(400).json({
        success: false,
        error: 'Address parameter is required',
      });
      return;
    }

    const account = await getAccount(address);
    logger.info(`Successfully fetched account info for ${address}`);
    res.status(200).json({
      success: true,
      data: account,
    });
  } catch (error) {
    logger.error('Failed to fetch account info', error);
    res.status(500).json({
      success: false,
      error: 'Failed to fetch account information',
    });
  }
};
