package com.langlearn.app.util.spacedrepetition

import com.langlearn.app.data.database.entity.FlashcardReviewEntity

object Sm2Algorithm {

    fun calculate(quality: Int, current: FlashcardReviewEntity): FlashcardReviewEntity {
        val correctedQuality = quality.coerceIn(0, 5)
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        val newEaseFactor = maxOf(
            1.3,
            current.easeFactor + (0.1 - (5 - correctedQuality) * (0.08 + (5 - correctedQuality) * 0.02))
        )

        return if (correctedQuality >= 3) {
            val newInterval = when (current.repetitions) {
                0 -> 1
                1 -> 6
                else -> (current.interval * current.easeFactor).toInt()
            }
            current.copy(
                repetitions = current.repetitions + 1,
                easeFactor = newEaseFactor,
                interval = newInterval,
                lastReviewDate = now,
                nextReviewDate = now + newInterval * oneDayMillis
            )
        } else {
            current.copy(
                repetitions = 0,
                easeFactor = newEaseFactor,
                interval = 1,
                lastReviewDate = now,
                nextReviewDate = now + oneDayMillis
            )
        }
    }
}
