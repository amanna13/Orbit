import { Request, Response } from 'express';
import {
  createScheduledPayment,
  cancelScheduledPayment,
  triggerImmediatePayment,
  listScheduledPayments,
  getScheduledPaymentDetails,
  updateScheduledPayment,
} from '../services/forteService';
import { logger } from '../utils/logger';

/**
 * Controller to create a scheduled payment
 */
export const handleCreateScheduledPayment = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { payerPodID, receiverPodID, amount, intervalDays } = req.body;

    if (
      payerPodID === undefined ||
      receiverPodID === undefined ||
      amount === undefined ||
      intervalDays === undefined
    ) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: payerPodID, receiverPodID, amount, intervalDays',
      });
      return;
    }

    const result = await createScheduledPayment(
      Number(payerPodID),
      Number(receiverPodID),
      Number(amount),
      Number(intervalDays)
    );

    if (result.success) {
      res.status(201).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleCreateScheduledPayment', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};

/**
 * Controller to cancel a scheduled payment
 */
export const handleCancelScheduledPayment = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { scheduleID } = req.params;

    if (!scheduleID) {
      res.status(400).json({
        success: false,
        error: 'Missing required parameter: scheduleID',
      });
      return;
    }

    const result = await cancelScheduledPayment(scheduleID);

    if (result.success) {
      res.status(200).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleCancelScheduledPayment', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};

/**
 * Controller to trigger an immediate payment
 */
export const handleTriggerImmediatePayment = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { payerPodID, receiverPodID, amount } = req.body;

    if (payerPodID === undefined || receiverPodID === undefined || amount === undefined) {
      res.status(400).json({
        success: false,
        error: 'Missing required fields: payerPodID, receiverPodID, amount',
      });
      return;
    }

    const result = await triggerImmediatePayment(
      Number(payerPodID),
      Number(receiverPodID),
      Number(amount)
    );

    if (result.success) {
      res.status(200).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleTriggerImmediatePayment', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};

/**
 * Controller to list scheduled payments for a pod
 */
export const handleListScheduledPayments = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { podID } = req.query;

    if (!podID) {
      res.status(400).json({
        success: false,
        error: 'Missing required query parameter: podID',
      });
      return;
    }

    const result = await listScheduledPayments(Number(podID));

    if (result.success) {
      res.status(200).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleListScheduledPayments', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};

/**
 * Controller to get details of a scheduled payment
 */
export const handleGetScheduledPaymentDetails = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { scheduleID } = req.params;

    if (!scheduleID) {
      res.status(400).json({
        success: false,
        error: 'Missing required parameter: scheduleID',
      });
      return;
    }

    const result = await getScheduledPaymentDetails(scheduleID);

    if (result.success) {
      res.status(200).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleGetScheduledPaymentDetails', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};

/**
 * Controller to update a scheduled payment
 */
export const handleUpdateScheduledPayment = async (
  req: Request,
  res: Response
): Promise<void> => {
  try {
    const { scheduleID } = req.params;
    const { amount, intervalDays } = req.body;

    if (!scheduleID) {
      res.status(400).json({
        success: false,
        error: 'Missing required parameter: scheduleID',
      });
      return;
    }

    if (amount === undefined && intervalDays === undefined) {
      res.status(400).json({
        success: false,
        error: 'At least one field must be provided: amount or intervalDays',
      });
      return;
    }

    const updates: { amount?: number; intervalDays?: number } = {};
    if (amount !== undefined) updates.amount = Number(amount);
    if (intervalDays !== undefined) updates.intervalDays = Number(intervalDays);

    const result = await updateScheduledPayment(scheduleID, updates);

    if (result.success) {
      res.status(200).json(result);
    } else {
      res.status(500).json(result);
    }
  } catch (error) {
    logger.error('Error in handleUpdateScheduledPayment', error);
    res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }
};
