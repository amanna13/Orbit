import { Request, Response } from 'express';
import {
  createPod,
  joinPod,
  leavePod,
  getAllPods,
  getPodDetails,
  transferBetweenPods,
} from '../services/flowService';
import { logger } from '../utils/logger';

/**
 * Controller to create a new Pod
 */
export const handleCreatePod = async (req: Request, res: Response): Promise<void> => {
  try {
    const { name, creatorAddress, role } = req.body;

    if (!name || !creatorAddress || !role) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: name, creatorAddress, role',
      });
      return;
    }

    const transactionId = await createPod(name, creatorAddress, role);
    logger.info(`Pod created with transaction ID: ${transactionId}`);

    res.status(201).json({
      success: true,
      transactionId,
      message: 'Pod created successfully',
    });
  } catch (error) {
    logger.error('Failed to create pod', error);
    res.status(500).json({
      success: false,
      error: 'Failed to create pod',
    });
  }
};

/**
 * Controller to join a Pod
 */
export const handleJoinPod = async (req: Request, res: Response): Promise<void> => {
  try {
    const { joinCode, signerAddress } = req.body;

    if (!joinCode || !signerAddress) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: joinCode, signerAddress',
      });
      return;
    }

    const transactionId = await joinPod(joinCode, signerAddress);
    logger.info(`User joined pod with transaction ID: ${transactionId}`);

    res.status(200).json({
      success: true,
      transactionId,
      message: 'Successfully joined pod',
    });
  } catch (error) {
    logger.error('Failed to join pod', error);
    res.status(500).json({
      success: false,
      error: 'Failed to join pod',
    });
  }
};

/**
 * Controller to leave a Pod
 */
export const handleLeavePod = async (req: Request, res: Response): Promise<void> => {
  try {
    const { podID, signerAddress } = req.body;

    if (podID === undefined || !signerAddress) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: podID, signerAddress',
      });
      return;
    }

    const transactionId = await leavePod(Number(podID), signerAddress);
    logger.info(`User left pod with transaction ID: ${transactionId}`);

    res.status(200).json({
      success: true,
      transactionId,
      message: 'Successfully left pod',
    });
  } catch (error) {
    logger.error('Failed to leave pod', error);
    res.status(500).json({
      success: false,
      error: 'Failed to leave pod',
    });
  }
};

/**
 * Controller to get all Pods
 */
export const handleGetAllPods = async (_req: Request, res: Response): Promise<void> => {
  try {
    const pods = await getAllPods();
    logger.info('Successfully retrieved all pods');

    res.status(200).json({
      success: true,
      data: pods,
    });
  } catch (error) {
    logger.error('Failed to get all pods', error);
    res.status(500).json({
      success: false,
      error: 'Failed to retrieve pods',
    });
  }
};

/**
 * Controller to get Pod details
 */
export const handleGetPodDetails = async (req: Request, res: Response): Promise<void> => {
  try {
    const { podID } = req.params;

    if (!podID) {
      res.status(400).json({
        success: false,
        error: 'Missing required parameter: podID',
      });
      return;
    }

    const podDetails = await getPodDetails(Number(podID));
    logger.info(`Successfully retrieved pod ${podID} details`);

    res.status(200).json({
      success: true,
      data: podDetails,
    });
  } catch (error) {
    logger.error('Failed to get pod details', error);
    res.status(500).json({
      success: false,
      error: 'Failed to retrieve pod details',
    });
  }
};

/**
 * Controller to transfer between Pods
 */
export const handleTransferBetweenPods = async (req: Request, res: Response): Promise<void> => {
  try {
    const { senderPodID, receiverPodID, amount } = req.body;

    if (senderPodID === undefined || receiverPodID === undefined || amount === undefined) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: senderPodID, receiverPodID, amount',
      });
      return;
    }

    const transactionId = await transferBetweenPods(
      Number(senderPodID),
      Number(receiverPodID),
      Number(amount)
    );
    logger.info(`Transfer completed with transaction ID: ${transactionId}`);

    res.status(200).json({
      success: true,
      transactionId,
      message: 'Transfer completed successfully',
    });
  } catch (error) {
    logger.error('Failed to transfer between pods', error);
    res.status(500).json({
      success: false,
      error: 'Failed to complete transfer',
    });
  }
};
